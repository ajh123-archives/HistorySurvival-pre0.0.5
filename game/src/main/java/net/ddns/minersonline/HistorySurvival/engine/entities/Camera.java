package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	private Player player;
	private float distanceFromPlayer;
	private float angleAroundPLayer;

	private final float CAMERA_Y_OFFSET = 7;
	private final float ZOOM_LEVEL_FACTOR = 0.1f;
	private final float PITCH_CHANGE_FACTOR = 0.2f;
	private final float ANGLE_AROUND_PLAYER_CHANGE_FACTOR = 0.3f;
	private final float MIN_PITCH = -90;
	private final float MAX_PITCH = 90;

	public Camera(Player player) {
		this.player = player;
		position = new Vector3f(0, 0, 0);
		distanceFromPlayer = 50;
		pitch = 20;
	}

	public void move() {
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();

		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		yaw = 180 - (player.getRotationY() + angleAroundPLayer);
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
		float zoomLevel = Mouse.getDMouseScrollY() * ZOOM_LEVEL_FACTOR;;
		distanceFromPlayer -= zoomLevel;
	}

	private void calculatePitch() {
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL) && Mouse.isButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
			float pitchChange = Mouse.getDY() * PITCH_CHANGE_FACTOR;
			pitch -= pitchChange;
			if (pitch < MIN_PITCH) {
				pitch = MIN_PITCH;
			} else if (pitch > MAX_PITCH) {
				pitch = MAX_PITCH;
			}
		}
	}

	private void calculateAngleAroundPlayer() {
		if (Keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL) && Mouse.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
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
		float theta = player.getRotationY() + angleAroundPLayer;
		float offsetXOfCameraFromPlayer = (float) (horizontalDistanceFromPlayer * Math.sin(Math.toRadians(theta)));
		float offsetZOfCameraFromPlayer = (float) (horizontalDistanceFromPlayer * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetXOfCameraFromPlayer;
		position.z = player.getPosition().z - offsetZOfCameraFromPlayer;
		position.y = player.getPosition().y + verticalDistanceFromPlayer + CAMERA_Y_OFFSET;
	}
}
