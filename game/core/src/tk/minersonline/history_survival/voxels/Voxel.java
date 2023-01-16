package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.math.Vector3;

public class Voxel {
	private final VoxelType type;
	private final Vector3 position;
	private final VoxelWorld world;

	public static final float VOXEL_SIZE = 0.3f;

	Voxel(VoxelType type, Vector3 position, VoxelWorld world) {
		this.type = type;
		this.position = position;
		this.world = world;
	}

	public VoxelWorld getWorld() {
		return world;
	}

	public VoxelType getType() {
		return type;
	}

	public Vector3 getPosition() {
		return position;
	}
	public static Vector3 toRealPos(Vector3 voxelPos) {
		return voxelPos.scl(VOXEL_SIZE);
	}
}
