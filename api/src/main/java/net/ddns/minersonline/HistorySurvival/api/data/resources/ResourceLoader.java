package net.ddns.minersonline.HistorySurvival.api.data.resources;

import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;

public abstract class ResourceLoader {
	public abstract RawModel loadObjModel(String filename);
	public abstract RawModel loadCube();
	public abstract int loadTexture(String filename);
}
