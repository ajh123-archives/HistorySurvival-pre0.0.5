package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;

import java.util.*;

public class EntityManager {
	private static final Map<Integer, GameObject> entities = new HashMap<>();
	private static int lastId = -1;
	public static EntityHandler entityHandler;

	public static void reset(){
		entities.clear();
	}

	public static GameObject addEntity(GameObject entity){
		lastId += 1;
		entity.setId(lastId);
		entities.put(lastId, entity);
		return entity;
	}

	public static Collection<GameObject> getClientEntities(){
		return entities.values();
	}

	private static void updateEntity(GameObject entity, float deltaTime){
		entity.update(deltaTime);
		if(entityHandler != null){
			entityHandler.run(entity);
		}
	}

	public static void update(float deltaTime ){
		entities.forEach((key, value) -> updateEntity(value, deltaTime));
	}

	public interface EntityHandler {
		void run(GameObject entity);
	}
}
