package tk.minersonline.history_survival.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.voxels.VoxelType;
import tk.minersonline.history_survival.voxels.VoxelWorld;

import java.util.List;

public class VoxelComponent implements Component {
	private final VoxelType type;
	private final Vector3 position;
	private final VoxelWorld world;
	private float debounceSound = 0;

	public static final float VOXEL_SIZE = 0.3f;

	public VoxelComponent(VoxelType type, Vector3 position, VoxelWorld world) {
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

	public static Vector3 toVoxelPos(Vector3 realPos) {
		return new Vector3(
			realPos.x / VOXEL_SIZE,
			realPos.y / VOXEL_SIZE,
			realPos.z / VOXEL_SIZE
		);
	}

	public void onStep() {
		List<Sound> sounds = getType().getProperties().getSounds();
		debounceSound -= 1f;
		if (debounceSound <= 0) {
			for (Sound sound : sounds) {
				sound.play(1.0f);
			}
			debounceSound = 10000f;
		}
	}
}
