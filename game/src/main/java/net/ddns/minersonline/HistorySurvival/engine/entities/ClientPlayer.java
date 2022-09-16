package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.World;
import org.joml.Vector3f;


import static org.lwjgl.glfw.GLFW.*;

public class ClientPlayer extends ClientEntity<PlayerEntity> {
	private static final float RUN_SPEED = 24;   // units per second
	private static final float TURN_SPEED = 160; // degrees per second
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 18;

	private final World world;

	public ClientPlayer(PlayerEntity entity, World world, TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
		super(entity, texturedModel, position, rotationX, rotationY, rotationZ, scale);
		this.world = world;
		getEntity().setJump(false);
		getEntity().onChatMessage(ChatSystem::addChatMessage);
	}

	public boolean isJump() {
		return getEntity().isJump();
	}

	public float getCurrentSpeed() {
		return getEntity().getCurrentSpeed();
	}

	public float getCurrentTurnSpeed() {
		return getEntity().getCurrentTurnSpeed();
	}

	public float getUpwardsSpeed() {
		return getEntity().getUpwardsSpeed();
	}

	public World getWorld() {
		return world;
	}

	public void move(float deltaTime) {
		Vector3f pos = getPosition();
		Terrain terrain = world.getTerrain(pos.x, pos.z);

		// Calculate movement
		increaseRotation(0, getCurrentTurnSpeed() * deltaTime, 0);
		float distance = getCurrentSpeed() * deltaTime;
		float dx = (float) (distance * Math.sin(Math.toRadians(getRotationY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(getRotationY())));
		increasePosition(dx, 0, dz);

		// Calculate jump
		getEntity().setUpwardsSpeed(getUpwardsSpeed() + GRAVITY * deltaTime);
		increasePosition(0, getUpwardsSpeed() * deltaTime, 0);

		// Player terrain collision detection
		if(terrain != null) {
			float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
			if (getPosition().y < terrainHeight) {
				getEntity().setUpwardsSpeed(0);
				getEntity().setJump(false);
				getPosition().y = terrainHeight;
			}
		}
	}

	private void jump() {
		if (getUpwardsSpeed() == 0) {
			getEntity().setUpwardsSpeed(JUMP_POWER);
			getEntity().setJump(true);
		}
	}

	public void checkInputs() {
		if (Keyboard.isKeyDown(GLFW_KEY_W) || Keyboard.isKeyDown(GLFW_KEY_UP)) {
			getEntity().setCurrentSpeed(RUN_SPEED);
		} else if (Keyboard.isKeyDown(GLFW_KEY_S) || Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
			getEntity().setCurrentSpeed(-RUN_SPEED);
		} else {
			getEntity().setCurrentSpeed(0);
		}

		if (Keyboard.isKeyDown(GLFW_KEY_D) || Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
			getEntity().setCurrentTurnSpeed(-TURN_SPEED);
		} else if (Keyboard.isKeyDown(GLFW_KEY_A) || Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
			getEntity().setCurrentTurnSpeed(TURN_SPEED);
		} else {
			getEntity().setCurrentTurnSpeed(0);
		}

		if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
			jump();
		}
	}

	public void setCurrentSpeed(Integer i) {
		if (i > 0) {
			getEntity().setCurrentSpeed(RUN_SPEED);
		} else if (i < 0) {
			getEntity().setCurrentSpeed(-RUN_SPEED);
		} else {
			getEntity().setCurrentSpeed(0);
		}
	}

	public void setCurrentTurnSpeed(Integer i) {
		if (i > 0) {
			getEntity().setCurrentTurnSpeed(TURN_SPEED);
		} else if (i < 0) {
			getEntity().setCurrentTurnSpeed(-TURN_SPEED);
		} else {
			getEntity().setCurrentTurnSpeed(0);
		}
	}

	public void setJump(Integer i) {
		if (i > 0) {
			jump();
		}
	}
}
