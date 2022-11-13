package net.ddns.minersonline.HistorySurvival.scenes;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.ControllableComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.MeshComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.*;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleTexture;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MainScene extends Scene {
	private transient static final Logger logger = LoggerFactory.getLogger(MainScene.class);
	private transient ModelLoader modelLoader;
	private transient MasterRenderer masterRenderer;
	private transient SceneMetaData worldData;

	private transient List<Light> lights = new ArrayList<>();
	private transient List<GuiTexture> guis = new ArrayList<>();

	private transient GameObject player;
	private transient Camera camera;
	private transient Light sun;

	private transient Vector3f worldCenter;

	private transient ParticleSystem particleSystem;

	public MainScene() {
		super();
		levelLoaded = true;
		isRunning = false;
	}


	public void setModelLoader(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
	}

	public void setMasterRenderer(MasterRenderer masterRenderer) {
		this.masterRenderer = masterRenderer;
	}

	public MainScene(Scene prevScene, ModelLoader modelLoader, MasterRenderer masterRenderer, SceneMetaData from) {
		this();
		levelLoaded = false;
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.prevScene = prevScene;
		this.worldData = from;
	}

	@Override
	public void init() {
		if (!hasInited) {
			masterRenderer.setBackgroundColour(new Vector3f(0.65f, 0.9f, 0.97f));
			ENABLE_FILES = true;

			sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f, 0.6f));
			lights.add(sun);

			worldCenter = new Vector3f(0, 0, 0);

			if (!levelLoaded) {
				player = new GameObject();
				player.addComponent(new ControllableComponent(metaData.world));
				player.addComponent(new MeshComponent(ModelType.PLAYER_MODEL.create()));
				player.addComponent(new TransformComponent(new Vector3f(worldCenter), new Vector3f(0, 0, 0), .6f));
				addGameObject(player);

				camera = new Camera(player.getComponent(TransformComponent.class));
			} else {
				camera = new Camera(getPlayer());
			}
			System.out.println(camera);
			metaData.name = worldData.name;
			metaData.world.start(getPlayer());


			GuiTexture gui = new GuiTexture(GameHook.getLoader().loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
			guis.add(gui);

			ParticleTexture particleTexture = new ParticleTexture(GameHook.getLoader().loadTexture("grass.png", false, "assets/history_survival/voxels/textures/"), 1, false);
			particleSystem = new ParticleSystem(particleTexture, 50, 0, 0.3f, 4, 2);
			particleSystem.randomizeRotation();
			particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
			particleSystem.setLifeError(0.1f);
			particleSystem.setSpeedError(0.4f);
			particleSystem.setScaleError(0.8f);


			getPlayer().position.y = 30;
			hasInited = true;
		}
	}


	@Override
	public void update(float deltaTime) {
		camera.move();
		camera.update();

		try {
			Vector3f pos = new Vector3f(worldCenter);
			pos.y += 20;
			//particleSystem.generateParticles(pos);
		} catch (Exception e){
			Game.logger.info("AHHHHh");
		}
	}

	@Override
	public void gui(ImBoolean debugAllowed) {
		if(debugAllowed.get()) {
			ImGui.begin("Hello?");
			ImGui.end();
		}
	}

	@Override
	public void stop() {
		metaData.world.stop();
		ParticleMaster.stop();
//		chatSystem.setInChat(false);
//		chatSystem.cleanUp();
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public List<GuiTexture> getGUIs() {
		return guis;
	}

	@Override
	public TransformComponent getPlayer() {
		GameObject player = GameObjectManager.getGameObjectByFirstComponent(ControllableComponent.class);
		if(player == null){return new TransformComponent();}
		ControllableComponent component = player.getComponent(ControllableComponent.class);
		if (component != null) {
			component.setWorld(metaData.world);
		}
		TransformComponent transformComponent = player.getComponent(TransformComponent.class);
		if (camera != null && transformComponent != null) {
			camera.setPlayer(transformComponent);
		}
		return player.getComponent(TransformComponent.class);
	}

	@Override
	public List<Light> getLights() {
		return lights;
	}

	@Override
	public Light getSun() {
		return sun;
	}
}
