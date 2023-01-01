package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public enum VoxelType {
	AIR("air", Color.CLEAR),
	GRASS("grass", Color.GREEN),
	STONE("stone", Color.GRAY);

	private final String name;
	private final Color color;

	VoxelType(String name, Color color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public static void init() {}
}
