package tk.minersonline.history_survival;

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
import net.fabricmc.loader.impl.launch.knot.KnotClient;
import org.spongepowered.asm.mixin.Implements;
import tk.minersonline.history_survival.main.Client;
import tk.minersonline.history_survival.voxels.PerlinNoiseGenerator;
import tk.minersonline.history_survival.voxels.VoxelWorld;

public class HistorySurvival extends ApplicationAdapter implements GameInstance {
	public static Client client;
	public SpriteBatch spriteBatch;
	public BitmapFont font;
	public ModelBatch modelBatch;
	public PerspectiveCamera camera;

	private static HistorySurvival INSTANCE;

	public HistorySurvival() {
		HistorySurvival.INSTANCE = this;
	}

	@Override
	public void create () {
		font = new BitmapFont();
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;

//		String[] args = new String[0];
//		KnotClient.main(args);
// Before the fabric loader!
		client = new Client();
		client.create();
	}

	@Override
	public void render () {
		if (client != null) {
			client.render();
		}
	}

	@Override
	public void resize (int width, int height) {
		if (client != null) {
			client.resize(width, height);
		}
	}

	public static HistorySurvival getINSTANCE() {
		return INSTANCE;
	}
}
