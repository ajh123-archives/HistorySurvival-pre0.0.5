package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import tk.minersonline.history_survival.HistorySurvival;
import tk.minersonline.history_survival.world.Chunk;
import tk.minersonline.history_survival.world.utils.VoxelUtils;
import tk.minersonline.history_survival.world.voxels.VoxelType;

import static tk.minersonline.history_survival.world.World.*;
import static tk.minersonline.history_survival.world.data.ChunkMesh.USE_PACKED_COLOR;
import static tk.minersonline.history_survival.world.data.ChunkMesh.VERTEX_SIZE;

public class ChunkMeshBuilder {
	public static ChunkMesh build(Chunk chunk, ChunkBuilderData builderData) {
		builderData.begin(chunk);
		int numVerts = ChunkMeshBuilder.calculateVertices(builderData);
		int vertexSize = chunk.chunkMesh.mesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
		chunk.chunkMesh.numVertices = numVerts / 4 * vertexSize;
		chunk.chunkMesh.mesh.setVertices(builderData.vertices, 0, numVerts * VERTEX_SIZE);



//		int colNumVerts = builderData.vertexOffset / 3;
//		int colVertexSize = chunk.chunkMesh.collisionMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
//		chunk.chunkMesh.numRawVertices = colNumVerts / 4 * colVertexSize;
//		chunk.chunkMesh.collisionMesh.setVertices(builderData.rawVertices, 0, colNumVerts * 3);
////		System.out.println(colNumVerts * 6);
//		if ((colNumVerts*3) != 0) {
//			MeshPart part = new MeshPart(null, chunk.chunkMesh.collisionMesh, 0, colNumVerts * 3, GL20.GL_TRIANGLES);
//			chunk.collision.add(part);
//			chunk.collisionInfo = new btRigidBody.btRigidBodyConstructionInfo(
//					0,
//					null,
//					new btBvhTriangleMeshShape(chunk.collision),
//					Vector3.Zero
//			);
//			chunk.body = new btRigidBody(chunk.collisionInfo);
//			chunk.body.translate(chunk.offset);
//			HistorySurvival.INSTANCE.bulletPhysicsSystem.addBody(chunk.body);
//		}



		builderData.begin(chunk);
		int transparentNumVerts = ChunkMeshBuilder.calculateTransparentVertices(builderData);
		int transparentVertexSize = chunk.chunkMesh.transparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
		chunk.chunkMesh.numTransparentVertices = transparentNumVerts / 4 * transparentVertexSize;
		chunk.chunkMesh.transparentMesh.setVertices(builderData.vertices, 0, transparentNumVerts * VERTEX_SIZE);

//		int transColNumVerts = builderData.rawVertexOffset / 3;
//		int transColVertexSize = chunk.chunkMesh.collisionTransparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
//		chunk.chunkMesh.numRawTransparentVertices = transColNumVerts / 3 * transColVertexSize;
//		chunk.chunkMesh.collisionTransparentMesh.setVertices(builderData.rawVertices, 0, builderData.rawVertexOffset);
//		if (builderData.rawVertexOffset % 3 != 0) {
//			MeshPart part2 = new MeshPart(null, chunk.chunkMesh.collisionTransparentMesh, 0, builderData.rawVertexOffset, GL20.GL_TRIANGLES);
//			chunk.transCollision.add(part2);
//			chunk.transCollisionInfo = new btRigidBody.btRigidBodyConstructionInfo(
//					0,
//					null,
//					new btBvhTriangleMeshShape(chunk.transCollision),
//					Vector3.Zero
//			);
//			chunk.transparentBody = new btRigidBody(chunk.transCollisionInfo);
//			chunk.transparentBody.translate(chunk.offset);
//			HistorySurvival.INSTANCE.bulletPhysicsSystem.addBody(chunk.transparentBody);
//		}

		return chunk.chunkMesh;
	}

	/** Creates a mesh out of the chunk, returning the number of indices produced
	 * @return the number of vertices produced */
	static int calculateVertices(ChunkBuilderData builderData) {
		int i = 0;

		for (int y = 0; y < builderData.height; y++) {
			for (int z = 0; z < builderData.depth; z++) {
				for (int x = 0; x < builderData.width; x++, i++) {
					VoxelType voxel = builderData.chunk.get(i);
					if (voxel == VoxelType.AIR) continue;
					if (voxel.getProperties().isTransparent()) continue;

					if (y < builderData.height - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.topOffset) == VoxelType.AIR) createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.topOffset).getProperties().isTransparent()) createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (y > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.bottomOffset) == VoxelType.AIR) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.bottomOffset).getProperties().isTransparent()) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.leftOffset) == VoxelType.AIR) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.leftOffset).getProperties().isTransparent()) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x < builderData.width - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.rightOffset) == VoxelType.AIR) createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.rightOffset).getProperties().isTransparent()) createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.frontOffset) == VoxelType.AIR) createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.frontOffset).getProperties().isTransparent()) createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z < builderData.depth - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.backOffset) == VoxelType.AIR) createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.backOffset).getProperties().isTransparent()) createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
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
					VoxelType voxel = builderData.chunk.get(i);
					if (!voxel.getProperties().isTransparent()) continue;

					if (y < builderData.height - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.topOffset) == VoxelType.AIR) createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createTop(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (y > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.bottomOffset) == VoxelType.AIR) createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBottom(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.leftOffset) == VoxelType.AIR) createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createLeft(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (x < builderData.width - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.rightOffset) == VoxelType.AIR) createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createRight(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z > 0) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.frontOffset) == VoxelType.AIR) createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createFront(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
					if (z < builderData.depth - 1) {
						if (builderData.chunk.get(i + builderData.chunk.chunkMesh.backOffset) == VoxelType.AIR) createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
					} else {
						createBack(builderData.chunk.offset, x, y, z, builderData, voxel);
					}
				}
			}
		}
		return builderData.vertexOffset / VERTEX_SIZE;
	}

	private static void createTop(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel) {
		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE ;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE ;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);
	}

	private static void createBottom(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel) {
		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-1;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);
	}

	private static void createLeft(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel) {
		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);
	}

	private static void createRight(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel) {
		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		addColorToVertices(builderData, voxel);
	}

	private static void createFront(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel) {
		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);
	}

	private static void createBack(Vector3 offset, int x, int y, int z, ChunkBuilderData builderData, VoxelType voxel) {
		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);

		builderData.vertices[builderData.vertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.vertices[builderData.vertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.x + (x * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.y + (y * VoxelUtils.VOXEL_SIZE);
		builderData.rawVertices[builderData.rawVertexOffset++] =offset.z + (z * VoxelUtils.VOXEL_SIZE) + VoxelUtils.VOXEL_SIZE;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =0;
		builderData.vertices[builderData.vertexOffset++] =-VoxelUtils.VOXEL_SIZE;
		addColorToVertices(builderData, voxel);
	}

	private static void addColorToVertices(ChunkBuilderData builderData, VoxelType voxel) {
		Color color = voxel.getProperties().getColor();
		if (USE_PACKED_COLOR) {
			builderData.vertices[builderData.vertexOffset++] =color.toFloatBits();
		} else {
			builderData.vertices[builderData.vertexOffset++] =color.r;
			builderData.vertices[builderData.vertexOffset++] =color.g;
			builderData.vertices[builderData.vertexOffset++] =color.b;
			builderData.vertices[builderData.vertexOffset++] =color.a;
		}
	}
}
