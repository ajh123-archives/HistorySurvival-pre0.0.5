package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.MeshComponent;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.shaders.StaticShader;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.TerrainRenderer;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.TerrainShader;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.engine.voxel.VoxelRenderer;
import net.ddns.minersonline.HistorySurvival.engine.voxel.VoxelShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class MasterRenderer {
	private static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	private static float SKY_RED = 0.65f;
	private static float SKY_GREEN = 0.9f;
	private static float SKY_BLUE = 0.97f;

	private StaticShader staticShader;
	private GameObjectRenderer gameObjectRenderer;
	private final Map<TexturedModel, List<GameObject>> entities;
	private Matrix4f projectionMatrix;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	private VoxelRenderer voxelRenderer;
	private VoxelShader voxelShader;
	private List<Terrain> terrainList;
	private final List<GameObject> newEntityList = new ArrayList<>();

	public MasterRenderer(ModelLoader loader) {
		enableCulling();
		staticShader = new StaticShader();
		terrainShader = new TerrainShader();
		voxelShader = new VoxelShader();
		entities = new HashMap<>();
		createProjectionMatrix();
		gameObjectRenderer = new GameObjectRenderer(staticShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		voxelRenderer = new VoxelRenderer(voxelShader, projectionMatrix);
		terrainList = new ArrayList<>();

	}

	public static void enableCulling() {
		// don't texture surfaces with normal vectors facing away from the "camera". don't render back faces of the a model
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	public static void disableCulling() {
		glDisable(GL_CULL_FACE);
	}

	public void processEntity(GameObject entity) {
		MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
		if (meshComponent != null) {
			TexturedModel entityModel = meshComponent.texturedModel;
			List<GameObject> entityList = entities.get(entityModel);

			if (entityList == null) {
				newEntityList.add(entity);
				entities.put(entityModel, newEntityList);
				return;
			}

			entityList.add(entity);
		}
	}

	public void renderScene(Map<TexturedModel, Collection<Voxel>> world, List<Light> lights, Camera camera, Vector4f clipping_plane, float deltaTime){
		for (GameObject entity : GameObjectManager.getGameObjects()) {
			processEntity(entity);
		}
		render(world, lights, camera, clipping_plane, deltaTime);
		newEntityList.clear();
	}


	public void render(Map<TexturedModel, Collection<Voxel>> world, List<Light> lights, Camera camera, Vector4f clipping_plane, float deltaTime) {
		prepare();
		voxelShader.bind();
		voxelRenderer.render(world, camera, deltaTime);
		voxelShader.unbind();
		staticShader.bind();
		staticShader.loadClipPlane(clipping_plane);
		staticShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		staticShader.loadDiffuseLights(lights);
		staticShader.loadViewMatrix(camera);
		gameObjectRenderer.render(newEntityList);
		staticShader.unbind();
		terrainShader.bind();
		terrainShader.loadClipPlane(clipping_plane);
		terrainShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		terrainShader.loadDiffuseLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrainList);
		terrainShader.unbind();
		entities.clear();
		terrainList.clear();
	}

	public void destroy() {
		staticShader.destroy();
		terrainShader.destroy();
		voxelShader.destroy();
	}

	private void prepare() {
		glEnable(GL_DEPTH_TEST);    // test which triangles are in front and render them in the correct order
		glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1);      // Load selected color into the color buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);     // Clear screen and draw with color in color buffer
	}

	private void createProjectionMatrix() {
		float aspectRatio = (float) DisplayManager.getWindowWidth() / (float) DisplayManager.getWindowHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix = projectionMatrix.m00(xScale);
		projectionMatrix = projectionMatrix.m11(yScale);
		projectionMatrix = projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix = projectionMatrix.m23(-1);
		projectionMatrix = projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix = projectionMatrix.m33(0);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setBackgroundColour(Vector3f colour){
		SKY_RED = colour.x;
		SKY_GREEN = colour.y;
		SKY_BLUE = colour.z;
	}
}
