package tk.minersonline.history_survival.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import tk.minersonline.history_survival.componments.VoxelChunkComponent;
import tk.minersonline.history_survival.componments.VoxelEntity;

import static tk.minersonline.history_survival.systems.VoxelWorld.*;

public class WorldRenderer extends IteratingSystem implements RenderableProvider {
	ComponentMapper<VoxelChunkComponent> chunks;
	private final VoxelWorld world;

	public WorldRenderer(VoxelWorld world) {
		super(Family.all(VoxelChunkComponent.class).get());
		this.world = world;
		chunks = ComponentMapper.getFor(VoxelChunkComponent.class);

		int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += (short) 4) {
			indices[i + 0] = (short)(j + 0);
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = (short)(j + 0);
		}

		VertexAttribute colorAttribute = VoxelChunkComponent.USE_PACKED_COLOR ? VertexAttribute.ColorPacked() : VertexAttribute.ColorUnpacked();
		VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), colorAttribute);

		int chunkIndex = 0;
		for (int y = 0; y < world.chunksY; y++) {
			for (int z = 0; z < world.chunksZ; z++) {
				for (int x = 0; x < world.chunksX; x++) {
					VoxelChunkComponent chunk = new VoxelChunkComponent(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, world);
					chunk.offset.set(
							(x * VoxelEntity.VOXEL_SIZE) * CHUNK_SIZE_X,
							(y * VoxelEntity.VOXEL_SIZE) * CHUNK_SIZE_Y,
							(z * VoxelEntity.VOXEL_SIZE) * CHUNK_SIZE_Z
					);
					chunk.indices = indices;
					chunk.mesh = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
							CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
					chunk.mesh.setIndices(indices);
					chunk.transparentMesh = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
							CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
					chunk.transparentMesh.setIndices(indices);
					chunk.dirty = true;
					chunk.numVertices = 0;
					chunk.numTransparentVertices = 0;
					chunk.material = new Material();
					world.chunks[chunkIndex++] = chunk;
				}
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		VoxelChunkComponent chunk = chunks.get(entity);
		if (chunk.dirty) {
			Mesh mesh = chunk.mesh;
			Mesh transparentMesh = chunk.transparentMesh;

			int numVerts = chunk.calculateVertices(chunk.vertices);
			int vertexSize = mesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
			chunk.numVertices = numVerts / 4 * vertexSize;
			mesh.setVertices(chunk.vertices, 0, numVerts * VoxelChunkComponent.VERTEX_SIZE);

			int transparentNumVerts = chunk.calculateTransparentVertices(chunk.transparentVertices);
			int transparentVertexSize = transparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
			chunk.numTransparentVertices = transparentNumVerts / 4 * transparentVertexSize;
			transparentMesh.setVertices(chunk.transparentVertices, 0, transparentNumVerts * VoxelChunkComponent.VERTEX_SIZE);
			chunk.dirty = false;
		}
	}

	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		for (VoxelChunkComponent chunk : world.chunks) {
			if (chunk.numVertices != 0) {
				Renderable renderable = pool.obtain();
				renderable.material = chunk.material;
				renderable.meshPart.mesh = chunk.mesh;
				renderable.meshPart.offset = 0;
				renderable.meshPart.size = chunk.numVertices;
				renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
				renderables.add(renderable);
			}
		}
		for (VoxelChunkComponent chunk : world.chunks) {
			if (chunk.numTransparentVertices != 0) {
				Renderable transparentRenderable = pool.obtain();
				Material transparent = chunk.material;
				transparent.set(FloatAttribute.createAlphaTest(0.2f));
				transparent.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

				transparentRenderable.material = transparent;
				transparentRenderable.meshPart.mesh = chunk.transparentMesh;
				transparentRenderable.meshPart.offset = 0;
				transparentRenderable.meshPart.size = chunk.numTransparentVertices;
				transparentRenderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
				renderables.add(transparentRenderable);
			}
		}
	}
}
