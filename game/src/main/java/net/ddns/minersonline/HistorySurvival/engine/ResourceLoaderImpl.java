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
	public RawModel loadCube() {
		float[] vertices = {
				-0.5f, 0.5f, 0,
				-0.5f, -0.5f, 0,
				0.5f, -0.5f, 0,
				0.5f, 0.5f, 0,
		};
		float[] textureCoOrds = {
				0, 0,
				0, 1,
				1, 1,
				1, 0
		};
		int[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		return Game.modelLoader.loadToVao(vertices, textureCoOrds, indices);
	}

	@Override
	public int loadTexture(String filename) {
		return Game.modelLoader.loadTexture(filename);
	}
}
