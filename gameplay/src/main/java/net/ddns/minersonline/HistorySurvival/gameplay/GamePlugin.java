package net.ddns.minersonline.HistorySurvival.gameplay;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
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
        GameHook game = GameHook.getInstance();
        game.getDispatcher().register(
        literal("foo")
                .then(
                        argument("bar", integer())
                                .executes(c -> {
                                    fooBar(c);
                                    return 1;
                                })
                )
                .executes(c -> {
                    System.out.println("Called foo with no arguments");
                    return 1;
                })
        );
    }

    private void fooBar(CommandContext<Object> c) {
        CommandSender sender = (CommandSender) c.getSource();
        sender.sendMessage(new JSONTextComponent("Hello"));
    }


    @Override
    public void stop() {
        logger.info("History Survival gameplay stopped!");
    }
}