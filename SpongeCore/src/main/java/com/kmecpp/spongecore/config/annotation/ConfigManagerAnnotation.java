package com.kmecpp.spongecore.config.annotation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;

import com.kmecpp.jlib.object.ObjectValue;
import com.kmecpp.jlib.object.Objects;
import com.kmecpp.spongecore.SpongePlugin;
import com.kmecpp.spongecore.config.SpongeConfig;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManagerAnnotation {

	private static List<ConfigKey> defaults;

	private static ConfigurationLoader<CommentedConfigurationNode> loader;
	private static CommentedConfigurationNode root;

	/**
	 * Reloads the configuration file
	 */
	public static void reload() {
		try {
			loader = Sponge.getGame().getConfigManager().getPluginConfig(SpongePlugin.getPlugin()).getConfig();

			Class<?> c = SpongePlugin.getConfig();
			if (c.isEnum()) {
				SpongeConfig spongeConfig = c.getAnnotation(SpongeConfig.class);
				if (spongeConfig != null) {
					root = loader.load(ConfigurationOptions.defaults().setHeader(spongeConfig.header()));
				}
			} else {
				SpongePlugin.logger().error("Configuration class: '" + c.getName() + "' is not an enum!");
				return;
			}

			defaults = Arrays.stream(SpongePlugin.getConfig().getDeclaredFields())
					.filter((field) -> field.isEnumConstant() && field.isAnnotationPresent(Configurable.class))
					.map((field) -> new ConfigKey(field.getName(), field.getAnnotation(Configurable.class)))
					.collect(Collectors.toList());

			defaults.forEach((def) -> {
				CommentedConfigurationNode node = root.getNode((Object[]) def.getData().key().split("\\."));
				if (node.isVirtual()) {
					node.setValue(Objects.typeEval(def.getData().def())); //Set value before comment
				}
				if (!def.getData().comment().equals("")) {
					node.setComment(def.getData().comment());//Overwrite comment
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
		for (ConfigKey def : defaults) {
			if (def.getName().equals(e.name())) {
				return ObjectValue.of(def);
			}
		}
		throw new RuntimeException("Config key '" + e.name() + "'does not exist!");
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

	private static void validateLoaded() {
		if (loader == null) {
			throw new RuntimeException("ConfigManager not yet initialized! Must call reload() to load the manager!");
		}
	}

}
