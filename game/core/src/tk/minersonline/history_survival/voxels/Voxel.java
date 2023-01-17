package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class Voxel {
	private final VoxelType type;
	private final Vector3 position;
	private final VoxelWorld world;
	private float debounceSound = 0;

	public static final float VOXEL_SIZE = 0.3f;

	Voxel(VoxelType type, Vector3 position, VoxelWorld world) {
		this.type = type;
		this.position = position;
		this.world = world;
	}

	protected VoxelWorld getWorld() {
		return world;
	}

	protected VoxelType getType() {
		return type;
	}

	protected Vector3 getPosition() {
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
