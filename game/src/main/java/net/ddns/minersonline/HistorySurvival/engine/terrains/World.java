package net.ddns.minersonline.HistorySurvival.engine.terrains;

import net.ddns.minersonline.HistorySurvival.engine.water.WaterTile;
import org.joml.Vector3f;

import java.util.List;


public interface World {
	float getHeightOfTerrain(float worldX, float worldZ);
	Vector3f getTerrainPoint(float worldX, float worldZ, float yOffset);
	float getHeightOfWater(float worldX, float worldZ);
	List<Terrain> getTerrains();
	List<WaterTile> getWaterTiles();
	Terrain getTerrain(float worldX, float worldZ);

	// size of each terrain tile
	float getTerrainSize();
	float getXSize();
	float getZSize();
}