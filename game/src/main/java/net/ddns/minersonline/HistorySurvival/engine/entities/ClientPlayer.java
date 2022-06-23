package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.api.entities.EntityType;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import org.joml.Vector3f;


import static org.lwjgl.glfw.GLFW.*;

public class ClientPlayer extends ClientEntity<PlayerEntity> {
	private static final float RUN_SPEED = 24;   // units per second
	private static final float TURN_SPEED = 160; // degrees per second
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 18;

	private final World world;

	public ClientPlayer(World world, TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
		super(EntityType.PLAYER_ENTITY.create(), texturedModel, position, rotationX, rotationY, rotationZ, scale);
		this.world = world;
		this.getEntity().setJump(false);
		this.getEntity().onChatMessage(ChatSystem::addChatMessage);
	}

	public boolean isJump() {
		return this.getEntity().isJump();
	}

	public float getCurrentSpeed() {
		return this.getEntity().getCurrentSpeed();
	}

	public float getCurrentTurnSpeed() {
		return this.getEntity().getCurrentTurnSpeed();
	}

	public float getUpwardsSpeed() {
		return this.getEntity().getUpwardsSpeed();
	}

	public World getWorld() {
		return world;
	}

	public void move() {
		Vector3f pos = getPosition();
		Terrain terrain = world.getTerrain(pos.x, pos.z);

		// Calculate movement
		super.increaseRotation(0, getCurrentTurnSpeed() * (float) DisplayManager.getDeltaInSeconds(), 0);
		float distance = getCurrentSpeed() * (float) DisplayManager.getDeltaInSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotationY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotationY())));
		super.increasePosition(dx, 0, dz);

		// Calculate jump
		this.getEntity().setUpwardsSpeed((float) (getUpwardsSpeed() + GRAVITY * DisplayManager.getDeltaInSeconds()));
		super.increasePosition(0, (float) (getUpwardsSpeed() * DisplayManager.getDeltaInSeconds()), 0);

		// Player terrain collision detection
		if(terrain != null) {
			float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
			if (super.getPosition().y < terrainHeight) {
				this.getEntity().setUpwardsSpeed(0);
				this.getEntity().setJump(false);
				super.getPosition().y = terrainHeight;
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
			this.getEntity().setCurrentSpeed(RUN_SPEED);
		} else if (i < 0) {
			this.getEntity().setCurrentSpeed(-RUN_SPEED);
		} else {
			this.getEntity().setCurrentSpeed(0);
		}
	}

	public void setCurrentTurnSpeed(Integer i) {
		if (i > 0) {
			this.getEntity().setCurrentTurnSpeed(TURN_SPEED);
		} else if (i < 0) {
			this.getEntity().setCurrentTurnSpeed(-TURN_SPEED);
		} else {
			this.getEntity().setCurrentTurnSpeed(0);
		}
	}

	public void setJump(Integer i) {
		if (i > 0) {
			jump();
		}
	}
}
