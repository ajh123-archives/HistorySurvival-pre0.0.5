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
import net.ddns.minersonline.HistorySurvival.engine.terrains.TestWorld;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainScene extends Scene {
	private transient static final Logger logger = LoggerFactory.getLogger(MainScene.class);
	private transient ModelLoader modelLoader;
	private transient MasterRenderer masterRenderer;
	private transient GuiRenderer guiRenderer;

	private transient List<Light> lights = new ArrayList<>();
	private transient List<GuiTexture> guis = new ArrayList<>();

	private transient GameObject player;
	private transient Camera camera;
	private transient ChatSystem chatSystem;
	private transient MousePicker picker;
	private transient Light sun;

	private transient Vector3f worldCenter;

	private transient ParticleSystem particleSystem;
	private transient Game game;

	public MainScene() {
		levelLoaded = true;
		isRunning = false;
	}



	public void setModelLoader(ModelLoader modelLoader) {
		this.modelLoader = modelLoader;
	}

	public void setMasterRenderer(MasterRenderer masterRenderer) {
		this.masterRenderer = masterRenderer;
	}

	public void setGuiRenderer(GuiRenderer guiRenderer) {
		this.guiRenderer = guiRenderer;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public MainScene(Scene prevScene, Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this();
		levelLoaded = false;
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
		this.prevScene = prevScene;
	}

	public void setPrevScene(Scene prevScene){
		this.prevScene = prevScene;
	}

	@Override
	public void init() {
		masterRenderer.setBackgroundColour(new Vector3f(0.65f, 0.9f, 0.97f));
		ENABLE_FILES = true;
		metaData.world = new TestWorld(modelLoader, 1, 1, 15, 256);
		if(!levelLoaded) {
			//world = new VoidWorld();
			Random random = new Random();

			for (int i = 0; i < 400; i++) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * 800;
				float y = metaData.world.getHeightOfTerrain(x, z);

				if (i % 20 == 0) {
					GameObject tree = new GameObject();
					tree.addComponent(new MeshComponent(ModelType.LOW_POLY_TREE_MODEL.create()));
					tree.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), 1));
					addGameObject(tree);
				}

				x = random.nextFloat() * 800;
				z = random.nextFloat() * 800;
				y = metaData.world.getHeightOfTerrain(x, z);

				if (i % 20 == 0) {
					GameObject tree = new GameObject();
					tree.addComponent(new MeshComponent(ModelType.TREE_MODEL.create()));
					tree.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), 5));
					addGameObject(tree);
				}

				x = random.nextFloat() * 800;
				z = random.nextFloat() * 800;
				y = metaData.world.getHeightOfTerrain(x, z);

				if (i % 10 == 0) {
					// assigns a random texture for each fern from its texture atlas
					GameObject fern = new GameObject();
					fern.addComponent(new MeshComponent(ModelType.FERN_MODEL.create(), random.nextInt(4)));
					fern.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), .9f));
					addGameObject(fern);
				}

				if (i % 5 == 0) {
					GameObject grass = new GameObject();
					grass.addComponent(new MeshComponent(ModelType.GRASS_MODEL.create()));
					grass.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), 1));
					addGameObject(grass);
				}
			}
		}

		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f, 0.6f));
		lights.add(sun);

		float centerX = metaData.world.getXSize()/2;
		float centerZ = metaData.world.getZSize()/2;
		worldCenter = metaData.world.getTerrainPoint(centerX, centerZ, 10);

		if(!levelLoaded) {
			player = new GameObject();
			player.addComponent(new ControllableComponent(metaData.world));
			player.addComponent(new MeshComponent(ModelType.PLAYER_MODEL.create()));
			player.addComponent(new TransformComponent(new Vector3f(worldCenter), new Vector3f(0, 0, 0), .6f));
			addGameObject(player);

			camera = new Camera(player.getComponent(TransformComponent.class));
		} else {
			camera = new Camera(getPlayer());
		}


		GuiTexture gui = new GuiTexture(modelLoader.loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
		guis.add(gui);

		picker = new MousePicker(metaData.world, masterRenderer.getProjectionMatrix(), camera);

		ParticleTexture particleTexture = new ParticleTexture(modelLoader.loadTexture("grass.png"), 1, false);
		particleSystem = new ParticleSystem(particleTexture, 50, 0, 0.3f, 4, 2);
		particleSystem.randomizeRotation();
		particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
		particleSystem.setLifeError(0.1f);
		particleSystem.setSpeedError(0.4f);
		particleSystem.setScaleError(0.8f);
	}

	@Override
	public void update(float deltaTime) {
		camera.move();
		picker.update();
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
