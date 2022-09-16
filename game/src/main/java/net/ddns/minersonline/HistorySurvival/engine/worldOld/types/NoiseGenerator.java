package net.ddns.minersonline.HistorySurvival.engine.worldOld.types;

import java.util.Random;

public class NoiseGenerator implements HeightsGenerator {

	private float amplitude;
	private int octaves = 4;
	private float roughness = (0.5f);

	private Random random = new Random();
	private int seed;
	private int xOffset = 0;
	private int zOffset = 0;
	private int vertexCount;

	private int xmin = Integer.MAX_VALUE;
	private int xmax = Integer.MIN_VALUE;
	private int zmin = Integer.MAX_VALUE;
	private int zmax = Integer.MIN_VALUE;

	private int getNoiseCalls = 0;
	private int getNoise1Calls = 0;
	private int getSmoothNoiseCalls = 0;
	private int getInterpolatedNoiseCalls = 0;
	private int generateHeightCalls = 0;


	private float maxHeight;

	// only works with POSITIVE gridX and gridZ values!
	public NoiseGenerator(int gridX, int gridZ, int vertexCount, int seed, float maxHeight) {
		this.seed = seed;
		// not correct, but fix later ?
		this.amplitude = maxHeight;
		this.vertexCount = vertexCount;

		xOffset = gridX * (vertexCount - 1);
		zOffset = gridZ * (vertexCount - 1);

		this.maxHeight = maxHeight;
	}

	public void getInfo() {
		System.out.println("xmin: " + xmin);
		System.out.println("xmax: " + xmax);
		System.out.println("zmin: " + zmin);
		System.out.println("zmax: " + zmax);
		System.out.println("getNoise() calls: " + getNoiseCalls);
		System.out.println("getNoise1() calls: " + getNoise1Calls);
		System.out.println("getSmoothNoise() calls: " + getSmoothNoiseCalls);
		System.out.println("getInterpolatedNoise() calls: " + getInterpolatedNoiseCalls);
		System.out.println("generateHeightCalls() calls: " + generateHeightCalls);
	}

	public float getWaterHeight() {
		return 0;
	}


	public float generateHeight (int x, int z) {

		generateHeightCalls++;

		float total = 0;
		// added * 4 to make terrain flatter
		float d = (float) Math.pow(2, octaves - 1) * 4f;

		for (int i = 0; i < octaves; i++) {
			float freq = (float) (Math.pow(2,  i) / d);
			float amp = (float) Math.pow(roughness,  i) * amplitude;
			total += getInterpolatedNoise((x + xOffset) * freq, (z + zOffset) * freq) * amp;
		}

		return total;
	}

	private float getInterpolatedNoise(float x, float z) {

		getInterpolatedNoiseCalls++;

		int intX = (int) x;
		int intZ = (int) z;
		float fracX = x - intX;
		float fracZ = z - intZ;

		float v1 = getSmoothNoise(intX, intZ);
		float v2 = getSmoothNoise(intX + 1, intZ);
		float v3 = getSmoothNoise(intX, intZ + 1);
		float v4 = getSmoothNoise(intX + 1, intZ + 1);
		float i1 = interpolate(v1, v2, fracX);
		float i2 = interpolate(v3, v4, fracX);
		return interpolate(i1, i2, fracZ);

	}

	private float interpolate(float a, float b, float blend) {
		double theta = blend * Math.PI;
		float f = (1f - (float)Math.cos(theta)) * 0.5f;
		return a * (1f - f) + b * f;
	}

	private float getSmoothNoise(int x, int z) {

		getSmoothNoiseCalls++;

		float corners = (
				getNoise(x - 1, z - 1)
						+ getNoise(x + 1, z - 1)
						+ getNoise(x - 1, z + 1)
						+ getNoise(x + 1, z + 1)
		) / 16f;
		float sides = (
				getNoise(x - 1, z)
						+ getNoise(x + 1, z)
						+ getNoise(x, z - 1)
						+ getNoise(x, z + 1)
		) / 8f;
		float center = getNoise(x, z) / 4f;
		return corners + sides + center;
	}

	private float getNoise(int x, int z) {
		getNoiseCalls++;
		return getNoise1(x, z);
	}

	private float getNoise1(int x, int z) {
		if (x < xmin) {
			xmin = x;
		}
		if (x > xmax) {
			xmax = x;
		}
		if (z < zmin) {
			zmin = z;
		}
		if (z > zmax) {
			zmax = z;
		}

		getNoiseCalls++;

		random.setSeed(x * 963L + (long) z * 13251 * seed * 31);
		return random.nextFloat() * 2f - 1f;
	}

	@Override
	public int getVertxCount() {
		return vertexCount;
	}
}
