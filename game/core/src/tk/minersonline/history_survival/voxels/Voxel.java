package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Voxel {
	private final VoxelType type;
	private final Vector3 position;
	private final Color color;

	Voxel(VoxelType type, Vector3 position) {
		this.type = type;
		this.position = position;
		this.color = type.getColor();
	}

	public VoxelType getType() {
		return type;
	}

	public Vector3 getPosition() {
		return position;
	}

	public Color getColor() {
		return color;
	}
}
