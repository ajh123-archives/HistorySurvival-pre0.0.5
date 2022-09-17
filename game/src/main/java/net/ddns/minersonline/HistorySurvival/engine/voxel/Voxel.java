package net.ddns.minersonline.HistorySurvival.engine.voxel;

import com.google.gson.*;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.registries.Registries;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Voxel {

	private Vector3f position;
	private final ResourceLocation type;
	private transient TexturedModel model;

	public Voxel(ResourceLocation type, Vector3f position) {
		this.position = position;
		this.type = type;
		this.model = Registries.MODEL_REGISTRY.get(type).create();
	}

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

	public static class JSON implements JsonDeserializer<Voxel> {
		@Override
		public Voxel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();

			JsonElement jsonType = jsonObject.get("type");
			ResourceLocation location = context.deserialize(jsonType, ResourceLocation.class);

			JsonElement jsonPosition = jsonObject.get("position");
			Vector3f position = context.deserialize(jsonPosition, Vector3f.class);

			return new Voxel(location, position);
		}
	}
}
