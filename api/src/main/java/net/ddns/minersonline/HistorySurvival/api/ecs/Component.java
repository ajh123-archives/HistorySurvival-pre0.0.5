package net.ddns.minersonline.HistorySurvival.api.ecs;

import com.google.gson.*;

import java.lang.reflect.Type;

public abstract class Component {
	public transient GameObject gameObject = null;


	public void update(float deltaTime){}

	public void start(){}

	public void debug(){}

	public static class JSON implements JsonDeserializer<Component>, JsonSerializer<Component> {
		@Override
		public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			String type = jsonObject.get("type").getAsString();
			JsonElement element = jsonObject.get("properties");

			try {
				return context.deserialize(element, Class.forName(type));
			} catch (ClassNotFoundException e){
				throw new JsonParseException("Unknown type `"+type+"` to parse a `Component` from.", e);
			}
		}

		@Override
		public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();
			result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
			result.add("properties", context.serialize(src, src.getClass()));
			return result;
		}
	}
}
