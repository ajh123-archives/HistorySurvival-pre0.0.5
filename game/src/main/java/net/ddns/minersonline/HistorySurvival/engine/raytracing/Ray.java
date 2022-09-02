package net.ddns.minersonline.HistorySurvival.engine.raytracing;

import org.joml.Vector3f;

public class Ray {
	public Vector3f origin;
	public Vector3f direction;

	public Ray(Vector3f origin, Vector3f direction) {
		this.origin = origin;
		this.direction = direction;
	}

	public Vector3f getPos(float t) {
		 return origin.add(direction.mul(t));
	}
}
