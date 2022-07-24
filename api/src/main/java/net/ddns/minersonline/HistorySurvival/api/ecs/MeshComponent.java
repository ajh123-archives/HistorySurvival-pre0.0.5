package net.ddns.minersonline.HistorySurvival.api.ecs;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import imgui.type.ImString;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;

public class MeshComponent extends Component {
	public TexturedModel texturedModel;
	public int textureAtlasIndex;

	public MeshComponent() {}

	public MeshComponent(TexturedModel texturedModel) {
		this.texturedModel = texturedModel;
	}

	public MeshComponent(TexturedModel texturedModel, int textureAtlasIndex) {
		this.texturedModel = texturedModel;
		this.textureAtlasIndex = textureAtlasIndex;
	}

	public final float getTextureAtlasXOffset() {
		int column = textureAtlasIndex % texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
		return (float) column / (float) texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
	}

	public final float getTextureAtlasYOffset() {
		int row = textureAtlasIndex / texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
		return (float) row / (float) texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas();
	}

	@Override
	public void debug() {
		ImGui.spacing();

		ImString modelType = new ImString(texturedModel.getModelType().toString());
		if (ImGui.inputText("Model", modelType)){
			texturedModel.setModelType(new ResourceLocation(modelType.get()));
		}

		ImGui.separator();
		ImGui.text("This component is locked");
		ImGui.inputInt("VAO ID", new ImInt(texturedModel.getRawModel().getVaoId()));
		ImGui.inputInt("Vertex Count", new ImInt(texturedModel.getRawModel().getVertexCount()));
		ImGui.separator();
		ImGui.text("This component is locked");
		ImGui.inputInt("Texture ID", new ImInt(texturedModel.getModelTexture().getTextureId()));
		ImGui.inputFloat("Shine Damper", new ImFloat(texturedModel.getModelTexture().getShineDamper()));
		ImGui.inputFloat("Reflectivity", new ImFloat(texturedModel.getModelTexture().getReflectivity()));
		ImGui.checkbox("Transparency", new ImBoolean(texturedModel.getModelTexture().isHasTransparency()));
		ImGui.inputInt("Texture Atlas Rows", new ImInt(texturedModel.getModelTexture().getNumberOfRowsInTextureAtlas()));
		ImGui.checkbox("Fake Lighting", new ImBoolean(texturedModel.getModelTexture().isUseFakeLighting()));
	}
}
