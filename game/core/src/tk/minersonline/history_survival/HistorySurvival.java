package tk.minersonline.history_survival;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.minersonline.history_survival.net.Packet;
import tk.minersonline.history_survival.screens.GameScreen;

import java.io.IOException;

public class HistorySurvival extends Game {
	private static final Logger logger = LoggerFactory.getLogger("HistorySurvival");
	public SpriteBatch spriteBatch;
	public BitmapFont font;

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();

		this.setScreen(new GameScreen(this));

		logger.info("Game started");


		try {
			Client client = new Client();
			Kryo kryo = client.getKryo();
			kryo.register(Packet.class);

			client.start();
			client.connect(5000, "127.0.0.1", 36676, 36676);
			client.sendTCP(new Packet("qwerty"));
		} catch (IOException e) {
			logger.info("Client unable to connect to port", e);
		}
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		font.dispose();
	}
}
