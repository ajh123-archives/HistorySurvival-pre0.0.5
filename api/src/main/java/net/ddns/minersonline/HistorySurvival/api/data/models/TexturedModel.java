package net.ddns.minersonline.HistorySurvival.api.data.models;

import com.google.gson.*;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.registries.Registries;

import java.lang.reflect.Type;

public class TexturedModel {
	private transient RawModel rawModel;
	private transient ModelTexture modelTexture;
	private ResourceLocation modelType;

	public TexturedModel(RawModel rawModel, ModelTexture modelTexture) {
		this.rawModel = rawModel;
		this.modelTexture = modelTexture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getModelTexture() {
		return modelTexture;
	}

	public ResourceLocation getModelType() {
		return modelType;
	}

	public void setModelType(ResourceLocation modelType) {
		this.modelType = modelType;
	}

	public static class JSON implements JsonDeserializer<TexturedModel> {
		@Override
		public TexturedModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			JsonElement element = jsonObject.get("modelType");
			ResourceLocation location = context.deserialize(element, ResourceLocation.class);

			return Registries.MODEL_REGISTRY.get(location).create();
		}
	}
}
