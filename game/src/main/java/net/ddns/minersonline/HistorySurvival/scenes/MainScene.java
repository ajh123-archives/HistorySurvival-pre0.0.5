package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.api.entities.EntityType;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.EntityManager;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.ObjLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.*;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleTexture;
import net.ddns.minersonline.HistorySurvival.engine.terrains.TestWorld;
import net.ddns.minersonline.HistorySurvival.engine.terrains.VoidWorld;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
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

	ClientPlayer player;
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
				Entity entity = EntityManager.addEntity(EntityType.EMPTY_ENTITY.create());
				EntityManager.addClientEntity(new ClientEntity<>(entity, lowPolyTreeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
			}

			x = random.nextFloat() * 800 - 400;
			z = random.nextFloat() * -600;
			y = world.getHeightOfTerrain(x, z);

			if (i % 20 == 0) {
				Entity entity = EntityManager.addEntity(EntityType.EMPTY_ENTITY.create());
				EntityManager.addClientEntity(new ClientEntity<>(entity, treeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 5));
			}

			x = random.nextFloat() * 800 - 400;
			z = random.nextFloat() * -600;
			y = world.getHeightOfTerrain(x, z);

			if (i % 10 == 0) {
				// assigns a random texture for each fern from its texture atlas
				Entity entity = EntityManager.addEntity(EntityType.EMPTY_ENTITY.create());
				EntityManager.addClientEntity(new ClientEntity<>(entity, fernModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
			}

			if (i % 5 == 0) {
				Entity entity = EntityManager.addEntity(EntityType.EMPTY_ENTITY.create());
				EntityManager.addClientEntity(new ClientEntity<>(entity, grassModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
			}
		}

		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
		lights.add(sun);

		TexturedModel playerOBJ = new TexturedModel(ObjLoader.loadObjModel("person.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("playerTexture.png")));

		float centerX = world.getXSize()/2;
		float centerZ = world.getZSize()/2;
		worldCenter = world.getTerrainPoint(centerX, centerZ, 10);

		player = new ClientPlayer(world, playerOBJ, new Vector3f(worldCenter), 0,0,0,0.6f);
		EntityManager.addEntity(player.getEntity());;
		EntityManager.addClientEntity(player);
		camera = new Camera(player);

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
	public void update() {
		player.checkInputs();
		camera.move();
		picker.update();

		player.move();
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
		chatSystem.setInChat(false);
		chatSystem.cleanUp();
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
	public ClientPlayer getPlayer() {
		return player;
	}

	@Override
	public List<ClientEntity<? extends Entity>> getEntities() {
		return EntityManager.getClientEntities();
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
