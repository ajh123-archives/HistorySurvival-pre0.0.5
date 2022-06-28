package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {
	private static final Map<Integer, Entity> entities = new HashMap<>();
	private static int lastId = -1;
	public static EntityHandler entityHandler;

	public static int addEntity(Entity entity){
		lastId += 1;
		entity.setId(lastId);
		entities.put(lastId, entity);
		return lastId;
	}

	public static Map<Integer, Entity> getEntities(){
		return entities;
	}

	public static void addPlayer(PlayerEntity entity){
		entities.put(entity.getId(), entity);
	}

	private static void updateEntity(Entity entity){
		if(entityHandler != null){
			entityHandler.run(entity);
		}
	}

	public static void update(){
		entities.forEach((key, value) -> updateEntity(value));
	}

	public interface EntityHandler {
		void run(Entity entity);
	}
}
