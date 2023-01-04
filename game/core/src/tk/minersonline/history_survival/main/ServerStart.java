package tk.minersonline.history_survival.main;

import com.badlogic.gdx.ApplicationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStart extends ApplicationAdapter {
	private static final Logger logger = LoggerFactory.getLogger("HistorySurvival");

	@Override
	public void create() {
		logger.info("Server started");
	}

	@Override
	public void render() {

	}
}
