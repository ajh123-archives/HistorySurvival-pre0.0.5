package tk.minersonline.history_survival.main;

import com.badlogic.gdx.ApplicationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server extends ApplicationAdapter {
	private static final Logger logger = LoggerFactory.getLogger("HistorySurvival");

	@Override
	public void create() {
		logger.info("Server started");
		ServerLauncher.server = this;
	}

	@Override
	public void render() {
		logger.info("...");
	}
}
