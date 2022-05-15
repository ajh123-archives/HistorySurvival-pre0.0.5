package net.ddns.minersonline.HistorySurvival.gameplay;

import net.ddns.minersonline.HistorySurvival.api.EventHandler;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Extension
public class GameEvents implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(GameEvents.class);

    @Override
    public void hello() {
        logger.info("Finished loading History Survival gameplay!");
    }
}