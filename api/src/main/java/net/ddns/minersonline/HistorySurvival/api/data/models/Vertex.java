package net.ddns.minersonline.HistorySurvival.api.data.models;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
	public Vector3f positions, normals;
	public Vector2f uvs;

	public Vertex(Vector3f positions, Vector3f normals, Vector2f uvs) {
		this.positions = positions;
		this.normals = normals;
		this.uvs = uvs;
	}
}
