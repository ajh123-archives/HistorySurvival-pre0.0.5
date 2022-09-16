package net.ddns.minersonline.HistorySurvival.engine.worldOld.types;

import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
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
	private transient static final Logger logger = LoggerFactory.getLogger(Terrain.class);

	public transient static final int SIZE = 800;

	private float x;
	private float z;
	private float size;
	private float maxHeight;
	private transient RawModel model;
	private transient TerrainTexturePack texturePack;
	private transient TerrainTexture blendMap;
	private transient HeightsGenerator generator = null;
	private int vertexCount;
	private int gridX;
	private int gridZ;
	private float waterHeight;

	private boolean isUsingHeightMap = false;

	// hard coded seed to get the same result every time
	private transient static final int SEED = 431; //new Random().nextInt(1000000000);

	private float[][] heights = null;

	private transient World world;

	public Terrain() {}

	private Terrain(World world, int gridX, int gridZ, float size, float maxHeight, ModelLoader loader, TerrainTexturePack texturePack,
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
		this.world = world;

		long nanoTime1 = System.nanoTime();
		generateTerrain(heightMap, vertexCount, maxHeight);
		this.model = updateTerrain(loader, generator);
		long nanoTime2 = System.nanoTime();
		float delta = (nanoTime2 - nanoTime1) / 1e3f;

		logger.info("Terrain: generateTerrain took " + delta + " microseconds");
	}

	public Terrain(World world, int gridX, int gridZ, float size, float maxHeight, ModelLoader loader, TerrainTexturePack texturePack,
				   TerrainTexture blendMap, int vertexCount) {
		this(world, gridX, gridZ, size, maxHeight, loader, texturePack, blendMap, null, vertexCount, false);
	}

	public Terrain(World world, int gridX, int gridZ, float size, float maxHeight, ModelLoader loader, TerrainTexturePack texturePack,
				   TerrainTexture blendMap, int vertexCount, String heightMap) {
		this(world, gridX, gridZ, size, maxHeight, loader, texturePack, blendMap, heightMap, vertexCount, true);
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

	private void generateTerrain(String heightMap, int vertexCount, float maxHeight) {

		BufferedImage image = null;
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
			assert image != null;
			generator = new MapGenerator(image, gridX, gridZ, SEED, maxHeight);
		}
		this.waterHeight = generator.getWaterHeight();

		this.vertexCount = generator.getVertxCount();

		heights = new float[this.vertexCount][this.vertexCount];

		for (int i = 0; i < this.vertexCount; i++) {
			for (int j = 0; j < this.vertexCount; j++) {
				float height = generator.generateHeight(j, i);
				heights[j][i] = height;
			}
		}

		generator.getInfo();
	}

	public RawModel updateTerrain(ModelLoader loader, HeightsGenerator generator) {
		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoOrds = new float[count * 2];

		int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];

		int vertexPointer = 0;
		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * size;
				float height = heights[j][i];
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * size;
				Vector3f normal = calculateNormal(j, i);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoOrds[vertexPointer * 2] = (float) j / ((float) vertexCount - 1);
				textureCoOrds[vertexPointer * 2 + 1] = (float) i / ((float) vertexCount - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < vertexCount - 1; gz++) {
			for (int gx = 0; gx < vertexCount - 1; gx++) {
				int topLeft = (gz * vertexCount) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertexCount) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}

		return loader.loadToVao(vertices, textureCoOrds, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z) {
		float heightL = getHeight(x-1, z);
		float heightR = getHeight(x+1, z);
		float heightD = getHeight(x, z-1);
		float heightU = getHeight(x, z+1);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalize();
		return normal;
	}

	private float getHeight(int x, int z) {
		x = x-1;
		z = z-1;
		if(x<0 || x>=SIZE || z<0 || z>=SIZE){
			return world.getHeightOfTerrain(x, z);
		}
		return heights[x][z];
	}

	public void setModel(RawModel model) {
		this.model = model;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setTexturePack(TerrainTexturePack texturePack) {
		this.texturePack = texturePack;
	}

	public void setBlendMap(TerrainTexture blendMap) {
		this.blendMap = blendMap;
	}
}
