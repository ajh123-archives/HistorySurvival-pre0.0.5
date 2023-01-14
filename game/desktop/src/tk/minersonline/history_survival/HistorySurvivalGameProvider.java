package tk.minersonline.history_survival;

import java.lang.reflect.InvocationTargetException;

public class HistorySurvivalGameProvider extends HistorySurvivalDefaultProvider {
	@Override
	public String getMainEntryPoint() {
		return "tk.minersonline.history_survival.ClientEntry";
	}

	@Override
	public void launch(ClassLoader loader) {
		try {
			Class<?> holder = ClassLoader.getSystemClassLoader().loadClass("tk.minersonline.history_survival.InstanceHolder");
			holder.getDeclaredMethod("getInstance").invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		super.launch(loader);
	}
}
