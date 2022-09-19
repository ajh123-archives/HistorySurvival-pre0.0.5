package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLoader;
import net.ddns.minersonline.HistorySurvival.api.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelModel;

public class ResourceLoaderImpl extends ResourceLoader {
	@Override
	public RawModel loadObjModel(String filename) {
		return ObjLoader.loadObjModel(filename, Game.modelLoader);
	}

	@Override
	public RawModel loadCube() {
		return Game.modelLoader.loadToVaoRaw(VoxelModel.vertices, VoxelModel.uv);
	}

	@Override
	public RawModel loadToVao(float[] positions, float[] textureCoOrds) {
		return Game.modelLoader.loadToVaoRaw(positions, textureCoOrds);
	}

	@Override
	public int loadTexture(String filename) {
		return Game.modelLoader.loadTexture(filename);
	}
}
