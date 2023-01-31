package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.world.Chunk;
import tk.minersonline.history_survival.world.utils.VoxelUtils;
import tk.minersonline.history_survival.world.voxels.Voxel;
import tk.minersonline.history_survival.world.voxels.VoxelType;

import static tk.minersonline.history_survival.world.data.ChunkMesh.USE_PACKED_COLOR;
import static tk.minersonline.history_survival.world.data.ChunkMesh.VERTEX_SIZE;

public class ChunkMeshBuilder {
	public static ChunkMesh build(Chunk chunk, ChunkBuilderData builderData) {
		builderData.begin(chunk, true);
		builderData.end();

		builderData.begin(chunk, false);
		builderData.end();

		return chunk.chunkMesh;
	}

	/** Creates a mesh out of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	static int calculateVertices(ChunkBuilderData builderData) {
		int i = 0;

		for (int y = 0; y < builderData.height; y++) {
			for (int z = 0; z < builderData.depth; z++) {
				for (int x = 0; x < builderData.width; x++, i++) {
					Voxel voxel = builderData.chunk.voxels[i];
					if (voxel.getType() == VoxelType.AIR) continue;
					if (voxel.getType().getProperties().isTransparent()) continue;

					if (y < builderData.height - 1) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.topOffset].getType() == VoxelType.AIR) createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.topOffset].getType().getProperties().isTransparent()) createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (y > 0) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.bottomOffset].getType() == VoxelType.AIR) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.bottomOffset].getType().getProperties().isTransparent()) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x > 0) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.leftOffset].getType() == VoxelType.AIR) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.leftOffset].getType().getProperties().isTransparent()) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x < builderData.width - 1) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.rightOffset].getType() == VoxelType.AIR) createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.rightOffset].getType().getProperties().isTransparent()) createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z > 0) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.frontOffset].getType() == VoxelType.AIR) createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.frontOffset].getType().getProperties().isTransparent()) createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z < builderData.depth - 1) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.backOffset].getType() == VoxelType.AIR) createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.backOffset].getType().getProperties().isTransparent()) createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
				}
			}
		}
		return builderData.vertexOffset / VERTEX_SIZE;
	}

	/** Creates a mesh out of the transparent parts of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	static int calculateTransparentVertices(ChunkBuilderData builderData) {
		int i = 0;

		for (int y = 0; y < builderData.height; y++) {
			for (int z = 0; z < builderData.depth; z++) {
				for (int x = 0; x < builderData.width; x++, i++) {
					Voxel voxel = builderData.chunk.voxels[i];
					if (!voxel.getType().getProperties().isTransparent()) continue;

					if (y < builderData.height - 1) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.topOffset].getType() == VoxelType.AIR) createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (y > 0) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.bottomOffset].getType() == VoxelType.AIR) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x > 0) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.leftOffset].getType() == VoxelType.AIR) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x < builderData.width - 1) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.rightOffset].getType() == VoxelType.AIR) createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z > 0) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.frontOffset].getType() == VoxelType.AIR) createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z < builderData.depth - 1) {
						if (builderData.chunk.voxels[i + builderData.chunk.chunkMesh.backOffset].getType() == VoxelType.AIR) createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
				}
			}
		}
		return builderData.vertexOffset / VERTEX_SIZE;
	}

	private static void createTop(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, Voxel voxel) {
		float topOffset = 0;

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);
	}

	private static void createBottom(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, Voxel voxel) {
		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(-1);
		builderData.append(0);
		addColorToVertices(builderData, voxel);
	}

	private static void createLeft(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, Voxel voxel) {
		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);
	}

	private static void createRight(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, Voxel voxel) {
		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		addColorToVertices(builderData, voxel);
	}

	private static void createFront(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, Voxel voxel) {
		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE));
		builderData.append(0);
		builderData.append(0);
		builderData.append(VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);
	}

	private static void createBack(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, Voxel voxel) {
		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);

		builderData.append(offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(offset.y + (y * VoxelUtils.VOXEL_SIZE));
		builderData.append(offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE);
		builderData.append(0);
		builderData.append(0);
		builderData.append(-VoxelUtils.VOXEL_SIZE);
		addColorToVertices(builderData, voxel);
	}

	private static void addColorToVertices(ChunkBuilderData builderData, Voxel voxel) {
		Color color = voxel.getType().getProperties().getColor();
		if (USE_PACKED_COLOR) {
			builderData.append(color.toFloatBits());
//			return 1;
		} else {
			builderData.append(color.r);
			builderData.append(color.g);
			builderData.append(color.b);
			builderData.append(color.a);
//			return 4;
		}
	}
}
