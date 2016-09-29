package com.kmecpp.spongecore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.kmecpp.spongecore.command.SpongeCommand;
import com.kmecpp.spongecore.config.ConfigManager;
import com.kmecpp.spongecore.event.SpongeListener;

public abstract class SpongePlugin implements Causable, SpongeListener {

	//Effectively final variables
	private static SpongePlugin plugin;
	private static PluginContainer pluginContainer;
	private static String pluginName;
	private static Logger logger;

	private static Initializer initializer = Initializer.DEFAULT;
	private static Class<?> config;

	//Services
	//	private static ConfigManager configManager = new ConfigManager();

	public SpongePlugin() {
		plugin = this;
	}

	public String getPluginName() {
		return pluginName;
	}

	public static SpongePlugin getPlugin() {
		return plugin;
	}

	@Override
	public Object getCause() {
		return pluginContainer;
	}

	public static PluginContainer getPluginContainer() {
		return pluginContainer;
	}

	public static Class<?> getConfig() {
		return config;
	}

	//	public static ConfigManager getConfigManager() {
	//		return configManager;
	//	}

	//Initialization
	protected void setInitializer(Initializer initializer) {
		SpongePlugin.initializer = initializer;
	}

	protected static void setConfig(Class<?> config) {
		SpongePlugin.config = config;
		//		configManager = new ConfigManager(Sponge.getGame().getConfigManager().getPluginConfig(this));
	}

	protected void preInit() {
	}

	protected void init() {
	}

	protected void postInit() {
	}

	@Listener
	public final void onGameConstruction(GameConstructionEvent e) {
		pluginContainer = Sponge.getPluginManager().fromInstance(this).get();
		pluginName = this.getClass().getAnnotation(Plugin.class).name();
		logger = LoggerFactory.getLogger(this.getClass().getName());

		ConfigManager.reload();
	}

	@Listener
	public final void onGamePreInitialization(GamePreInitializationEvent e) {
		preInit();
		initializer.preInit();
	}

	@Listener
	public final void onGameInitialization(GameInitializationEvent e) {
		init();
		initializer.init();
	}

	@Listener
	public final void onGamePostInitialization(GamePostInitializationEvent e) {
		postInit();
		initializer.postInit();
		log("Initialization complete!");
	}

	//COMMANDS
	public static final void registerCommand(SpongeCommand command) {
		Sponge.getCommandManager().register(plugin, command.getSpec(), command.getAliases());
	}

	//EVENTS
	public static final void registerListener(SpongeListener listener) {
		Sponge.getEventManager().registerListeners(plugin, listener);
	}

	public static final void unregisterListener(SpongeListener listener) {
		Sponge.getEventManager().unregisterListeners(listener);
	}

	public static final boolean postEvent(Event e) {
		return Sponge.getEventManager().post(e);
	}

	//LOGGING
	public static final Logger logger() {
		return logger;
	}

	public static final void log(String message) {
		logger.info(message);
	}

	public static final void logCore(String message) {
		logger.info(MarkerFactory.getMarker("SpongeCore"), message);
	}

}
