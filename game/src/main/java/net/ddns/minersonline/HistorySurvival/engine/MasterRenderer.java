package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.shaders.StaticShader;
import net.ddns.minersonline.HistorySurvival.engine.shaders.TerrainShader;
import net.ddns.minersonline.HistorySurvival.engine.terrains.Terrain;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private EntityRenderer entityRenderer;
	private Map<TexturedModel, List<ClientEntity>> entities;
	private Matrix4f projectionMatrix;
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader;
	private List<Terrain> terrainList;

	public MasterRenderer() {
		enableCulling();
		staticShader = new StaticShader();
		terrainShader = new TerrainShader();
		entities = new HashMap<>();
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(staticShader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
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

	public void processEntity(ClientEntity entity) {
		TexturedModel entityModel = entity.getTexturedModel();
		List<ClientEntity> entityList = entities.get(entityModel);

		if (entityList == null) {
			List<ClientEntity> newEntityList = new ArrayList<>();
			newEntityList.add(entity);
			entities.put(entityModel, newEntityList);
			return;
		}

		entityList.add(entity);
	}

	public void renderScene(List<ClientEntity> entities, World world, List<Light> lights, Camera camera, Vector4f clipping_plane){
		processWorld(world);
		for (ClientEntity entity : entities) {
			processEntity(entity);
		}
		render(lights, camera, clipping_plane);
	}


	public void render(List<Light> lights, Camera camera, Vector4f clipping_plane) {
		prepare();
		staticShader.bind();
		staticShader.loadClipPlane(clipping_plane);
		staticShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		staticShader.loadDiffuseLights(lights);
		staticShader.loadViewMatrix(camera);
		entityRenderer.render(entities);
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

	public void processWorld(World world) {
		if (world != null) {
			terrainList.addAll(world.getTerrains());
		}
	}

	public void destory() {
		staticShader.destroy();
		terrainShader.destroy();
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
		projectionMatrix.m00(xScale);
		projectionMatrix.m11(yScale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
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
