package net.ddns.minersonline.HistorySurvival.engine.utils;

import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MousePicker {
	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;

	private Vector3f currentRay;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;

	private Camera camera;

	private Vector3f currentTerrainPoint;

	private World world;

	public MousePicker(World world, Matrix4f projectionMatrix, Camera camera) {
		this.projectionMatrix = projectionMatrix;
		this.camera = camera;
		this.viewMatrix = Maths.createViewMatrix(camera);
		this.world = world;
	}

	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update(){
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calcMouseRay();
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		} else {
			currentTerrainPoint = null;
		}
	}

	private Vector3f calcMouseRay(){
		float mouseX = (float) Mouse.getMouseX();
		float mouseY = (float) Mouse.getMouseY();
		Vector2f normalisedDeviceCoOrds = getNormalisedDeviceCoOrds(mouseX, mouseY);
		Vector4f clipCoOrds = new Vector4f(normalisedDeviceCoOrds.x, normalisedDeviceCoOrds.y, -1f, 1f);
		Vector4f eyeCoOrds = toEyeCoOrds(clipCoOrds);
		return toWorldCoOrds(eyeCoOrds);
	}

	private Vector3f toWorldCoOrds(Vector4f eyeCoOrds){
		Matrix4f invertedView = viewMatrix.invert();
		Vector4f rayWorld = invertedView.transform(eyeCoOrds);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private Vector4f toEyeCoOrds(Vector4f clipCoOrds){
		Matrix4f invertedProjection = projectionMatrix.invert();
		Vector4f eyeCoOrds = invertedProjection.transform(clipCoOrds);
		return new Vector4f(eyeCoOrds.x, eyeCoOrds.z, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoOrds(float mouseX, float mouseY){
		float x = (2f*mouseX) / DisplayManager.getWindowWidth() -1;
		float y = (2f*mouseY) / DisplayManager.getWindowHeight() -1f;
		return new Vector2f(x, y);
	}

	//**********************************************************

	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return start.add(scaledRay);
	}

	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Terrain terrain = world.getTerrain(endPoint.x, endPoint.z);
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		return !isUnderGround(startPoint) && isUnderGround(endPoint);
	}

	private boolean isUnderGround(Vector3f testPoint) {
		Terrain terrain = world.getTerrain(testPoint.x, testPoint.z);
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(testPoint.x, testPoint.z);
		}
		return testPoint.y < height;
	}


}
