package net.ddns.minersonline.HistorySurvival.api.data.resources;

import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import org.joml.Vector2f;

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
	public int loadTexture(String filename, boolean addToAtlas, String rootPath) {
		return -1;
	}

	@Override
	public ModelTexture getTextureAtlas() {
		return null;
	}

	@Override
	public Vector2f getTextureAtlasSize() {
		return null;
	}

	@Override
	public int getAtlasCount() {
		return -1;
	}
}
