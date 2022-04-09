package net.ddns.minersonline.HistorySurvival.gameplay;

import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GamePlugin extends Plugin {
    private static final Logger logger = LoggerFactory.getLogger(GamePlugin.class);

    public GamePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        logger.info("History Survival gameplay started!");
    }

    @Override
    public void stop() {
        logger.info("History Survival gameplay stopped!");
    }
}