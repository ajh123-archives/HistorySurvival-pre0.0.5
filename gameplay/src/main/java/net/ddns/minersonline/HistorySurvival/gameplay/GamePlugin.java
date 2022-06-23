package net.ddns.minersonline.HistorySurvival.gameplay;

import com.mojang.brigadier.context.CommandContext;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GamePlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(GamePlugin.class);

    public GamePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("History Survival gameplay started!");
        GameHook.getInstance().getDispatcher().register(literal("foo")
        .then(
            argument("bar", integer())
            .executes(c -> {
                fooBar(c);
                return 1;
            })
        )
        .executes(c -> {
            fooBar(c);
            System.out.println("Called foo with no arguments");
            return 1;
        }));
        logger.info("Commands registered");
    }

    private void fooBar(CommandContext<Object> c) {
        CommandContext<CommandSender> context = (CommandContext<CommandSender>) (CommandContext<? extends Object>) c;
        CommandSender sender = context.getSource();
        sender.sendMessage(new JSONTextComponent("Hello"));
    }


    @Override
    public void stop() {
        logger.info("History Survival gameplay stopped!");
    }
}