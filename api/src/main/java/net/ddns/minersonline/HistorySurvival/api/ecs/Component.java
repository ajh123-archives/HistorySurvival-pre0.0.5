package net.ddns.minersonline.HistorySurvival.api.ecs;

import com.google.gson.*;
import imgui.ImGui;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public abstract class Component {
	public transient GameObject gameObject = null;


	public void update(float deltaTime){}

	public void start(){}

	public void debug(){
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field field : fields){
				boolean isTransient = Modifier.isTransient(field.getModifiers());
				if(!isTransient) {
					boolean isPrivate = Modifier.isPrivate(field.getModifiers());
					if (isPrivate) {
						field.setAccessible(true);
					}

					Class<?> type = field.getType();
					Object value = field.get(this);
					String name = field.getName();

					if (type == int.class) {
						int val = (int) value;
						int[] imInt = {val};
						if (ImGui.dragInt(name, imInt)) {
							field.set(this, imInt[0]);
						}
					} else if (type == float.class) {
						float val = (float) value;
						float[] imInt = {val};
						if (ImGui.dragFloat(name, imInt)) {
							field.set(this, imInt[0]);
						}
					} else if (type == boolean.class) {
						boolean val = (boolean) value;
						if (ImGui.checkbox(name, val)) {
							field.set(this, !val);
						}
					} else if (type == Vector3f.class) {
						Vector3f val = (Vector3f) value;
						float[] imVec = {val.x, val.y, val.z};
						if (ImGui.dragFloat3(name, imVec)) {
							val.set(imVec);
						}
					}

					if (isPrivate) {
						field.setAccessible(false);
					}
				}
			}
		} catch (IllegalAccessException e){
			GameHook.getInstance().getLogger().error("An error occurred!", e);
		}
	}

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
