package net.ddns.minersonline.HistorySurvival.api;

import com.mojang.brigadier.CommandDispatcher;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;

public abstract class GameHook {
	protected static GameHook instance = null;

	public static GameHook getInstance(){
		return instance;
	}

	public abstract CommandDispatcher<Object> getDispatcher();

	public abstract void hello();
}
