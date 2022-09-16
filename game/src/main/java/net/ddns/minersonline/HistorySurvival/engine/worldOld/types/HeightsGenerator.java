package net.ddns.minersonline.HistorySurvival.engine.worldOld.types;

public interface HeightsGenerator {
	void getInfo();
	float getWaterHeight();
	float generateHeight (int x, int z);
	int getVertxCount();
}
