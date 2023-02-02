package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.world.Chunk;
import tk.minersonline.history_survival.world.utils.VoxelUtils;
import tk.minersonline.history_survival.world.voxels.VoxelType;

import static tk.minersonline.history_survival.world.data.ChunkMesh.USE_PACKED_COLOR;
import static tk.minersonline.history_survival.world.data.ChunkMesh.VERTEX_SIZE;

public class ChunkMeshBuilder {
	public static ChunkMesh build(Chunk chunk, ChunkBuilderData builderData) {
		builderData.begin(chunk);
		Mesh mesh = chunk.chunkMesh.mesh;
		int numVerts = ChunkMeshBuilder.calculateVertices(builderData, builderData.vertices);
		int vertexSize = mesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
		chunk.chunkMesh.numVertices = numVerts / 4 * vertexSize;
		mesh.setVertices(builderData.vertices, 0, numVerts * VERTEX_SIZE);

		builderData.begin(chunk);
		Mesh transparentMesh = chunk.chunkMesh.transparentMesh;
		int transparentNumVerts = ChunkMeshBuilder.calculateTransparentVertices(builderData, builderData.vertices);
		int transparentVertexSize = transparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
		chunk.chunkMesh.numTransparentVertices = transparentNumVerts / 4 * transparentVertexSize;
		transparentMesh.setVertices(builderData.vertices, 0, transparentNumVerts * VERTEX_SIZE);

		return chunk.chunkMesh;
	}

	/** Creates a mesh out of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	static int calculateVertices(ChunkBuilderData builderData, float[] vertices) {
		int i = 0;

		for (int y = 0; y < builderData.height; y++) {
			for (int z = 0; z < builderData.depth; z++) {
				for (int x = 0; x < builderData.width; x++, i++) {
					VoxelType voxel = builderData.chunk.get(i);
					if (voxel == VoxelType.AIR) continue;
					if (voxel.getProperties().isTransparent()) continue;

					if (y < builderData.height - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.topOffset) == VoxelType.AIR) createTop(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.topOffset).getProperties().isTransparent()) createTop(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createTop(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (y > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.bottomOffset) == VoxelType.AIR) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.bottomOffset).getProperties().isTransparent()) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createBottom(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (x > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.leftOffset) == VoxelType.AIR) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.leftOffset).getProperties().isTransparent()) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createLeft(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (x < builderData.width - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.rightOffset) == VoxelType.AIR) createRight(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.rightOffset).getProperties().isTransparent()) createRight(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createRight(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (z > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.frontOffset) == VoxelType.AIR) createFront(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.frontOffset).getProperties().isTransparent()) createFront(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createFront(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (z < builderData.depth - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.backOffset) == VoxelType.AIR) createBack(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.backOffset).getProperties().isTransparent()) createBack(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createBack(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
				}
			}
		}
		return builderData.vertexOffset / VERTEX_SIZE;
	}

	/** Creates a mesh out of the transparent parts of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	static int calculateTransparentVertices(ChunkBuilderData builderData, float[] vertices) {
		int i = 0;

		for (int y = 0; y < builderData.height; y++) {
			for (int z = 0; z < builderData.depth; z++) {
				for (int x = 0; x < builderData.width; x++, i++) {
					VoxelType voxel = builderData.chunk.get(i);
					if (!voxel.getProperties().isTransparent()) continue;

					if (y < builderData.height - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.topOffset) == VoxelType.AIR) createTop(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createTop(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (y > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.bottomOffset) == VoxelType.AIR) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createBottom(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (x > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.leftOffset) == VoxelType.AIR) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createLeft(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (x < builderData.width - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.rightOffset) == VoxelType.AIR) createRight(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createRight(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (z > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.frontOffset) == VoxelType.AIR) createFront(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createFront(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
					if (z < builderData.depth - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.backOffset) == VoxelType.AIR) createBack(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					} else {
						createBack(builderData.chunk.offset, x, y, z, builderData, voxel, vertices);
					}
				}
			}
		}
		return builderData.vertexOffset / VERTEX_SIZE;
	}

	private static void createTop(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		float topOffset = 0;

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE + topOffset;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);
	}

	private static void createBottom(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-1;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);
	}

	private static void createLeft(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);
	}

	private static void createRight(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel, vertices);
	}

	private static void createFront(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);
	}

	private static void createBack(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);

		vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =0;
		vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel, vertices);
	}

	private static void addColorToVertices(ChunkBuilderData builderData, VoxelType voxel, float[] vertices) {
		Color color = voxel.getProperties().getColor();
		if (USE_PACKED_COLOR) {
			vertices[builderData.vertexOffset++] =color.toFloatBits();
//			return 1;
		} else {
			vertices[builderData.vertexOffset++] =color.r;
			vertices[builderData.vertexOffset++] =color.g;
			vertices[builderData.vertexOffset++] =color.b;
			vertices[builderData.vertexOffset++] =color.a;
//			return 4;
		}
	}
}
