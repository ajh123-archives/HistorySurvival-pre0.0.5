package tk.minersonline.history_survival.componments;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SolidModelComponent implements Component {
	private final Color color;
	private final boolean transparent;
	private final List<Sound> sounds = new ArrayList<>();

	public SolidModelComponent(Color color, boolean transparent) {
		this.color = color;
		this.transparent = transparent;
	}

	public SolidModelComponent(Color color, List<Sound> sounds) {
		this(color, false);
		this.sounds.addAll(sounds);
	}

	public SolidModelComponent(Color color, Sound sound) {
		this(color, false);
		this.sounds.add(sound);
	}

	public SolidModelComponent(Color color) {
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
