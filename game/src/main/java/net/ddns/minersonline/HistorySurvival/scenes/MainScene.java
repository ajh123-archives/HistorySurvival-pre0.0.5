package net.ddns.minersonline.HistorySurvival.scenes;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.ControllableComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.MeshComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.*;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleTexture;
import net.ddns.minersonline.HistorySurvival.engine.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.engine.voxel.VoxelRenderer;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.TestWorld;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MainScene extends Scene {
	private transient static final Logger logger = LoggerFactory.getLogger(MainScene.class);
	private transient ModelLoader modelLoader;
	private transient MasterRenderer masterRenderer;

	private transient List<Light> lights = new ArrayList<>();
	private transient List<GuiTexture> guis = new ArrayList<>();

	private transient GameObject player;
	private transient Camera camera;
	private transient Light sun;

	private transient Vector3f worldCenter;

	private transient ParticleSystem particleSystem;

	private transient volatile boolean generating = true;

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

	public MainScene(Scene prevScene, ModelLoader modelLoader, MasterRenderer masterRenderer) {
		this();
		levelLoaded = false;
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.prevScene = prevScene;
	}

	@Override
	public void init() {
		masterRenderer.setBackgroundColour(new Vector3f(0.65f, 0.9f, 0.97f));
		ENABLE_FILES = true;

		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f, 0.6f));
		lights.add(sun);

		worldCenter = new Vector3f(0, 0, 0);

		if(!levelLoaded) {
			metaData.voxels = new ConcurrentHashMap<>();
			player = new GameObject();
			player.addComponent(new ControllableComponent(metaData.voxels));
			player.addComponent(new MeshComponent(ModelType.PLAYER_MODEL.create()));
			player.addComponent(new TransformComponent(new Vector3f(worldCenter), new Vector3f(0, 0, 0), .6f));
			addGameObject(player);

			camera = new Camera(player.getComponent(TransformComponent.class));
		} else {
			camera = new Camera(getPlayer());
		}


		GuiTexture gui = new GuiTexture(modelLoader.loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
		guis.add(gui);

		ParticleTexture particleTexture = new ParticleTexture(modelLoader.loadTexture("grass.png"), 1, false);
		particleSystem = new ParticleSystem(particleTexture, 50, 0, 0.3f, 4, 2);
		particleSystem.randomizeRotation();
		particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
		particleSystem.setLifeError(0.1f);
		particleSystem.setSpeedError(0.4f);
		particleSystem.setScaleError(0.8f);

		int chunkDistance = 20*2;
		new Thread(() -> {
			while (generating) {
				Vector3f position = getPlayer().position;
				for (int x = (int) (position.x - chunkDistance); x < (int) position.x; x++) {
					for (int z = (int) position.z; z < (int) (position.z + chunkDistance); z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (metaData.voxels.get(pos) == null) {
							metaData.voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
					}
				}
				for (int x = (int) position.x; x < (int) (position.x + chunkDistance); x++) {
					for (int z = (int) position.z; z < (int) (position.z + chunkDistance); z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (metaData.voxels.get(pos) == null) {
							metaData.voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
					}
				}
			}
		}).start();
		new Thread(() -> {
			while (generating) {
				Vector3f position = getPlayer().position;
				for (int x = (int) (position.x - chunkDistance); x < (int) position.x; x++) {
					for (int z = (int) (position.z - chunkDistance); z < (int) position.z; z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (metaData.voxels.get(pos) == null) {
							metaData.voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
					}
				}
				for (int x = (int) position.x; x < (int) (position.x + chunkDistance); x++) {
					for (int z = (int) (position.z - chunkDistance); z < (int) position.z; z++) {
						Vector3f pos = new Vector3f(x, 0, z);
						if (metaData.voxels.get(pos) == null) {
							metaData.voxels.put(pos, new Voxel(
									ModelType.GRASS_MODEL.getRegistryName(), pos
							));
						}
					}
				}
			}
		}).start();
		new Thread(() -> {
			while (generating) {
				for (Vector3f pos : metaData.voxels.keySet()) {
					TransformComponent player = getPlayer();
					if (player == null){continue;}
					int distX = (int) (player.position.x - pos.x);
					int distZ = (int) (player.position.z - pos.z);

					if (distX < 0){
						distX = -distX;
					}
					if (distZ < 0){
						distZ = -distZ;
					}

					if ((distX > chunkDistance) || ( distZ > chunkDistance)){
						metaData.voxels.remove(pos);
					}
				}
			}
		}).start();
		getPlayer().position.y = 6;
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
		generating = false;
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
			component.setWorld(metaData.voxels);
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
