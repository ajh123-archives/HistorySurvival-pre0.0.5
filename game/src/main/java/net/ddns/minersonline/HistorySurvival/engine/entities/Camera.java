package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	private TransformComponent player;
	private float distanceFromPlayer;
	private float angleAroundPLayer;

	private final float CAMERA_Y_OFFSET = 6;
	private final float CAMERA_FORWARD_OFFSET = 1;
	private final float ZOOM_LEVEL_FACTOR = 0.1f;
	private final float PITCH_CHANGE_FACTOR = 0.2f;
	private final float ANGLE_AROUND_PLAYER_CHANGE_FACTOR = 0.3f;
	private final float MIN_PITCH = -90;
	private final float MAX_PITCH = 90;

	public Camera(TransformComponent player) {
		this.player = player;
		position = new Vector3f(0, 0, 0);
		distanceFromPlayer = 50;
		pitch = 20;
	}

	public void move() {
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)){
			Mouse.setIsGrabbed(true);
			calculateZoom();
			calculatePitch();
		} else {
			Mouse.setIsGrabbed(false);
		}
		calculateAngleAroundPlayer();
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL) && Mouse.isButtonDown(2)){
			distanceFromPlayer = 50;
			pitch = 20;
			angleAroundPLayer = 0;
		}
	}

	public void update() {
		if(this.player != null) {
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance, verticalDistance);
			yaw = 180 - (player.rotation.y + angleAroundPLayer);
		}
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setX(int x) {
		position.x = x;
	}

	public void setY(int y) {
		position.y = y;
	}

	public void setZ(int z) {
		position.z = z;
	}

	public void invertPitch() {
		this.pitch = -pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDMouseScrollY() * ZOOM_LEVEL_FACTOR;
		if ((distanceFromPlayer - zoomLevel) < 0){
			distanceFromPlayer = 0;
		} else {
			distanceFromPlayer -= zoomLevel;
		}
	}

	private void calculatePitch() {
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
			float pitchChange = Mouse.getDY() * PITCH_CHANGE_FACTOR;
			pitch += pitchChange;
			if (pitch < MIN_PITCH) {
				pitch = MIN_PITCH;
			} else if (pitch > MAX_PITCH) {
				pitch = MAX_PITCH;
			}
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
			float angleChange = Mouse.getDX() * ANGLE_AROUND_PLAYER_CHANGE_FACTOR;
			angleAroundPLayer -= angleChange;
		}
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private void calculateCameraPosition(float horizontalDistanceFromPlayer, float verticalDistanceFromPlayer) {
		float theta = player.rotation.y + angleAroundPLayer;
		float offsetXOfCameraFromPlayer = (float) ((horizontalDistanceFromPlayer-CAMERA_FORWARD_OFFSET) * Math.sin(Math.toRadians(theta)));
		float offsetZOfCameraFromPlayer = (float) ((horizontalDistanceFromPlayer-CAMERA_FORWARD_OFFSET) * Math.cos(Math.toRadians(theta)));
		position.x = player.position.x - offsetXOfCameraFromPlayer;
		position.z = player.position.z - offsetZOfCameraFromPlayer;
		position.y = player.position.y + verticalDistanceFromPlayer + CAMERA_Y_OFFSET;
	}

	public void setPlayer(TransformComponent player) {
		this.player = player;
	}
}
