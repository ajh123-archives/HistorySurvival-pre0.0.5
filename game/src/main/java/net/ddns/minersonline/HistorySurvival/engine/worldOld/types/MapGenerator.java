package net.ddns.minersonline.HistorySurvival.engine.worldOld.types;

import java.awt.image.BufferedImage;

public class MapGenerator implements HeightsGenerator {
	private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256;

	private float amplitude;

	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;
	private int vertexCount;

	private int xmin = Integer.MAX_VALUE;
	private int xmax = Integer.MIN_VALUE;
	private int zmin = Integer.MAX_VALUE;
	private int zmax = Integer.MIN_VALUE;

	private BufferedImage image;

	private int generateHeightCalls = 0;


	private float maxHeight;

	// only works with POSITIVE gridX and gridZ values!
	public MapGenerator(BufferedImage image, int gridX, int gridZ, int seed, float maxHeight) {
		int vertexCount = image.getHeight();

		this.seed = seed;
		// not correct, but fix later ?
		this.amplitude = maxHeight;
		this.vertexCount = vertexCount;

		xOffset = gridX * (vertexCount - 1);
		zOffset = gridZ * (vertexCount - 1);

		this.maxHeight = maxHeight;

		this.image = image;
	}

	public void getInfo() {
		System.out.println("generateHeightCalls() calls: " + generateHeightCalls);
	}

	public float getWaterHeight() {
		return -3f;
	}


	public float generateHeight (int x, int z) {
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getHeight()){
			return 0;
		}
		generateHeightCalls++;
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOUR /2f;
		height /= MAX_PIXEL_COLOUR /2f;
		return height * maxHeight;
	}

	@Override
	public int getVertxCount() {
		return vertexCount;
	}
}
