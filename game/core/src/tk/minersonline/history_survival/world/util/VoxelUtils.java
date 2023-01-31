package tk.minersonline.history_survival.world.util;

import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.componments.TransformComponent;

public class VoxelUtils {
	public static final float VOXEL_SIZE = 0.3f;
	public static final float VOXEL_SCALE = ((VOXEL_SIZE  / (VOXEL_SIZE*100))*VOXEL_SIZE) * 0.5f;

	public static Vector3 toRealPos(Vector3 voxelPos) {
		return new Vector3(
			(float) ((Math.floor(voxelPos.x) * VOXEL_SIZE) + (VOXEL_SIZE/2f)),
			(float) ((Math.floor(voxelPos.y) * VOXEL_SIZE) + (VOXEL_SIZE/2f)),
			(float) ((Math.floor(voxelPos.z) * VOXEL_SIZE) + (VOXEL_SIZE/2f))
		);
	}
	public static Vector3 toVoxelPos(Vector3 realPos) {
		return new Vector3(
				realPos.x / VOXEL_SIZE,
				realPos.y / VOXEL_SIZE,
				realPos.z / VOXEL_SIZE
		);
	}

	public static float toRealScale(float voxelPos) {
		return  (float) ((Math.floor(voxelPos) * VOXEL_SIZE) + (VOXEL_SIZE / 2f));
	}

	public static TransformComponent realScaledTransform(Vector3 voxelPos) {
		return new TransformComponent(toRealPos(voxelPos), VOXEL_SCALE);
	}
}
