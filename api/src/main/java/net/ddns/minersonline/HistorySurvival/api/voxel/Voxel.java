package net.ddns.minersonline.HistorySurvival.api.voxel;

import com.google.gson.*;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.registries.Registries;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Voxel {

	private Vector3f position;
	private ResourceLocation type;
	private transient TexturedModel model;

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public TexturedModel getModel() {
		return model;
	}

	public ResourceLocation getType() {
		return type;
	}

	public void setModelType(ResourceLocation modelType) {
		this.type = modelType;
		this.model = Registries.MODEL_REGISTRY.get(type).create();
	}

	public static class JSON implements JsonDeserializer<Voxel> {
		@Override
		public Voxel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			JsonElement jsonType = jsonObject.get("type");
			ResourceLocation location = context.deserialize(jsonType, ResourceLocation.class);

			JsonElement jsonPosition = jsonObject.get("position");
			Vector3f position = context.deserialize(jsonPosition, Vector3f.class);

			return Registries.VOXEL_REGISTRY.get(location).create(position);
		}
	}
}
