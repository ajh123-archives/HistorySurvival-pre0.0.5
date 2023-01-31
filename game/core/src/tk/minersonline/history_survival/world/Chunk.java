package tk.minersonline.history_survival.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.util.VoxelUtils;

import static tk.minersonline.history_survival.world.World.*;

public class Chunk implements Component {
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
	private final World world;
	public Mesh mesh;
	public Mesh transparentMesh;
	public Material material;
	public int numVertices;
	public int numTransparentVertices;
	public float[] vertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
	public float[] transparentVertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
	public boolean dirty = false;
	int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
	public short[] indices = new short[len];

	public Chunk(int width, int height, int depth, World world) {
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
		Entity me = world.engine.createEntity();
		me.add(this);
		world.engine.addEntity(me);
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

	public void set (int x, int y, int z, VoxelType type) {
		if (x < 0 || x >= width) return;
		if (y < 0 || y >= height) return;
		if (z < 0 || z >= depth) return;
		setFast(x, y, z, type);
	}

	public void setFast (int x, int y, int z, VoxelType type) {
		Voxel voxelEntity = new Voxel(type, new Vector3(x, y, z), world);
		voxels[x + z * width + y * widthTimesHeight] = voxelEntity;
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
					if (voxel.getType().getProperties().isTransparent()) continue;

					if (y < height - 1) {
						if (voxels[i + topOffset].getType() == VoxelType.AIR) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + topOffset].getType().getProperties().isTransparent()) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (voxels[i + bottomOffset].getType() == VoxelType.AIR) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + bottomOffset].getType().getProperties().isTransparent()) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (voxels[i + leftOffset].getType() == VoxelType.AIR) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + leftOffset].getType().getProperties().isTransparent()) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (voxels[i + rightOffset].getType() == VoxelType.AIR) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + rightOffset].getType().getProperties().isTransparent()) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (voxels[i + frontOffset].getType() == VoxelType.AIR) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
						if (voxels[i + frontOffset].getType().getProperties().isTransparent()) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (voxels[i + backOffset].getType() == VoxelType.AIR) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel);
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
					Voxel voxel = voxels[i];
					if (!voxel.getType().getProperties().isTransparent()) continue;

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
		float topOffset = 0;

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createBottom (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -1;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createLeft (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createRight (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createFront (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	public static int createBack (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);

		vertices[vertexOffset++] = offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[vertexOffset++] = offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = 0;
		vertices[vertexOffset++] = -VoxelUtils.VOXEL_SIZE;
		vertexOffset += addColorToVertices(vertices, voxel, vertexOffset);
		return vertexOffset;
	}

	private static int addColorToVertices(float[] vertices, Voxel voxel, int vertexOffset) {
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
