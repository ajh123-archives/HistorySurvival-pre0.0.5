package net.ddns.minersonline.HistorySurvival.engine.terrains;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexturePack;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterTile;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VoidWorld implements World {

	List<Terrain> terrains = new ArrayList<>();
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

	public List<Terrain> getTerrains() {
		return terrains;
	}

	public Terrain getTerrain(float worldX, float worldZ) {
		return null;
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
