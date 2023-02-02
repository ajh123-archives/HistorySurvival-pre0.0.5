package tk.minersonline.history_survival.componments;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class TransformComponent implements Component {
	private final Vector3 pos;
	private final float scale;

	public TransformComponent(Vector3 pos, float scale) {
		this.pos = pos;
		this.scale = scale;
	}

	public Vector3 getPos() {
		return pos;
	}

	public float getScale() {
		return scale;
	}
}
