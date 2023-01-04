package tk.minersonline.history_survival;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class ClientEntry {
	public void create() {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("HistorySurvival");
		new Lwjgl3Application(new HistorySurvival(), config);
	}
}
