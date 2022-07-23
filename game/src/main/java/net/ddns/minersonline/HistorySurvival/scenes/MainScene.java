package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.engine.entities.ControllableComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.MeshComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.EntityManager;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.ObjLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.*;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleTexture;
import net.ddns.minersonline.HistorySurvival.engine.terrains.TestWorld;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainScene extends Scene {
	private static final Logger logger = LoggerFactory.getLogger(MainScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;

	List<Light> lights = new ArrayList<>();
	List<GuiTexture> guis = new ArrayList<>();

	TexturedModel treeModel;
	TexturedModel lowPolyTreeModel;
	TexturedModel grassModel;
	ModelTexture fernTextureAtlas;
	TexturedModel fernModel;

	GameObject player;
	Camera camera;
	ChatSystem chatSystem;
	MousePicker picker;
	Light sun;

	Vector3f worldCenter;
	World world;

	ParticleSystem particleSystem;
	Game game;
	Scene prevScene;

	public MainScene(Scene prevScene, Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
		this.prevScene = prevScene;

		masterRenderer.setBackgroundColour(new Vector3f(0.65f, 0.9f, 0.97f));

		// Tree entity
		treeModel = new TexturedModel(ObjLoader.loadObjModel("tree.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("tree.png")));

		// Low poly tree entity
		lowPolyTreeModel = new TexturedModel(ObjLoader.loadObjModel("lowPolyTree.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("lowPolyTree.png")));

		// Grass entity
		grassModel = new TexturedModel(ObjLoader.loadObjModel("grassModel.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("grassTexture.png")));
		grassModel.getModelTexture().setHasTransparency(true);
		grassModel.getModelTexture().setUseFakeLighting(true);

		// Fern entity
		fernTextureAtlas = new ModelTexture(modelLoader.loadTexture("fern.png"));
		fernTextureAtlas.setNumberOfRowsInTextureAtlas(2);
		fernModel = new TexturedModel(ObjLoader.loadObjModel("fern.obj", modelLoader), fernTextureAtlas);
		fernModel.getModelTexture().setHasTransparency(true);
	}

	@Override
	public void init() {
		world = new TestWorld(modelLoader, 3, 3, 15, 256);
		//world = new VoidWorld();
		Random random = new Random();

		for (int i = 0; i < 400; i++) {
			float x = random.nextFloat() * 800 - 400;
			float z = random.nextFloat() * -600;
			float y = world.getHeightOfTerrain(x, z);

			if (i % 20 == 0) {
				GameObject tree = new GameObject();
				tree.addComponent(new MeshComponent(lowPolyTreeModel));
				tree.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), 1));
				addGameObject(tree);
			}

			x = random.nextFloat() * 800 - 400;
			z = random.nextFloat() * -600;
			y = world.getHeightOfTerrain(x, z);

			if (i % 20 == 0) {
				GameObject tree = new GameObject();
				tree.addComponent(new MeshComponent(treeModel));
				tree.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), 5));
				addGameObject(tree);
			}

			x = random.nextFloat() * 800 - 400;
			z = random.nextFloat() * -600;
			y = world.getHeightOfTerrain(x, z);

			if (i % 10 == 0) {
				// assigns a random texture for each fern from its texture atlas
				GameObject fern = new GameObject();
				fern.addComponent(new MeshComponent(fernModel, random.nextInt(4)));
				fern.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), .9f));
				addGameObject(fern);
			}

			if (i % 5 == 0) {
				GameObject grass = new GameObject();
				grass.addComponent(new MeshComponent(grassModel));
				grass.addComponent(new TransformComponent(new Vector3f(x, y, z), new Vector3f(0, random.nextFloat() * 360, 0), 1));
				addGameObject(grass);
			}
		}

		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
		lights.add(sun);

		TexturedModel playerOBJ = new TexturedModel(ObjLoader.loadObjModel("person.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("playerTexture.png")));

		float centerX = world.getXSize()/2;
		float centerZ = world.getZSize()/2;
		worldCenter = world.getTerrainPoint(centerX, centerZ, 10);

		player = new GameObject();
		player.addComponent(new ControllableComponent(world));
		player.addComponent(new MeshComponent(playerOBJ));
		player.addComponent(new TransformComponent(new Vector3f(worldCenter), new Vector3f(0, 0, 0), .6f));
		addGameObject(player);

		camera = new Camera(player.getComponent(TransformComponent.class));

		GuiTexture gui = new GuiTexture(modelLoader.loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
		guis.add(gui);

		picker = new MousePicker(world, masterRenderer.getProjectionMatrix(), camera);

		ParticleTexture particleTexture = new ParticleTexture(modelLoader.loadTexture("grass.png"),  1, false);
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
	public void initDebug() {

	}

	@Override
	public void stop() {
		ParticleMaster.stop();
		ParticleMaster.update(camera);
//		chatSystem.setInChat(false);
//		chatSystem.cleanUp();
	}

	@Override
	public World getWorld() {
		return world;
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
