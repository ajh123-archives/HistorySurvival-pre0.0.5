package tk.minersonline.history_survival.main;

import com.badlogic.gdx.ApplicationAdapter;
import net.fabricmc.loader.impl.launch.knot.KnotServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLauncher extends ApplicationAdapter {
	public static Server server;
	private static final Logger logger = LoggerFactory.getLogger("HistorySurvival");

	@Override
	public void create() {
		String[] args = new String[0];
		KnotServer.main(args);
	}

	@Override
	public void render() {
		if (server != null) {
			server.render();
		}
	}
}
