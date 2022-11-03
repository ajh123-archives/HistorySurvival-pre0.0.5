package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.RawModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLoader;
import net.ddns.minersonline.HistorySurvival.api.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelModel;
import org.joml.Vector2f;

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
	public int loadTexture(String filename, boolean addToAtlas, String rootPath) {
		return Game.modelLoader.loadTexture(filename, addToAtlas, rootPath);
	}

	@Override
	public ModelTexture getTextureAtlas() {
		return TextureLoader.getTextureAtlas();
	}

	@Override
	public Vector2f getTextureAtlasSize() {return TextureLoader.getAtlasSize();}

	@Override
	public int getAtlasCount() {
		return TextureLoader.getAtlasCount();
	}
}
