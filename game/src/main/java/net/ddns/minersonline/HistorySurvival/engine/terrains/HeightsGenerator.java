package net.ddns.minersonline.HistorySurvival.engine.terrains;

public interface HeightsGenerator {
	void getInfo();
	float getWaterHeight();
	float generateHeight (int x, int z);

}
