package tk.minersonline.history_survival.world.voxels;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import tk.minersonline.history_survival.world.World;

import java.util.List;

public class Voxel {
	private final VoxelType type;
	private final Vector3 position;
	private final World world;
	private float debounceSound = 0;


	public Voxel(VoxelType type, Vector3 position, World world) {
		this.type = type;
		this.position = position;
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public VoxelType getType() {
		return type;
	}

	public Vector3 getPosition() {
		return position;
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
