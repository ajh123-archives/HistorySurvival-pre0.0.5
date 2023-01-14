package tk.minersonline.history_survival;

import net.fabricmc.loader.api.FabricLoader;

public class InstanceHolder {
	private static final InstanceHolder instance = new InstanceHolder();
	private final FabricLoader loader;


	private InstanceHolder() {
		loader = FabricLoader.getInstance();
	}

	public static InstanceHolder getInstance() {
		return instance;
	}

	public FabricLoader getLoader() {
		return loader;
	}
}
