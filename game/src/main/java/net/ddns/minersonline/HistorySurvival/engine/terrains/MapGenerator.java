package net.ddns.minersonline.HistorySurvival.engine.terrains;

import java.util.Random;

public class MapGenerator implements HeightsGenerator {


	private int generateHeightCalls = 0;

	// only works with POSITIVE gridX and gridZ values!
	public MapGenerator(int gridX, int gridZ, int vertexCount, int seed, float maxHeight) {

	}

	public void getInfo() {
		System.out.println("generateHeightCalls() calls: " + generateHeightCalls);
	}

	public float getWaterHeight() {
		return 0;
	}


	public float generateHeight (int x, int z) {

		generateHeightCalls++;

		float total = 1;

		return total;
	}
}
