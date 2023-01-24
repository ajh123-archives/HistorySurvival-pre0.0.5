package tk.minersonline.history_survival;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gwt.thirdparty.guava.common.annotations.GwtIncompatible;
import com.kotcrab.vis.ui.VisUI;
import net.fabricmc.loader.api.FabricLoader;
import tk.minersonline.history_survival.screens.MenuScreen;

public class HistorySurvival extends Game {
	public SpriteBatch spriteBatch;
	public BitmapFont font;
	@GwtIncompatible("")
	private FabricLoader loader = FabricLoader.getInstance();
	public static HistorySurvival INSTANCE = InitHelper.get();

	@Override
	public void create () {
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
		super.render();
	}

	@Override
	public void dispose() {
		VisUI.dispose();
		spriteBatch.dispose();
		font.dispose();
	}

	@GwtIncompatible("")
	public FabricLoader getLoader() {
		return loader;
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

	@GwtIncompatible("")
	public void setLoader(FabricLoader loader) {
		this.loader = loader;
	}
}
