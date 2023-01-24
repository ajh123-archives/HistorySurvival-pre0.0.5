package tk.minersonline.history_survival.componments;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	private final Vector3 pos;
	private final float scale;

	public TransformComponent(Vector3 pos, float scale) {
		this.pos = pos;
		this.scale = scale;
	}

	public TransformComponent(Vector3 pos) {
		pos.x = pos.x + VoxelEntity.VOXEL_SCALE * VoxelEntity.VOXEL_SCALE;
		pos.y = pos.y + VoxelEntity.VOXEL_SCALE * VoxelEntity.VOXEL_SCALE;
		pos.z = pos.z + VoxelEntity.VOXEL_SCALE * VoxelEntity.VOXEL_SCALE;

		this.pos = pos;
		this.scale = VoxelEntity.VOXEL_SCALE;
	}

	public Vector3 getPos() {
		return pos;
	}

	public float getScale() {
		return scale;
	}
}
