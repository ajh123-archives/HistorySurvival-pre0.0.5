package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLoader;

public class ResourceLoaderImpl extends ResourceLoader {
	@Override
	public RawModel loadObjModel(String filename) {
		return ObjLoader.loadObjModel(filename, Game.modelLoader);
	}

	@Override
	public int loadTexture(String filename) {
		return Game.modelLoader.loadTexture(filename);
	}
}
