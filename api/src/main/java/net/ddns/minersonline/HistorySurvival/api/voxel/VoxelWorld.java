package net.ddns.minersonline.HistorySurvival.api.voxel;

import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import net.ddns.minersonline.HistorySurvival.api.registries.VoxelType;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelWorld {
	private final Map<Vector3f, VoxelChunk> chunks = new ConcurrentHashMap<>();
	private transient final List<Map<VoxelChunk, VoxelChunkMesh>> visible_models = Collections.synchronizedList(new ArrayList<>());
	private transient volatile boolean generating = true;

	public void setChunk(Vector3f pos, VoxelChunk chunk){
		chunks.put(pos, chunk);
	}

	public VoxelChunk getChunk(Vector3f chunkPos){
		return chunks.get(chunkPos);
	}

	public VoxelChunk getWorldChunk(Vector3f worldPos){
		Vector3f chunkPos = new Vector3f(
				(float) Math.floor(worldPos.x/VoxelChunk.CHUNK_SIZE),
				0,
				(float) Math.floor(worldPos.z/VoxelChunk.CHUNK_SIZE)
		);

		return chunks.get(chunkPos);
	}

	public Voxel getBlock(Vector3f pos){
		Vector3f pos2 = new Vector3f(pos);
		pos2.x = (int) Math.floor((int) pos2.x);
		pos2.y = (int) Math.floor((int) pos2.y);
		pos2.z = (int) Math.floor((int) pos2.z);

		VoxelChunk chunk = getWorldChunk(pos);
		if (chunk == null) { return null; }

		for (Voxel voxel : chunk.getVoxels().values()){
			if (voxel.getPosition().x == pos2.x){
				if (voxel.getPosition().y == pos2.y) {
					if (voxel.getPosition().z == pos2.z){
						return voxel;
					}
				}
			}
		}
		return null;
	}

	public void start(TransformComponent player){
		generating = true;
		int chunkDistance = 4;
		int chunkSize = VoxelChunk.CHUNK_SIZE;
		new Thread(() -> {
			while (generating) {
				Vector3f position = player.position;
				for (int x = (int) (position.x-chunkDistance*chunkSize)/chunkSize; x < (int) (position.x+chunkDistance*chunkSize)/chunkSize; x++) {
					for (int z = (int) (position.z-chunkDistance*chunkSize)/chunkSize; z < (int) (position.z+chunkDistance*chunkSize)/chunkSize; z++) {
						Vector3f chunkPos = new Vector3f(x, 0, z);
						if (getChunk(chunkPos) == null) {
							Map<Vector3f, Voxel> voxels = new HashMap<>();
							for (int cx = 0; cx < chunkSize+1; cx++) {
								for (int cz = 0; cz < chunkSize+1; cz++) {
									Vector3f pos = new Vector3f((x * chunkSize)+cx, 0, (z * chunkSize)+cz);
									voxels.put(pos, VoxelType.GRASS.create(pos));
								}
							}
							chunks.put(chunkPos, new VoxelChunk(voxels, chunkPos));
						}
						if (chunks.get(chunkPos) != null) {
							synchronized (visible_models) {
								VoxelChunk chunk = chunks.get(chunkPos);
								boolean contained = false;
								for (Map<VoxelChunk, VoxelChunkMesh> meshes : visible_models) {
									for (VoxelChunk chunk2 : meshes.keySet()) {
										if (chunk.getOrigin() == chunk2.getOrigin()) {
											contained = true;
											break;
										}
									}
								}

								if (!contained) {
									int index = visible_models.size();
									Map<VoxelChunk, VoxelChunkMesh> chunkMeshMap = new HashMap<>();
									VoxelChunkMesh mesh = new VoxelChunkMesh(chunk);
									mesh.index = index;
									chunkMeshMap.put(chunk, mesh);
									visible_models.add(mesh.index, chunkMeshMap);
								}
							}
						}
					}
				}
			}
		}).start();

		new Thread(() -> {
			while (generating) {
				synchronized (visible_models) {
					for (Map<VoxelChunk, VoxelChunkMesh> meshes : visible_models) {
						for (VoxelChunk chunk : meshes.keySet()) {
							VoxelChunkMesh mesh = meshes.get(chunk);
							if (mesh == null) {
								continue;
							}
							Vector3f pos = mesh.chunk.getCenter();

							if (player == null) {
								continue;
							}
							int distX = (int) (player.position.x - (pos.x));
							int distZ = (int) (player.position.z - (pos.z));

							if (distX < 0) {
								distX = -distX;
							}
							if (distZ < 0) {
								distZ = -distZ;
							}

							if ((distX > (chunkDistance * VoxelChunk.CHUNK_SIZE)) || (distZ > (chunkDistance * VoxelChunk.CHUNK_SIZE))) {
								visible_models.get(mesh.index).remove(chunk);
							}
						}
					}
				}
			}
		}).start();
	}

	public void stop(){
		generating = false;
	}

	public Map<TexturedModel, Collection<VoxelChunkMesh>> getVisible(){
		Map<TexturedModel, Collection<VoxelChunkMesh>> result = new HashMap<>();
		synchronized (visible_models) {
			for (Map<VoxelChunk, VoxelChunkMesh> meshes : visible_models) {
				for (VoxelChunk chunk : meshes.keySet()) {
					VoxelChunkMesh mesh = meshes.get(chunk);
					if (mesh != null) {
						if (mesh.model == null) {
							RawModel model = GameHook.getLoader().loadToVao(mesh.positions, mesh.uvs);

							ModelTexture texture = GameHook.getLoader().getTextureAtlas();
							mesh.model = new TexturedModel(model, texture);
							mesh.positions = null;
							mesh.uvs = null;
							mesh.normals = null;
						}

						result.computeIfAbsent(mesh.model, k -> new ArrayList<>());
						result.get(mesh.model).add(mesh);
					}
				}
			}
		}
		return result;
	}
}
