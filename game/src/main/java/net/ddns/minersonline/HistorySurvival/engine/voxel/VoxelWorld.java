package net.ddns.minersonline.HistorySurvival.engine.voxel;

import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class VoxelWorld {
	private final Map<Vector3f, Voxel> voxels = new ConcurrentHashMap<>();
	private transient final List<Voxel> visible_voxels = new CopyOnWriteArrayList<>();
	private transient volatile boolean generating = true;

	public void setVoxel(Vector3f pos, Voxel voxel){
		voxels.put(pos, voxel);
	}

	public Voxel getVoxel(Vector3f pos){
		return voxels.get(pos);
	}

	public void start(TransformComponent player){
		generating = true;
		int chunkDistance = 20*2;
		new Thread(() -> {
			while (generating) {
				Vector3f position = player.position;
				for (int x = (int) (position.x - chunkDistance); x < (int) position.x; x++) {
					for (int z = (int) position.z; z < (int) (position.z + chunkDistance); z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (voxels.get(pos) == null) {
							voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
						if (!visible_voxels.contains(getVoxel(pos))){
							visible_voxels.add(getVoxel(pos));
						}
					}
				}
				for (int x = (int) position.x; x < (int) (position.x + chunkDistance); x++) {
					for (int z = (int) position.z; z < (int) (position.z + chunkDistance); z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (voxels.get(pos) == null) {
							voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
						if (!visible_voxels.contains(getVoxel(pos))){
							visible_voxels.add(getVoxel(pos));
						}
					}
				}
			}
		}).start();
		new Thread(() -> {
			while (generating) {
				Vector3f position = player.position;
				for (int x = (int) (position.x - chunkDistance); x < (int) position.x; x++) {
					for (int z = (int) (position.z - chunkDistance); z < (int) position.z; z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (voxels.get(pos) == null) {
							voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
						if (!visible_voxels.contains(getVoxel(pos))){
							visible_voxels.add(getVoxel(pos));
						}
					}
				}
				for (int x = (int) position.x; x < (int) (position.x + chunkDistance); x++) {
					for (int z = (int) (position.z - chunkDistance); z < (int) position.z; z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (voxels.get(pos) == null) {
							voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
						if (!visible_voxels.contains(getVoxel(pos))){
							visible_voxels.add(getVoxel(pos));
						}
					}
				}
			}
		}).start();
		new Thread(() -> {
			while (generating) {
				for (Voxel voxel : visible_voxels) {
					Vector3f pos = voxel.getPosition();
					if (player == null){continue;}
					int distX = (int) (player.position.x - pos.x);
					int distZ = (int) (player.position.z - pos.z);

					if (distX < 0){
						distX = -distX;
					}
					if (distZ < 0){
						distZ = -distZ;
					}

					if ((distX > chunkDistance) || ( distZ > chunkDistance)){
						visible_voxels.remove(voxel);
					}
				}
			}
		}).start();
	}

	public void stop(){
		generating = false;
	}

	public Collection<Voxel> getVisible(){
		return visible_voxels;
	}
}
