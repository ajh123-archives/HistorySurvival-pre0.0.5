package tk.minersonline.history_survival.componments;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelComponent implements Component {
	private final String path;

	public ModelComponent(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
