package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.EntityManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.ClientPlayer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
	private boolean isRunning = false;
	protected List<GameObject> gameObjects = new ArrayList<>();

	public void init(){}
	public abstract void update(float deltaTime);//KeyEvent keyEvent
	public void stop(){}
	public final void start(){
		for (GameObject go : gameObjects){
			go.start();
		}
	}

	public void addGameObject(GameObject go){
		EntityManager.addEntity(go);
		if (!isRunning){
			gameObjects.add(go);
		} else {
			gameObjects.add(go);
			go.start();
		}
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public abstract World getWorld();
	public abstract Camera getCamera();
	public abstract TransformComponent getPlayer();

	public abstract List<GuiTexture> getGUIs();
	public abstract List<Light> getLights();
	public abstract Light getSun();

	public void initDebug(){}

	public final void renderDebug(){

	}
}
