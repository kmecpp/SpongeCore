package com.kmecpp.spongecore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import com.kmecpp.spongecore.command.SpongeCommand;
import com.kmecpp.spongecore.event.SpongeListener;

public abstract class SpongeCore {

	//Effectively final variables
	private static SpongeCore plugin;
	private static Initializer initializer;
	private static Logger logger;

	public SpongeCore() {
		plugin = this;
		initializer = getInitializer();
		logger = LoggerFactory.getLogger(this.getClass().getName());
	}

	public static SpongeCore getPlugin() {
		return plugin;
	}

	//INITIALIZATION
	public abstract Initializer getInitializer();

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