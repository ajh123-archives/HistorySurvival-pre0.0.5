package tk.minersonline.ExampleMod;

import net.fabricmc.api.ModInitializer;
import tk.minersonline.history_survival.HistorySurvival;

public class ExampleMod implements ModInitializer {

	@Override
	public void onInitialize() {
		HistorySurvival.LOGGER.info("Hello Mod");
	}
}
