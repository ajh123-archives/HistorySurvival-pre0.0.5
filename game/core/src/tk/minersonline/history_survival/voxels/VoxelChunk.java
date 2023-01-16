package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import java.awt.*;

public class VoxelChunk {
	public static final boolean USE_PACKED_COLOR = true;
	public static final int VERTEX_SIZE = USE_PACKED_COLOR ? 7 : 10;
	public final Voxel[] voxels;
	public final int width;
	public final int height;
	public final int depth;
	public final Vector3 offset = new Vector3();
	private final int widthTimesHeight;
	private final int topOffset;
	private final int bottomOffset;
	private final int leftOffset;
	private final int rightOffset;
	private final int frontOffset;
	private final int backOffset;
	private final VoxelWorld world;

	public VoxelChunk (int width, int height, int depth, VoxelWorld world) {
		this.world = world;
		this.voxels = new Voxel[width * height * depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.topOffset = width * depth;
		this.bottomOffset = -width * depth;
		this.leftOffset = -1;
		this.rightOffset = 1;
		this.frontOffset = -width;
		this.backOffset = width;
		this.widthTimesHeight = width * height;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++) {
					set(x, y, z, VoxelType.AIR);
				}
			}
		}
	}

	public Voxel get (int x, int y, int z) {
		if (x < 0 || x >= width) return null;
		if (y < 0 || y >= height) return null;
		if (z < 0 || z >= depth) return null;
		return getFast(x, y, z);
	}

	public Voxel getFast (int x, int y, int z) {
		return voxels[x + z * width + y * widthTimesHeight];
	}

	public Voxel set (int x, int y, int z, VoxelType voxel) {
		if (x < 0 || x >= width) return null;
		if (y < 0 || y >= height) return null;
		if (z < 0 || z >= depth) return null;
		return setFast(x, y, z, voxel);
	}

	public Voxel setFast (int x, int y, int z, VoxelType voxel) {
		return voxels[x + z * width + y * widthTimesHeight] = new Voxel(voxel, new Vector3(x, y, z), world);
	}

	/** Creates a mesh out of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	public int calculateVertices (float[] vertices) {
		int i = 0;
		int vertexOffset = 0;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++, i++) {
					Voxel voxel = voxels[i];
					if (voxel.getType() == VoxelType.AIR) continue;
					if (voxel.getType().isTransparent()) continue;

					if (y < height - 1) {
						if (voxels[i + topOffset].getType() == VoxelType.AIR) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + topOffset].getType().isTransparent()) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (voxels[i + bottomOffset].getType() == VoxelType.AIR) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + bottomOffset].getType().isTransparent()) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (voxels[i + leftOffset].getType() == VoxelType.AIR) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + leftOffset].getType().isTransparent()) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (voxels[i + rightOffset].getType() == VoxelType.AIR) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + rightOffset].getType().isTransparent()) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (voxels[i + frontOffset].getType() == VoxelType.AIR) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + frontOffset].getType().isTransparent()) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (voxels[i + backOffset].getType() == VoxelType.AIR) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + backOffset].getType().isTransparent()) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	/** Creates a mesh out of the transparent parts of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	public int calculateTransparentVertices (float[] vertices) {
		int i = 0;
		int vertexOffset = 0;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++, i++) {
					Voxel voxel = voxels[i];
					if (!voxel.getType().isTransparent()) continue;

					if (y < height - 1) {
						if (voxels[i + topOffset].getType() == VoxelType.AIR) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (voxels[i + bottomOffset].getType() == VoxelType.AIR) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (voxels[i + leftOffset].getType() == VoxelType.AIR) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (voxels[i + rightOffset].getType() == VoxelType.AIR) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (voxels[i + frontOffset].getType() == VoxelType.AIR) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (voxels[i + backOffset].getType() == VoxelType.AIR) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	public static int createTop (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createBottom (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createLeft (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createRight (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createFront (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createBack (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * Voxel.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * Voxel.VOXEL_SIZE) + Voxel.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -Voxel.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	private static int addColorToVertices(float[] vertices, Voxel voxel, int vertexOffset) {
		Color color = voxel.getColor();
		if (USE_PACKED_COLOR) {
			vertices[vertexOffset] = color.toFloatBits();
			return 1;
		} else {
			vertices[vertexOffset++] = color.r;
			vertices[vertexOffset++] = color.g;
			vertices[vertexOffset++] = color.b;
			vertices[vertexOffset] = color.a;
			return 4;
		}
	}
}
