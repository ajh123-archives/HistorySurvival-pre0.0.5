package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;

import java.util.*;

public class GameObjectManager {
	private static final Map<Integer, GameObject> game_objects = new HashMap<>();
	private static int lastId = -1;
	public static UpdateHandler updateHandler;

	public static void reset(){
		game_objects.clear();
		lastId = -1;
	}

	public static void addGameObject(GameObject gameObject){
		lastId += 1;
		gameObject.setId(lastId);
		game_objects.put(lastId, gameObject);
	}

	public static Collection<GameObject> getGameObjects(){
		return game_objects.values();
	}

	public static GameObject getGameObject(int id){
		return game_objects.get(id);
	}

	public static GameObject getGameObjectByFirstComponent(Class<? extends Component> component ){
		GameObject found = null;
		for (GameObject go : getGameObjects()){
			if (go.getComponent(component) != null){
				found = go;
				break;
			}
		}
		return found;
	}

	private static void updateEntity(GameObject entity, float deltaTime){
		entity.update(deltaTime);
		if(updateHandler != null){
			updateHandler.run(entity);
		}
	}

	public static void update(float deltaTime ){
		game_objects.forEach((key, value) -> updateEntity(value, deltaTime));
	}

	public interface UpdateHandler {
		void run(GameObject entity);
	}
}
