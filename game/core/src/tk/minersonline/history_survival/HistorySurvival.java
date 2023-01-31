package tk.minersonline.history_survival;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.kotcrab.vis.ui.VisUI;
import tk.minersonline.history_survival.screens.MenuScreen;
import tk.minersonline.history_survival.systems.BulletPhysicsSystem;

public class HistorySurvival extends Game {
	public SpriteBatch spriteBatch;
	public BitmapFont font;
	public static HistorySurvival INSTANCE = InitHelper.get();
	public BulletPhysicsSystem bulletPhysicsSystem;
	public PerspectiveCamera camera;
	@Override
	public void create () {
		Bullet.init();
		bulletPhysicsSystem = new BulletPhysicsSystem();

		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		VisUI.load();

		this.setScreen(new MenuScreen(this));

//		LOGGER.info("Game started");


//		try {
//			Client client = new Client();
//			Kryo kryo = client.getKryo();
//			kryo.register(Packet.class);
//
//			client.start();
//			client.connect(5000, "127.0.0.1", 36676, 36676);
//			client.sendTCP(new Packet("qwerty"));
//		} catch (IOException e) {
//			logger.info("Client unable to connect to port", e);
//		}
	}

	@Override
	public void render () {
		bulletPhysicsSystem.update(Gdx.graphics.getDeltaTime());
		super.render();
	}

	@Override
	public void dispose() {
		VisUI.dispose();
		spriteBatch.dispose();
		font.dispose();
		bulletPhysicsSystem.dispose();
	}

	/**
	 * Provides singleton for static init assignment regardless of load order.
	 */
	public static class InitHelper {
		private static HistorySurvival instance;

		public static HistorySurvival get() {
			if (instance == null) instance = new HistorySurvival();

			return instance;
		}
	}
}
