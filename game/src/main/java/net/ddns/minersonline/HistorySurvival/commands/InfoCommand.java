package net.ddns.minersonline.HistorySurvival.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.ddns.minersonline.HistorySurvival.api.EnvironmentType;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.Command;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.network.Utils;

public class InfoCommand extends Command {
	/**
	 * Info command is used to get info about the current game.
	 */
	private static void infoCommand(CommandContext<CommandSender> c) {
		CommandSender sender = c.getSource();

		EnvironmentType type = GameHook.getInstance().getType();
		String sType = "server";

		if (type == EnvironmentType.CLIENT) {
			sType = "client";
		}

		JSONTextComponent component = new JSONTextComponent();
		component.setText("Game is currently running on a "+sType+".");
		sender.sendMessage(component);

		JSONTextComponent component2 = new JSONTextComponent();
		if (type == EnvironmentType.CLIENT) {
			component2.setText("Client Software:");
		} else {
			component2.setText("Server Software:");
		}
		component2.setColor(ChatColor.GREEN);
		sender.sendMessage(component2);

		JSONTextComponent component3 = new JSONTextComponent();
		component3.setText("\tName: "+ Utils.GAME);
		sender.sendMessage(component3);

		JSONTextComponent component4 = new JSONTextComponent();
		component4.setText("\tBrand: "+ Utils.GAME_ID);
		sender.sendMessage(component4);

		JSONTextComponent component5 = new JSONTextComponent();
		component5.setText("\tVersion: "+ Utils.VERSION);
		sender.sendMessage(component5);
	}

	public static void register(CommandDispatcher<CommandSender> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("info")
		.executes(c -> {
			infoCommand(c);
			return 1;
		}));
	}
}
