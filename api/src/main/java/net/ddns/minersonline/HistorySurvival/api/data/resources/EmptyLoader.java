package net.ddns.minersonline.HistorySurvival.api.data.resources;

import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;

public class EmptyLoader extends ResourceLoader{
	@Override
	public RawModel loadObjModel(String filename) {
		return null;
	}

	@Override
	public RawModel loadCube() {
		return null;
	}

	@Override
	public RawModel loadToVao(float[] positions, float[] textureCoOrds) {
		return null;
	}

	@Override
	public int loadTexture(String filename) {
		return 0;
	}

	@Override
	public ModelTexture getTextureAtlas() {
		return null;
	}
}
