package net.ddns.minersonline.HistorySurvival.api;

import com.mojang.brigadier.CommandDispatcher;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLoader;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import org.slf4j.Logger;

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

	public abstract Logger getLogger();

	protected static void setInstance(GameHook instance) {
		GameHook.instance = instance;
		ResourceType.init();
	}

	/**
	 * A function that used to get the CommandDispatcher for registering commands
	 */
	public abstract CommandDispatcher<CommandSender> getDispatcher();

	public static ResourceLoader getLoader() {
		return LOADER;
	}

	public abstract void hello();
}
