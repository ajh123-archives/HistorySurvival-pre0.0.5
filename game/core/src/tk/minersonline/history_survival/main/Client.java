package tk.minersonline.history_survival.main;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.minersonline.history_survival.HistorySurvival;
import tk.minersonline.history_survival.voxels.PerlinNoiseGenerator;
import tk.minersonline.history_survival.voxels.VoxelWorld;

public class Client extends ApplicationAdapter {
	private static final Logger logger = LoggerFactory.getLogger("HistorySurvival");

	ModelBatch modelBatch;
	PerspectiveCamera camera;
	Environment lights;
	FirstPersonCameraController controller;
	VoxelWorld voxelWorld;

	public Client() {
//		try {
//			Gdx.app = (Application) gdx.getClass().getField("app").get(null);
//		} catch (IllegalAccessException | NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		}
	}

	@Override
	public void create () {
		logger.info("Client starting");
		HistorySurvival.client = this;

		modelBatch = new ModelBatch();
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;
		controller = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(controller);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

		MathUtils.random.setSeed(0);
		voxelWorld = new VoxelWorld(20, 4, 20);
		PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 63, 10);
		float camX = voxelWorld.voxelsX / 2f;
		float camZ = voxelWorld.voxelsZ / 2f;
		float camY = voxelWorld.getHighest(camX, camZ) + 1.5f;
		camera.position.set(camX, camY, camZ);
		logger.info("Client started");
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.4f, 0.4f, 0.4f, 1f, true);
		modelBatch.begin(camera);
		modelBatch.render(voxelWorld, lights);
		modelBatch.end();
		controller.update();

		HistorySurvival.spriteBatch.begin();
		HistorySurvival.font.draw(HistorySurvival.spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ", #visible chunks: " + voxelWorld.renderedChunks + "/"
				+ voxelWorld.numChunks, 0, 20);
		HistorySurvival.spriteBatch.end();
	}

	@Override
	public void resize (int width, int height) {
		HistorySurvival.spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
}