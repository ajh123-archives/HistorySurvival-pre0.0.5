package net.ddns.minersonline.HistorySurvival.engine.voxel;

import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoxelWorld {
	private final Map<Vector3f, VoxelChunk> chunks = new ConcurrentHashMap<>();
	private transient final List<VoxelChunk> visible_chunks = new CopyOnWriteArrayList<>();
	private transient volatile boolean generating = true;

	public void setChunk(Vector3f pos, VoxelChunk chunk){
		chunks.put(pos, chunk);
	}

	public VoxelChunk getChunk(Vector3f pos){
		return chunks.get(pos);
	}

	public Voxel getBlock(Vector3f pos){
		int chunkSize = 20;
		Vector3f chunkPos = new Vector3f(
				(int) Math.floor(Math.floor(pos.x)/chunkSize),
				0,
				(int) Math.floor(Math.floor(pos.z)/chunkSize)
		);
		VoxelChunk chunk = getChunk(chunkPos);
		if (chunk == null) { return null; }
		return chunk.getVoxels().get(chunkPos);
	}

	public void start(TransformComponent player){
		generating = true;
		int chunkDistance = 5;
		int chunkSize = 20;
		new Thread(() -> {
			while (generating) {
				Vector3f position = player.position;
				for (int x = (int) (position.x-chunkDistance*chunkSize)/chunkSize; x < (int) (position.x+chunkDistance*chunkSize)/chunkSize; x++) {
					for (int z = (int) (position.z-chunkDistance*chunkSize)/chunkSize; z < (int) (position.z+chunkDistance*chunkSize)/chunkSize; z++) {
						Vector3f chunkPos = new Vector3f(x * chunkSize, 0, z * chunkSize);
						if (getChunk(chunkPos) == null) {
							Map<Vector3f, Voxel> voxels = new HashMap<>();
							for (int cx = 0; cx < chunkSize; cx++) {
								for (int cz = 0; cz < chunkSize; cz++) {
									Vector3f pos = new Vector3f((x * chunkSize)+cx, 0, (z * chunkSize)+cz);
									voxels.put(pos, new Voxel(
											ModelType.GRASS_MODEL.getRegistryName(), pos
									));
								}
							}
							chunks.put(chunkPos, new VoxelChunk(voxels, chunkPos));
						}

						if (!visible_chunks.contains(getChunk(chunkPos))) {
							visible_chunks.add(getChunk(chunkPos));
						}
					}
				}
			}
		}).start();

		new Thread(() -> {
			while (generating) {
				for (VoxelChunk chunk : visible_chunks) {
					Vector3f pos = chunk.getOrigin();
					if (player == null){continue;}
					int distX = (int) (player.position.x - pos.x);
					int distZ = (int) (player.position.z - pos.z);

					if (distX < 0){
						distX = -distX;
					}
					if (distZ < 0){
						distZ = -distZ;
					}

					if ((distX > chunkDistance*chunkSize) || ( distZ > chunkDistance*chunkSize)){
						visible_chunks.remove(chunk);
					}
				}
			}
		}).start();
	}

	public void stop(){
		generating = false;
	}

	public Map<TexturedModel, Collection<Voxel>> getVisible(){
		Map<TexturedModel, Collection<Voxel>> result = new ConcurrentHashMap<>();
		for (VoxelChunk chunk : visible_chunks) {
			for (Voxel voxel : chunk.getVoxels().values()) {
				result.computeIfAbsent(voxel.getModel(), k -> new ArrayList<>());
				result.get(voxel.getModel()).add(voxel);
			}
		}
		return result;
	}
}
