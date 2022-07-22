package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {
	private static final Map<Integer, Entity> entities = new HashMap<>();
	private static final List<ClientEntity<? extends Entity>> clientEntities = new ArrayList<>();
	private static int lastId = -1;
	public static EntityHandler entityHandler;

	public static Entity addEntity(Entity entity){
		lastId += 1;
		entity.setId(lastId);
		entities.put(lastId, entity);
		return entity;
	}

	public static void addClientEntity(ClientEntity<? extends Entity> entity){
		clientEntities.add(entity);
	}

	public static Map<Integer, Entity> getEntities(){
		return entities;
	}
	public static List<ClientEntity<? extends Entity>> getClientEntities(){
		return clientEntities;
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
