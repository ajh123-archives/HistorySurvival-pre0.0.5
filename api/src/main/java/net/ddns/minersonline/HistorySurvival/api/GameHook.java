package net.ddns.minersonline.HistorySurvival.api;

import com.mojang.brigadier.CommandDispatcher;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLoader;

/**
 * An api used to interface with the game
 */
public abstract class GameHook {
	protected static GameHook instance = null;
	protected static ResourceLoader LOADER = null;

	/**
	 * A function that should be used to get the current instance of the game
	 */
	public static GameHook getInstance(){
		return instance;
	}

	protected static void setInstance(GameHook instance) {
		GameHook.instance = instance;
	}

	/**
	 * A function that used to get the CommandDispatcher for registering commands
	 */
	public abstract CommandDispatcher<Object> getDispatcher();

	public static ResourceLoader getLoader() {
		return LOADER;
	}

	public abstract void hello();
}
