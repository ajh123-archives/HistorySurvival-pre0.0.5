package tk.minersonline.history_survival.voxels.data;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoxelProperties {
	private final Color color;
	private final boolean transparent;
	private final List<Sound> sounds = new ArrayList<>();

	public VoxelProperties(Color color, boolean transparent) {
		this.color = color;
		this.transparent = transparent;
	}

	public VoxelProperties(Color color, List<Sound> sounds) {
		this(color, false);
		this.sounds.addAll(sounds);
	}

	public VoxelProperties(Color color, Sound sound) {
		this(color, false);
		this.sounds.add(sound);
	}

	public VoxelProperties(Color color) {
		this(color, false);
	}

	public Color getColor() {
		return color;
	}

	public List<Sound> getSounds() {
		return Collections.unmodifiableList(sounds);
	}

	public boolean isTransparent() {
		return transparent;
	}
}
