package com.kmecpp.spongecore.config;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;

import com.kmecpp.jlib.object.ObjectValue;
import com.kmecpp.spongecore.SpongePlugin;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private static List<ConfigKey> keys;

	private static ConfigurationLoader<CommentedConfigurationNode> loader;
	private static CommentedConfigurationNode root;

	/**
	 * Reloads the configuration file
	 */
	public static void reload() {
		try {
			loader = Sponge.getGame().getConfigManager().getPluginConfig(SpongePlugin.getPlugin()).getConfig();

			SpongeConfig spongeConfig = SpongePlugin.getConfig().getAnnotation(SpongeConfig.class);
			if (spongeConfig != null) {
				root = loader.load(ConfigurationOptions.defaults().setHeader(spongeConfig.header()));
			}

			keys = Arrays.stream(SpongePlugin.getConfig().getDeclaredFields())
					.filter((field) -> Modifier.isStatic(field.getModifiers()) && field.getType() == ConfigKey.class)
					.map((field) -> {
						try {
							field.setAccessible(true);
							return (ConfigKey) field.get(null);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					})
					.collect(Collectors.toList());

			keys.forEach((key) -> {
				CommentedConfigurationNode node = root.getNode(key.getKey());
				if (node.isVirtual()) {
					node.setValue(key.getValue().asObject()); //Set value before comment
				} else {
					key.setValue(node.getValue());
				}
				if (!key.getComment().equals("")) {
					node.setComment(key.getComment());//Overwrite comment
				}
			});
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the value for the given configuration key
	 * 
	 * @param e
	 *            the key to get
	 * @return the value for the key
	 */
	public static ObjectValue getValue(Enum<?> e) {
		validateLoaded();
		for (ConfigKey key : keys) {
			if (key.getKey().equals(e.name())) {
				return ObjectValue.of(key);
			}
		}
		throw new RuntimeException("Config key '" + e.name() + "'does not exist!");
	}

	/**
	 * Sets the value for the given configuration keys
	 * 
	 * @param key
	 *            the key to update
	 * @param value
	 *            the new value for the key
	 */
	public static void setValue(ConfigKey key, Object value) {
		root.getNode(key.getPath()).setValue(value);
	}

	/**
	 * Saves the configuration file
	 */
	public static void save() {
		validateLoaded();
		try {
			loader.save(root);
		} catch (IOException e) {
			SpongePlugin.logger().error("Unable to save the configuration file!", e);
		}
	}

	/**
	 * Validates that the {@link ConfigManager} has been loaded successfully
	 */
	private static void validateLoaded() {
		if (loader == null) {
			throw new RuntimeException("ConfigManager not yet initialized! Must call reload() to load the manager!");
		}
	}

}
