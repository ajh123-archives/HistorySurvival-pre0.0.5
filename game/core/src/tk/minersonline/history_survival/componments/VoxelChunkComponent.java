package tk.minersonline.history_survival.componments;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.voxels.VoxelWorld;

public class VoxelChunkComponent implements Component {
	public static final boolean USE_PACKED_COLOR = true;
	public static final int VERTEX_SIZE = USE_PACKED_COLOR ? 7 : 10;
	public final VoxelComponent[] voxels;
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

	public VoxelChunkComponent(int width, int height, int depth, VoxelWorld world) {
		this.world = world;
		this.voxels = new VoxelComponent[width * height * depth];
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
					set(x, y, z, VoxelTypeComponent.AIR);
				}
			}
		}
	}

	public VoxelComponent get (int x, int y, int z) {
		if (x < 0 || x >= width) return null;
		if (y < 0 || y >= height) return null;
		if (z < 0 || z >= depth) return null;
		return getFast(x, y, z);
	}

	public VoxelComponent getFast (int x, int y, int z) {
		return voxels[x + z * width + y * widthTimesHeight];
	}

	public VoxelComponent set (int x, int y, int z, VoxelTypeComponent voxel) {
		if (x < 0 || x >= width) return null;
		if (y < 0 || y >= height) return null;
		if (z < 0 || z >= depth) return null;
		return setFast(x, y, z, voxel);
	}

	public VoxelComponent setFast (int x, int y, int z, VoxelTypeComponent voxel) {
		return voxels[x + z * width + y * widthTimesHeight] = new VoxelComponent(voxel, new Vector3(x, y, z), world);
	}

	/** Creates a mesh out of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	public int calculateVertices (float[] vertices) {
		int i = 0;
		int vertexOffset = 0;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++, i++) {
					VoxelComponent voxel = voxels[i];
					if (voxel.getType() == VoxelTypeComponent.AIR) continue;
					if (voxel.getType().getProperties().isTransparent()) continue;

					if (y < height - 1) {
						if (voxels[i + topOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + topOffset].getType().getProperties().isTransparent()) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (voxels[i + bottomOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + bottomOffset].getType().getProperties().isTransparent()) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (voxels[i + leftOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + leftOffset].getType().getProperties().isTransparent()) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (voxels[i + rightOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + rightOffset].getType().getProperties().isTransparent()) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (voxels[i + frontOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + frontOffset].getType().getProperties().isTransparent()) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (voxels[i + backOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + backOffset].getType().getProperties().isTransparent()) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
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
					VoxelComponent voxel = voxels[i];
					if (!voxel.getType().getProperties().isTransparent()) continue;

					if (y < height - 1) {
						if (voxels[i + topOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (voxels[i + bottomOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (voxels[i + leftOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (voxels[i + rightOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (voxels[i + frontOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (voxels[i + backOffset].getType() == VoxelTypeComponent.AIR) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	public static int createTop (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, VoxelComponent voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createBottom (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, VoxelComponent voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createLeft (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, VoxelComponent voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createRight (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, VoxelComponent voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createFront (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, VoxelComponent voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createBack (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, VoxelComponent voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelComponent.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelComponent.VOXEL_SIZE) + VoxelComponent.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelComponent.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	private static int addColorToVertices(float[] vertices, VoxelComponent voxel, int vertexOffset) {
		Color color = voxel.getType().getProperties().getColor();
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
