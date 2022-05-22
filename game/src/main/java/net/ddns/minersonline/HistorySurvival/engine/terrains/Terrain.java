package net.ddns.minersonline.HistorySurvival.engine.terrains;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.textures.ModelTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexture;
import net.ddns.minersonline.HistorySurvival.engine.textures.TerrainTexturePack;
import net.ddns.minersonline.HistorySurvival.engine.utils.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Terrain {
	private static final Logger logger = LoggerFactory.getLogger(Terrain.class);

	public static final int SIZE = 300;

	private float x;
	private float z;
	private float size;
	private float maxHeight;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private int vertexCount;
	private int gridX;
	private int gridZ;
	private float waterHeight;

	private boolean isUsingHeightMap = false;

	// hard coded seed to get the same result every time
	private static final int SEED = 431; //new Random().nextInt(1000000000);

	private float[][] heights;

	public Terrain(int gridX, int gridZ, float size, float maxHeight, ModelLoader loader, TerrainTexturePack texturePack,
				   TerrainTexture blendMap, String heightMap, int vertexCount, boolean isUsingHeightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.size = size;
		this.maxHeight = maxHeight;
		this.x = gridX * size;
		this.z = gridZ * size;
		this.vertexCount = vertexCount;
		this.gridX = gridX;
		this.gridZ = gridZ;
		this.waterHeight = 0;
		this.isUsingHeightMap = isUsingHeightMap;

		long nanoTime1 = System.nanoTime();
		this.model = generateTerrain(loader, heightMap, vertexCount, maxHeight);
		long nanoTime2 = System.nanoTime();
		float delta = (nanoTime2 - nanoTime1) / 1e3f;

		logger.info("Terrain: generateTerrain took " + delta + " microseconds");
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public float getSize() {
		return size;
	}

	public Vector3f getPosition() {
		return new Vector3f(x, 0, z);
	}

	public RawModel getModel() {
		return model;
	}

	// uses texture pack, so can return null
	public ModelTexture getTexture() {
		return null;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public float getHeightOfWater() {
		return waterHeight;
	}

	public boolean containsPosition(float worldX, float worldZ) {
		if (worldX < x || worldX >= x + size)
			return false;
		if (worldZ < z || worldZ >= z + size)
			return false;
		return true;
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = size / ((float)heights.length - 1);
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;

		if (xCoord <= (1 - zCoord)) {
			answer = Maths.baryCentric(
					new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.baryCentric(
					new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1),
					new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	private RawModel generateTerrain(ModelLoader loader, String heightMap, int vertexCount, float maxHeight) {

		BufferedImage image = null;
		HeightsGenerator generator = null;
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classloader.getResourceAsStream(heightMap);

		try {
			assert stream != null;
			image = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(!isUsingHeightMap) {
			generator = new NoiseGenerator(gridX, gridZ, vertexCount, SEED, maxHeight);
		} else {
			generator = new MapGenerator(gridX, gridZ, vertexCount, SEED, maxHeight);
		}
		this.waterHeight = generator.getWaterHeight();

		//int VERTEX_COUNT = image.getHeight();
		int VERTEX_COUNT = vertexCount;

		int count = VERTEX_COUNT * VERTEX_COUNT;
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];

		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * size;
				float height = getHeight(j, i, generator);
				vertices[vertexPointer * 3 + 1] = height;
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * size;
				Vector3f normal = calculateNormal(j, i, generator);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		generator.getInfo();

		return loader.loadToVao(vertices, textureCoords, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
		float heightL = getHeight(x-1, z, generator);
		float heightR = getHeight(x+1, z, generator);
		float heightD = getHeight(x, z-1, generator);
		float heightU = getHeight(x, z+1, generator);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}

	private float getHeight(int x, int z, HeightsGenerator generator) {
		return generator.generateHeight(x, z);
	}
}
