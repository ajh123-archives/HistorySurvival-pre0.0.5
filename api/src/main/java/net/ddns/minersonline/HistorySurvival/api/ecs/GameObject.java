package net.ddns.minersonline.HistorySurvival.api.ecs;

import com.google.gson.*;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameObject implements CommandSender {
	private int id;
	private final List<Component> components = new ArrayList<>();
	private transient ChatHandler messageHandler;

	public GameObject() {}

	public final <T extends Component> T getComponent(Class<T> componentClass){
		for (Component c : components){
			if(componentClass.isAssignableFrom(c.getClass())){
				try {
					return componentClass.cast(c);
				} catch (ClassCastException e){
					e.printStackTrace();
					assert false : "Error casting";
				}
			}
		}
		return null;
	}

	public final Collection<Component> getComponents(){
		return components;
	}

	public final <T extends Component> void removeComponent(Class<T> componentClass){
		for (int i=0; i < components.size(); i ++){
			if(componentClass.isAssignableFrom(components.get(i).getClass())){
				components.remove(i);
				return;
			}
		}
	}

	public final void addComponent(Component c){
		components.add(c);
		c.gameObject = this;
	}

	public final void update(float deltaTime){
		for (Component component : components) {
			component.update(deltaTime);
		}
	}

	public void start(){
		for (Component component : components) {
			component.start();
		}
	}

	public final int getId() {
		return id;
	}

	public final void setId(int id) {
		this.id = id;
	}


	@Override
	public final void sendMessage(JSONTextComponent message) {
		if(this.messageHandler != null) {
			this.messageHandler.run(message);
		}
	}

	public final void onChatMessage(ChatHandler handler){
		this.messageHandler = handler;
	}
	public interface ChatHandler {
		void run(JSONTextComponent message);
	}

	public static class JSON implements JsonDeserializer<GameObject> {
		@Override
		public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			int id = jsonObject.get("id").getAsInt();
			JsonArray components = jsonObject.getAsJsonArray("components");

			GameObject object = new GameObject();
			object.id = id;

			for (JsonElement e : components){
				Component c = context.deserialize(e, Component.class);
				object.addComponent(c);
			}

			return object;
		}
	}
}
