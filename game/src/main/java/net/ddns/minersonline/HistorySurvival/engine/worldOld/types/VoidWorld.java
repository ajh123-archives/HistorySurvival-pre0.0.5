package net.ddns.minersonline.HistorySurvival.engine.worldOld.types;

import net.ddns.minersonline.HistorySurvival.engine.worldOld.water.WaterTile;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VoidWorld extends World {

	List<WaterTile> waterTiles = new ArrayList<>();

	public VoidWorld() {}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		return (float) 0;
	}

	// return a point in space that is at worldX, worldZ, at yOffset units above the terrain
	public Vector3f getTerrainPoint(float worldX, float worldZ, float yOffset) {
		float y = getHeightOfTerrain(worldX, worldZ) + yOffset;
		return new Vector3f(worldX, y, worldZ);
	}

	public float getHeightOfWater(float worldX, float worldZ) {
		return 0;
	}

	public List<WaterTile> getWaterTiles() {
		return waterTiles;
	}

	public float getTerrainSize() {
		return 0;
	}

	public float getXSize() {
		return 0;
	}

	public float getZSize() {
		return 0;
	}
}
