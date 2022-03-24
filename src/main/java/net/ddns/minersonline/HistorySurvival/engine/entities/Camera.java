package net.ddns.minersonline.HistorySurvival.engine.entities;

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
        float zoomLevel = (float) Mouse.getMouseScrollY() * 0.1f;
        distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        // TODO: pitching adjustments don't stop until the mouse button is released!
        if (Mouse.isButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
            float lastMouseY = (float) Mouse.getLastMouseY();
            float mouseY = (float) Mouse.getMouseY();
            float pitchChange = mouseY - lastMouseY;
            if(lastMouseY == mouseY) pitchChange = 0;
            Mouse.setLastMouseY(lastMouseY);
            pitch = pitch + pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        // TODO: angleAroundPLayer adjustments don't stop until the mouse button is released!
        if (Mouse.isButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            float lastMouseX = (float) Mouse.getLastMouseX();
            float mouseX = (float) Mouse.getMouseX();
            float angleChange = mouseX - lastMouseX;
            if(lastMouseX == mouseX) angleChange = 0;
            Mouse.setLastMouseX(lastMouseX);
            angleAroundPLayer = angleAroundPLayer + angleChange;
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
        position.y = player.getPosition().y + verticalDistanceFromPlayer;
    }
}
