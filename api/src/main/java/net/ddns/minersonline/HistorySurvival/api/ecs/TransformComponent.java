package net.ddns.minersonline.HistorySurvival.api.ecs;

import imgui.ImGui;
import imgui.type.ImFloat;
import org.joml.Vector3f;

public class TransformComponent extends Component{
	public Vector3f position = new Vector3f();
	public Vector3f rotation = new Vector3f();
	public float scale = 0f;

	public TransformComponent() {}

	public TransformComponent(Vector3f position, Vector3f rotation, float scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public final void increaseRotation(float dx, float dy, float dz) {
		this.rotation.x += dx;
		this.rotation.y += dy;
		this.rotation.z += dz;
	}

	public final void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	@Override
	public void debug() {
		float[] pos = {position.x, position.y, position.z};
		if (ImGui.inputFloat3("Position", pos)){this.position.set(pos);}

		float[] rot = {rotation.x, rotation.y, rotation.z};
		if (ImGui.inputFloat3("Rotation", rot)){this.rotation.set(rot);}

		ImFloat scale = new ImFloat(this.scale);
		ImGui.inputFloat("Scale", scale);
		this.scale = scale.get();
	}
}
