package tk.minersonline.history_survival.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import tk.minersonline.history_survival.HistorySurvival;
import tk.minersonline.history_survival.componments.ModelComponent;
import tk.minersonline.history_survival.componments.TransformComponent;
import tk.minersonline.history_survival.systems.ModelRenderer;
import tk.minersonline.history_survival.systems.PerlinNoiseGenerator;
import tk.minersonline.history_survival.componments.VoxelEntity;
import tk.minersonline.history_survival.systems.VoxelWorld;
import tk.minersonline.history_survival.systems.WorldRenderer;

public class GameScreen implements Screen {
	final HistorySurvival game;
	ModelBatch chunkBatch;
	ModelBatch modelBatch;
	PerspectiveCamera camera;
	Environment environment;
	FirstPersonCameraController controller;
	VoxelWorld voxelWorld;
	WorldRenderer worldRenderer;
	ModelRenderer modelRenderer;
	Vector3 lastPos;
	PooledEngine engine;

	public GameScreen(HistorySurvival game) {
		this.game = game;
	}

	@Override
	public void show() {
		engine = new PooledEngine();
		chunkBatch = new ModelBatch();
		modelBatch = new ModelBatch();
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;
		controller = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(controller);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		environment.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

		MathUtils.random.setSeed(0);
		voxelWorld = new VoxelWorld(20, 4, 20, engine);
		worldRenderer = new WorldRenderer(voxelWorld);
		engine.addSystem(worldRenderer);
		PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 63, 10);
		modelRenderer = new ModelRenderer();
		engine.addSystem(modelRenderer);

		float camX = (float) Math.floor(voxelWorld.voxelsX / 2f);
		float camZ = (float) Math.floor(voxelWorld.voxelsZ / 2f);
		float camY = (float) Math.floor(voxelWorld.getHighest(camX, camZ) + (1.5f / VoxelEntity.VOXEL_SIZE));
		camera.position.set(VoxelEntity.toRealPos(new Vector3( camX, camY, camZ)));

		Entity test = engine.createEntity();
		test.add(new TransformComponent(VoxelEntity.toRealPos(new Vector3(camX, voxelWorld.getHighest(camX, camZ), camZ))));
		test.add(new ModelComponent("data/models/cube/cube.g3dj"));
		engine.addEntity(test);
	}

	@Override
	public void render(float delta) {
		engine.update(delta);
		ScreenUtils.clear(Color.SKY, true);
		chunkBatch.begin(camera);
		chunkBatch.render(worldRenderer, environment);
		chunkBatch.end();
		modelBatch.begin(camera);
		modelRenderer.render(modelBatch, environment);
		modelBatch.end();
		controller.update();

		Vector3 voxelPos = VoxelEntity.toVoxelPos(camera.position.cpy());
		VoxelEntity voxel = voxelWorld.get(voxelPos.x, voxelPos.y-(1.5f / VoxelEntity.VOXEL_SIZE)-1, voxelPos.z);
		if (voxel != null && !voxelPos.equals(lastPos)) {
			voxel.onStep();
		}
		lastPos = voxelPos.cpy();

		game.spriteBatch.begin();
		game.font.draw(game.spriteBatch, "pos: " + voxelPos, 0, 36);
		game.font.draw(game.spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 0, 20);
		game.spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		game.spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		modelRenderer.dispose();
	}
}
