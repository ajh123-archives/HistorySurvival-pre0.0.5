package tk.minersonline.history_survival.world.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import tk.minersonline.history_survival.world.Chunk;
import tk.minersonline.history_survival.world.data.ChunkMeshBuilder;
import tk.minersonline.history_survival.world.utils.VoxelUtils;
import tk.minersonline.history_survival.world.World;

import static tk.minersonline.history_survival.world.World.*;

public class WorldRenderer extends IteratingSystem implements RenderableProvider {
	ComponentMapper<Chunk> chunks;
	private final World world;

	public WorldRenderer(World world) {
		super(Family.all(Chunk.class).get());
		this.world = world;
		chunks = ComponentMapper.getFor(Chunk.class);

		int chunkIndex = 0;
		for (int y = 0; y < world.chunksY; y++) {
			for (int z = 0; z < world.chunksZ; z++) {
				for (int x = 0; x < world.chunksX; x++) {
					Chunk chunk = new Chunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, world);
					chunk.offset.set(
							(x * VoxelUtils.VOXEL_SIZE) * CHUNK_SIZE_X,
							(y * VoxelUtils.VOXEL_SIZE) * CHUNK_SIZE_Y,
							(z * VoxelUtils.VOXEL_SIZE) * CHUNK_SIZE_Z
					);
					chunk.initMesh();
					chunk.dirty = true;
					world.chunks[chunkIndex++] = chunk;
				}
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Chunk chunk = chunks.get(entity);
		if (chunk.dirty) {
			ChunkMeshBuilder.build(chunk);
			chunk.dirty = false;
		}
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for (Chunk chunk : world.chunks) {
			if (chunk.chunkMesh.numVertices != 0) {
				chunk.chunkMesh.getRenderables(renderables, pool);
			}
		}
		for (Chunk chunk : world.chunks) {
			if (chunk.chunkMesh.numTransparentVertices != 0) {
				chunk.chunkMesh.getTransparentRenderables(renderables, pool);
			}
		}
	}
}
