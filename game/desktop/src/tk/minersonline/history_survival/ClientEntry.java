package tk.minersonline.history_survival;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import net.fabricmc.loader.api.FabricLoader;
import tk.minersonline.history_survival.util.ClassProxy;

import java.lang.reflect.*;

public class ClientEntry {
	public void create() {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("HistorySurvival");

//		try {
//			Class<?> holder = ClassLoader.getSystemClassLoader().loadClass("tk.minersonline.history_survival.InstanceHolder");
//			Object singleton = holder.getDeclaredMethod("getInstance").invoke(null);
//			FabricLoader loader = (FabricLoader) singleton.getClass().getDeclaredMethod("getLoader").invoke(singleton);

//			ClassProxy<FabricLoader> classProxy = new ClassProxy<>(FabricLoader.class);
//			HistorySurvival.INSTANCE.setLoader(loader);
//			System.out.println(FabricLoader.getInstance().getAllMods());
//		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//			throw new RuntimeException(e);
//		}

		new Lwjgl3Application(HistorySurvival.INSTANCE, config);
	}
}
