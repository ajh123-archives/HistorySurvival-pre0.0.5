package net.ddns.minersonline.HistorySurvival.api.data.resources;

import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;

public class EmptyLoader extends ResourceLoader{
	@Override
	public RawModel loadObjModel(String filename) {
		return null;
	}

	@Override
	public int loadTexture(String filename) {
		return 0;
	}
}
