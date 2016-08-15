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

import com.kmecpp.spongecore.command.SpongeCommand;
import com.kmecpp.spongecore.event.SpongeListener;

public abstract class SpongeCore {

	//Effectively final variables
	private static SpongeCore plugin;
	private static Initializer initializer;
	private static ConfigManager configManager;
	private static Logger logger;

	public SpongeCore() {
		plugin = this;
	}

	public static SpongeCore getPlugin() {
		return plugin;
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	//INITIALIZATION
	public abstract String getPluginName();

	public abstract Initializer getInitializer();

	public abstract ConfigurationSpec getConfigurationSpec();

	@Listener
	public void onGameConstruction(GameConstructionEvent e) {
		logger = LoggerFactory.getLogger(this.getClass().getName());
		configManager = new ConfigManager(Sponge.getGame().getConfigManager().getPluginConfig(this));

		initializer = getInitializer();
	}

	@Listener
	public void onGameInitialization(GamePreInitializationEvent e) {
		initializer.preInit();
	}

	@Listener
	public void onGameInitialization(GameInitializationEvent e) {
		initializer.init();
	}

	@Listener
	public void onGameInitialization(GamePostInitializationEvent e) {
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
	public static final Logger getLogger() {
		return logger;
	}

	public static final void log(String message) {
		logger.info(message);
	}

	public static final void logCore(String message) {
		logger.info(MarkerFactory.getMarker("SpongeCore"), message);
	}

}