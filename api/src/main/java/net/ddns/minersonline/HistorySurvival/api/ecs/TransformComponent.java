package net.ddns.minersonline.HistorySurvival.api.ecs;

import org.joml.Vector3f;

public class TransformComponent extends Component{
	public Vector3f position;
	public Vector3f rotation;
	public float scale;

	public TransformComponent(Vector3f position, Vector3f rotation, float scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
}
