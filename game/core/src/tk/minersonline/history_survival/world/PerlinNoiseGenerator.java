package tk.minersonline.history_survival.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import tk.minersonline.history_survival.world.voxels.VoxelType;

/** Adapted from <a href="http://devmag.org.za/2009/04/25/perlin-noise/">http://devmag.org.za/2009/04/25/perlin-noise/</a>
 * @author badlogic */
public class PerlinNoiseGenerator {
	public static float[][] generateWhiteNoise (int width, int height) {
		float[][] noise = new float[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				noise[x][y] = MathUtils.random();
			}
		}
		return noise;
	}

	public static float interpolate (float x0, float x1, float alpha) {
		return x0 * (1 - alpha) + alpha * x1;
	}

	public static float[][] generateSmoothNoise (float[][] baseNoise, int octave) {
		int width = baseNoise.length;
		int height = baseNoise[0].length;
		float[][] smoothNoise = new float[width][height];

		int samplePeriod = 1 << octave; // calculates 2 ^ k
		float sampleFrequency = 1.0f / samplePeriod;
		for (int i = 0; i < width; i++) {
			int sample_i0 = (i / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % width; // wrap around
			float horizontal_blend = (i - sample_i0) * sampleFrequency;

			for (int j = 0; j < height; j++) {
				int sample_j0 = (j / samplePeriod) * samplePeriod;
				int sample_j1 = (sample_j0 + samplePeriod) % height; // wrap around
				float vertical_blend = (j - sample_j0) * sampleFrequency;
				float top = interpolate(baseNoise[sample_i0][sample_j0], baseNoise[sample_i1][sample_j0], horizontal_blend);
				float bottom = interpolate(baseNoise[sample_i0][sample_j1], baseNoise[sample_i1][sample_j1], horizontal_blend);
				smoothNoise[i][j] = interpolate(top, bottom, vertical_blend);
			}
		}

		return smoothNoise;
	}

	public static float[][] generatePerlinNoise (float[][] baseNoise, int octaveCount) {
		int width = baseNoise.length;
		int height = baseNoise[0].length;
		float[][][] smoothNoise = new float[octaveCount][][]; // an array of 2D arrays containing
		float persistence = 0.7f;

		for (int i = 0; i < octaveCount; i++) {
			smoothNoise[i] = generateSmoothNoise(baseNoise, i);
		}

		float[][] perlinNoise = new float[width][height]; // an array of floats initialised to 0

		float amplitude = 1.0f;
		float totalAmplitude = 0.0f;

		for (int octave = octaveCount - 1; octave >= 0; octave--) {
			amplitude *= persistence;
			totalAmplitude += amplitude;

			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
				}
			}
		}

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				perlinNoise[i][j] /= totalAmplitude;
			}
		}

		return perlinNoise;
	}

	public static float[][] generatePerlinNoise (int width, int height, int octaveCount) {
		float[][] baseNoise = generateWhiteNoise(width, height);
		return generatePerlinNoise(baseNoise, octaveCount);
	}

	public static byte[] generateHeightMap (int width, int height, int min, int max, int octaveCount) {
		float[][] baseNoise = generateWhiteNoise(width, height);
		float[][] noise = generatePerlinNoise(baseNoise, octaveCount);
		byte[] bytes = new byte[baseNoise.length * baseNoise[0].length];
		int idx = 0;
		int range = max - min;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				bytes[idx++] = (byte)(noise[x][y] * range + min);
			}
		}
		return bytes;
	}

	public static Pixmap generatePixmap (int width, int height, int min, int max, int octaveCount) {
		byte[] bytes = generateHeightMap(width, height, min, max, octaveCount);
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		for (int i = 0, idx = 0; i < bytes.length; i++) {
			byte val = bytes[i];
			pixmap.getPixels().put(idx++, val);
			pixmap.getPixels().put(idx++, val);
			pixmap.getPixels().put(idx++, val);
			pixmap.getPixels().put(idx++, (byte)255);
		}
		return pixmap;
	}

	public static Pixmap colourPixmap (World voxelWorld, int startX, int startZ, int endX, int endZ) {
		int scale = 5;
		Pixmap pixmap = new Pixmap(endX * scale, endZ * scale, Format.RGBA8888);
		for (int z = startZ; z < endZ; z++) {
			for (int x = startX; x < endX; x++) {
				int y = (int) Math.floor(voxelWorld.getHighest(x, z));
				VoxelType voxel = voxelWorld.get(x, y-1, z);
				Color color = Color.BLACK;
				if (voxel != null && voxel != VoxelType.AIR ) {
					color = voxel.getProperties().getColor().cpy();
				}
				color.a = 1;
				pixmap.setColor(color);
				pixmap.fillRectangle((x-startX)*scale, (z-startZ)*scale, scale, scale);
				pixmap.setColor(Color.CYAN);

				if (x % World.CHUNK_SIZE_X == 0) {
					pixmap.drawLine((x-startX)*scale, (endX-startX)*scale, (x-startX)*scale, 0);
				}
				if (z % World.CHUNK_SIZE_Z == 0) {
					pixmap.drawLine((endZ-startZ)*scale, (z-startZ)*scale, 0, (z-startZ)*scale);
				}

				int midX = (startX+endX)/2;
				int minZ = (startZ+endZ)/2;
				int camX = (midX-startX)*scale;
				int camY = (minZ-startZ)*scale;
				int midCamX = (camX+(scale/2));
				int midCamY = (camY+(scale/2));
				pixmap.setColor(Color.FIREBRICK);
				pixmap.fillCircle(midCamX, midCamY, scale);
				pixmap.setColor(Color.CORAL);
				pixmap.drawCircle(midCamX, midCamY, scale);
			}
		}
		return pixmap;
	}

	public static void generateVoxels (World voxelWorld, int min, int max, int octaveCount) {
		byte[] heightMap = PerlinNoiseGenerator.generateHeightMap(voxelWorld.voxelsX, voxelWorld.voxelsZ, min, max, octaveCount);
		int idx = 0;
		for (int z = 0; z < voxelWorld.voxelsZ; z++) {
			for (int x = 0; x < voxelWorld.voxelsX; x++) {
				voxelWorld.setColumn(x, heightMap[idx++], z, VoxelType.STONE);

				float y = voxelWorld.getHighest(x, z);
				VoxelType surface = VoxelType.GRASS;
				if (y < 30) {
					surface = VoxelType.SAND;
				}

				voxelWorld.set(x, y, z, surface);
				voxelWorld.set(x, voxelWorld.getHighest(x, z)-2, z, VoxelType.DIRT);
				voxelWorld.set(x, voxelWorld.getHighest(x, z)-3, z, VoxelType.DIRT);
				voxelWorld.set(x, voxelWorld.getHighest(x, z)-4, z, VoxelType.DIRT);

				if (y < 30) {
					voxelWorld.setColumn(x, y, 29, z, VoxelType.WATER);
				}
			}
		}
	}
}