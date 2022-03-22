package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
    private static final float RUN_SPEED = 20;  // units per second
    private static final float TURN_SPEED = 160;    // degrees per second
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

    private float currentSpeed;
    private float currentTurnSpeed;
    private float upwardsSpeed;
    private Map<Integer, Map<Integer, Terrain>> world;

    public Player(Map<Integer, Map<Integer, Terrain>> world, TexturedModel texturedModel, Vector3f position, float rotationX, float rotationY, float rotationZ, float scale) {
        super(texturedModel, position, rotationX, rotationY, rotationZ, scale);
        this.world = world;
    }

    public void move() {
        checkInputs();
        Vector3f pos = getPosition();
        Terrain terrain = Terrain.getTerrain(world, pos.x, pos.z);

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
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            super.getPosition().y = terrainHeight;
        }
    }

    private void jump() {
        if (upwardsSpeed == 0) {
            upwardsSpeed = JUMP_POWER;
        }
    }

    private void checkInputs() {
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
}
