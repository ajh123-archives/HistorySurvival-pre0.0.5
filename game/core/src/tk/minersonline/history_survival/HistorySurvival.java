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
import tk.minersonline.history_survival.main.Client;
import tk.minersonline.history_survival.voxels.PerlinNoiseGenerator;
import tk.minersonline.history_survival.voxels.VoxelWorld;

public class HistorySurvival extends ApplicationAdapter {
	public static Client client;
	public static SpriteBatch spriteBatch;
	public static BitmapFont font;

	@Override
	public void create () {
		font = new BitmapFont();
		spriteBatch = new SpriteBatch();
//		String[] args = new String[0];
//		KnotClient.main(args);
		client = new Client();
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
}
