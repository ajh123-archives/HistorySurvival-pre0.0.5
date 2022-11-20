package net.ddns.minersonline.HistorySurvival.api.commands;

import com.mojang.brigadier.CommandDispatcher;

public abstract class Command {
	public static void register(CommandDispatcher<CommandSender> dispatcher) {}
}
