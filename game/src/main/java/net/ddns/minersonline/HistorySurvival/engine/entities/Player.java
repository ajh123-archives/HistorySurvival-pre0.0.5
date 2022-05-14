package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import org.joml.Vector3f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
	private static final float RUN_SPEED = 24;  // units per second
	private static final float TURN_SPEED = 160;    // degrees per second
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 18;

	private float currentSpeed;
	private float currentTurnSpeed;
	private float upwardsSpeed;
	private boolean isJump;
	private World world;

	public Player(World world, TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
		super(texturedModel, position, rotationX, rotationY, rotationZ, scale);
		this.world = world;
		this.isJump = false;
	}

	public boolean isJump() {
		return isJump;
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public float getCurrentTurnSpeed() {
		return currentTurnSpeed;
	}

	public float getUpwardsSpeed() {
		return upwardsSpeed;
	}

	public World getWorld() {
		return world;
	}

	public void move() {
		Vector3f pos = getPosition();
		Terrain terrain = world.getTerrain(pos.x, pos.z);

		// Calculate movement
		super.increaseRotation(0, currentTurnSpeed * (float) DisplayManager.getDeltaInSeconds(), 0);
		float distance = currentSpeed * (float) DisplayManager.getDeltaInSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotationY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotationY())));
		super.increasePosition(dx, 0, dz);

		// Calculate jump
		upwardsSpeed += GRAVITY * DisplayManager.getDeltaInSeconds();
		super.increasePosition(0, (float) (upwardsSpeed * DisplayManager.getDeltaInSeconds()), 0);

		// Player terrain collision detection
		if(terrain != null) {
			float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
			if (super.getPosition().y < terrainHeight) {
				upwardsSpeed = 0;
				isJump = false;
				super.getPosition().y = terrainHeight;
			}
		}
	}

	private void jump() {
		if (upwardsSpeed == 0) {
			upwardsSpeed = JUMP_POWER;
			isJump = true;
		}
	}

	public void checkInputs() {
		if (Keyboard.isKeyDown(GLFW_KEY_W) || Keyboard.isKeyDown(GLFW_KEY_UP)) {
			currentSpeed = RUN_SPEED;
		} else if (Keyboard.isKeyDown(GLFW_KEY_S) || Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
			currentSpeed = -RUN_SPEED;
		} else {
			currentSpeed = 0;
		}

		if (Keyboard.isKeyDown(GLFW_KEY_D) || Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
			currentTurnSpeed = -TURN_SPEED;
		} else if (Keyboard.isKeyDown(GLFW_KEY_A) || Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
			currentTurnSpeed = TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}

		if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
			jump();
		}
	}

	public void setCurrentSpeed(Integer i) {
		if (i > 0) {
			this.currentSpeed = RUN_SPEED;
		} else if (i < 0) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}
	}

	public void setCurrentTurnSpeed(Integer i) {
		if (i > 0) {
			this.currentTurnSpeed = TURN_SPEED;
		} else if (i < 0) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
	}

	public void setJump(Integer i) {
		if (i > 0) {
			jump();
		}
	}
}
