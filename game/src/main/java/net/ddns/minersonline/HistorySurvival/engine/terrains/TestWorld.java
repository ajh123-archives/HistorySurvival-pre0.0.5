package net.ddns.minersonline.HistorySurvival.engine.terrains;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexturePack;
import net.ddns.minersonline.HistorySurvival.engine.water.WaterTile;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class TestWorld extends World {
	float waterHeight;
	float waterSize;
	float terrainSize;

	float xSize;
	float zSize;

	int terrainVertexCount;

	List<WaterTile> waterTiles = new ArrayList<>();

	public TestWorld() {}

	public TestWorld(ModelLoader loader, int xTiles, int zTiles, float terrainMaxHeight, int terrainVertexCount) {

		// *********TERRAIN TEXTURE STUFF**********
		float terrainSize = Terrain.SIZE;

		xSize = terrainSize * xTiles;
		zSize = terrainSize * zTiles;

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2.png"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud.png"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers.png"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path.png"));

		texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		blendMap = new TerrainTexture(loader.loadTexture("blendMap.png"));

		this.terrainSize = terrainSize;
		this.terrainVertexCount = terrainVertexCount;

		// tiles are assumed to be square
		float waterOffset = (terrainSize) * 0.5f;
		//this.waterHeight = waterHeight;
		this.waterSize = Terrain.SIZE;

		for (int x = 0; x < xTiles; x++) {
			for (int z = 0; z < zTiles; z++) {
				Terrain terrain = new Terrain(this, x, z, terrainSize, terrainMaxHeight, loader, texturePack, blendMap, terrainVertexCount, "heightmap.png");
				terrains.add(terrain);
				// center the water tile on the terrain tile
				waterHeight = terrain.getHeightOfWater();
				System.out.println("===> waterHeight = " + waterHeight);
				WaterTile water = new WaterTile(x * terrainSize + waterOffset, z * terrainSize + waterOffset, waterHeight, waterSize-waterOffset);
				waterTiles.add(water);
			}
		}

		System.out.println("" + this.getClass().getName() + ": generated " + terrains.size() + " terrains.");

	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float height = 0;
		Terrain terrain = getTerrain(worldX, worldZ);

		// if we got a terrain, get terrain height
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(worldX, worldZ);
		}

		//System.out.println("" + this.getClass().getName() + ": getHeightOfTerrain: (" + worldX + ", " + worldZ + "), height " + height);

		return height;
	}

	// return a point in space that is at worldX, worldZ, at yOffset units above the terrain
	public Vector3f getTerrainPoint(float worldX, float worldZ, float yOffset) {
		float y = getHeightOfTerrain(worldX, worldZ) + yOffset;
		return new Vector3f(worldX, y, worldZ);
	}

	public float getHeightOfWater(float worldX, float worldZ) {
		return waterHeight;
	}

	public List<WaterTile> getWaterTiles() {
		return waterTiles;
	}

	public float getTerrainSize() {
		return terrainSize;
	}

	public float getXSize() {
		return xSize;
	}

	public float getZSize() {
		return zSize;
	}
}
