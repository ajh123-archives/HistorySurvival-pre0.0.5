package tk.minersonline.history_survival.voxels.data;

import com.badlogic.gdx.graphics.Color;

public class VoxelProperties {
	private final Color color;
	private final boolean transparent;

	public VoxelProperties(Color color, boolean transparent) {
		this.color = color;
		this.transparent = transparent;
	}

	public VoxelProperties(Color color) {
		this.color = color;
		this.transparent = false;
	}

	public Color getColor() {
		return color;
	}

	public boolean isTransparent() {
		return transparent;
	}
}
