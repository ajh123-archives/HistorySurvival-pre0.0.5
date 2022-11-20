package net.ddns.minersonline.HistorySurvival.gameplay.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.ddns.minersonline.HistorySurvival.api.commands.Command;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

import java.util.Collection;

public class GamePlayCommand extends Command {
	/**
	 * Testing
	 */
	private static void gamePlayCommand(CommandContext<CommandSender> c) {
		CommandSender sender = c.getSource();

		JSONTextComponent component = new JSONTextComponent();
		component.setText("Asda!");
		component.setColor(ChatColor.GREEN);
		sender.sendMessage(component);
	}

	public static void register(CommandDispatcher<CommandSender> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("gameplay")
		.executes(c -> {
			gamePlayCommand(c);
			return 1;
		}));
	}
}
