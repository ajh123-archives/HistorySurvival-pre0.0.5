package net.ddns.minersonline.HistorySurvival.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.ddns.minersonline.HistorySurvival.api.commands.Command;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

import java.util.Collection;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

public class HelpCommand extends Command {
	/**
	 * Testing
	 */
	private static void helpCommand(CommandContext<CommandSender> c) {
		CommandSender sender = c.getSource();
		Collection<CommandNode<CommandSender>> commands = c.getRootNode().getChildren();
		int page = 0;
		try {
			page = getInteger(c, "page");
		} catch (IllegalArgumentException ignored){
		}
		JSONTextComponent header = new JSONTextComponent(" Help page ("+page+") ");
		header.setColor(ChatColor.DARK_GREEN);

		JSONTextComponent prefix = new JSONTextComponent("=====");
		prefix.setColor(ChatColor.GOLD);
		JSONTextComponent suffix = new JSONTextComponent("\n");

		sender.sendMessage(prefix);
		sender.sendMessage(header);
		sender.sendMessage(prefix);
		sender.sendMessage(suffix);

		for(CommandNode<CommandSender> command : commands){
			sender.sendMessage(new JSONTextComponent("/"+command.getName()));
		}
	}

	public static void register(CommandDispatcher<CommandSender> dispatcher) {
		dispatcher.register(LiteralArgumentBuilder.<CommandSender>literal("help")
		.then(
			RequiredArgumentBuilder.<CommandSender, Integer>argument("page", IntegerArgumentType.integer())
			.executes(c -> {
				helpCommand(c);
				return 1;
			})
		)
		.then(
			RequiredArgumentBuilder.<CommandSender, String>argument("comm", StringArgumentType.string())
			.executes(c -> {
				helpCommand(c);
				return 1;
			})
		)
		.executes(c -> {
			helpCommand(c);
			return 1;
		}));
	}
}
