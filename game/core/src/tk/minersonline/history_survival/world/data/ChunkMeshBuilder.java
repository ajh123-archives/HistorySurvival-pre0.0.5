package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.world.Chunk;
import tk.minersonline.history_survival.world.utils.VoxelUtils;
import tk.minersonline.history_survival.world.voxels.Voxel;
import tk.minersonline.history_survival.world.voxels.VoxelType;

import static tk.minersonline.history_survival.world.data.ChunkMesh.USE_PACKED_COLOR;
import static tk.minersonline.history_survival.world.data.ChunkMesh.VERTEX_SIZE;

public class ChunkMeshBuilder {
	int width;
	int height;
	int depth;
	Chunk chunk;

	private ChunkMeshBuilder(int width, int height, int depth, Chunk chunk) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.chunk = chunk;
	}

	public static ChunkMesh build(Chunk chunk) {
		ChunkMeshBuilder builder = new ChunkMeshBuilder(chunk.width, chunk.height, chunk.depth, chunk);

		Mesh mesh = chunk.chunkMesh.mesh;
		Mesh transparentMesh = chunk.chunkMesh.transparentMesh;

		int numVerts = builder.calculateVertices(chunk.chunkMesh.vertices);
		int vertexSize = mesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
		chunk.chunkMesh.numVertices = numVerts / 4 * vertexSize;
		mesh.setVertices(chunk.chunkMesh.vertices, 0, numVerts * VERTEX_SIZE);

		int transparentNumVerts = builder.calculateTransparentVertices(chunk.chunkMesh.transparentVertices);
		int transparentVertexSize = transparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
		chunk.chunkMesh.numTransparentVertices = transparentNumVerts / 4 * transparentVertexSize;
		transparentMesh.setVertices(chunk.chunkMesh.transparentVertices, 0, transparentNumVerts * VERTEX_SIZE);

		return chunk.chunkMesh;
	}

	/** Creates a mesh out of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	private int calculateVertices (float[] vertices) {
		int i = 0;
		int vertexOffset = 0;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++, i++) {
					Voxel voxel = chunk.voxels[i];
					if (voxel.getType() == VoxelType.AIR) continue;
					if (voxel.getType().getProperties().isTransparent()) continue;

					if (y < height - 1) {
						if (chunk.voxels[i + chunk.chunkMesh.topOffset].getType() == VoxelType.AIR) vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
						if (chunk.voxels[i + chunk.chunkMesh.topOffset].getType().getProperties().isTransparent()) vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (chunk.voxels[i + chunk.chunkMesh.bottomOffset].getType() == VoxelType.AIR) vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
						if (chunk.voxels[i + chunk.chunkMesh.bottomOffset].getType().getProperties().isTransparent()) vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (chunk.voxels[i + chunk.chunkMesh.leftOffset].getType() == VoxelType.AIR) vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
						if (chunk.voxels[i + chunk.chunkMesh.leftOffset].getType().getProperties().isTransparent()) vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (chunk.voxels[i + chunk.chunkMesh.rightOffset].getType() == VoxelType.AIR) vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
						if (chunk.voxels[i + chunk.chunkMesh.rightOffset].getType().getProperties().isTransparent()) vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (chunk.voxels[i + chunk.chunkMesh.frontOffset].getType() == VoxelType.AIR) vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
						if (chunk.voxels[i + chunk.chunkMesh.frontOffset].getType().getProperties().isTransparent()) vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (chunk.voxels[i + chunk.chunkMesh.backOffset].getType() == VoxelType.AIR) vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
						if (chunk.voxels[i + chunk.chunkMesh.backOffset].getType().getProperties().isTransparent()) vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	/** Creates a mesh out of the transparent parts of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	private int calculateTransparentVertices(float[] vertices) {
		int i = 0;
		int vertexOffset = 0;
		for (int y = 0; y < height; y++) {
			for (int z = 0; z < depth; z++) {
				for (int x = 0; x < width; x++, i++) {
					Voxel voxel = chunk.voxels[i];
					if (!voxel.getType().getProperties().isTransparent()) continue;

					if (y < height - 1) {
						if (chunk.voxels[i + chunk.chunkMesh.topOffset].getType() == VoxelType.AIR) vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (y > 0) {
						if (chunk.voxels[i + chunk.chunkMesh.bottomOffset].getType() == VoxelType.AIR) vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x > 0) {
						if (chunk.voxels[i + chunk.chunkMesh.leftOffset].getType() == VoxelType.AIR) vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (x < width - 1) {
						if (chunk.voxels[i + chunk.chunkMesh.rightOffset].getType() == VoxelType.AIR) vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z > 0) {
						if (chunk.voxels[i + chunk.chunkMesh.frontOffset].getType() == VoxelType.AIR) vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
					if (z < depth - 1) {
						if (chunk.voxels[i + chunk.chunkMesh.backOffset].getType() == VoxelType.AIR) vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					} else {
						vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, voxel);
					}
				}
			}
		}
		return vertexOffset / VERTEX_SIZE;
	}

	private static int createTop(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
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

	private static int createBottom(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
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

	private static int createLeft(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
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

	private static int createRight(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
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

	private static int createFront(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
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

	private static int createBack(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, Voxel voxel) {
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
