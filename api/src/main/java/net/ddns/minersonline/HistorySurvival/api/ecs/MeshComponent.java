package net.ddns.minersonline.HistorySurvival.api.ecs;

import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;

public class MeshComponent extends Component {
	public TexturedModel texturedModel;
	public int textureAtlasIndex;

	public MeshComponent(TexturedModel texturedModel) {
		this.texturedModel = texturedModel;
	}

	public MeshComponent(TexturedModel texturedModel, int textureAtlasIndex) {
		this.texturedModel = texturedModel;
		this.textureAtlasIndex = textureAtlasIndex;
	}

	public float getTextureAtlasXOffset() {
		int column = textureAtlasIndex % texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
		return (float) column / (float) texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
	}

	public float getTextureAtlasYOffset() {
		int row = textureAtlasIndex / texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
		return (float) row / (float) texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
	}
}
