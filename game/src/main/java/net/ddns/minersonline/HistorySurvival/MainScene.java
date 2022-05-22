package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.ClientPlayer;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.ObjLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleSystem;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleTexture;
import net.ddns.minersonline.HistorySurvival.engine.terrains.TestWorld;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.textures.ModelTexture;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainScene extends Scene{
	private static final Logger logger = LoggerFactory.getLogger(MainScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;

	List<Entity> entityList = new ArrayList<>();
	List<Light> lights = new ArrayList<>();
	List<GuiTexture> guis = new ArrayList<>();

	FontGroup consolas;
	TexturedModel treeModel;
	TexturedModel lowPolyTreeModel;
	TexturedModel grassModel;
	ModelTexture fernTextureAtlas;
	TexturedModel fernModel;

	Player player;
	ClientPlayer clientPlayer;
	Camera camera;
	ChatSystem chatSystem;
	MousePicker picker;
	Light sun;

	Vector3f worldCenter;
	World world;

	ParticleSystem particleSystem;
	Game game;

	public MainScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;

		masterRenderer.setBackgroundColour(new Vector3f(0.65f, 0.9f, 0.97f));

		FontType font = new FontType(modelLoader.loadTexture("font/consolas.png"), "font/consolas.fnt");
		FontType font_bold = new FontType(modelLoader.loadTexture("font/consolas_bold.png"), "font/consolas_bold.fnt");
		FontType font_bold_italic = new FontType(modelLoader.loadTexture("font/consolas_bold_italic.png"), "font/consolas_bold_italic.fnt");
		FontType font_italic = new FontType(modelLoader.loadTexture("font/consolas_italic.png"), "font/consolas_italic.fnt");
		consolas = new FontGroup(font, font_bold, font_bold_italic, font, font, font_italic, font, font);

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
		Random random = new Random();

		for (int i = 0; i < 400; i++) {
			float x = random.nextFloat() * 800 - 400;
			float z = random.nextFloat() * -600;
			float y = world.getHeightOfTerrain(x, z);

			if (i % 20 == 0) {
				entityList.add(new Entity(lowPolyTreeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
			}

			x = random.nextFloat() * 800 - 400;
			z = random.nextFloat() * -600;
			y = world.getHeightOfTerrain(x, z);

			if (i % 20 == 0) {
				entityList.add(new Entity(treeModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 5));
			}

			x = random.nextFloat() * 800 - 400;
			z = random.nextFloat() * -600;
			y = world.getHeightOfTerrain(x, z);

			if (i % 10 == 0) {
				// assigns a random texture for each fern from its texture atlas
				entityList.add(new Entity(fernModel, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
			}

			if (i % 5 == 0) {
				entityList.add(new Entity(grassModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));
			}
		}

		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
		lights.add(sun);

//		TexturedModel lamp = new TexturedModel(ObjLoader.loadObjModel("lamp.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("lamp.png")));
//
//		Light moveLight = new Light(
//				new Vector3f(400, 22, -293),
//				new Vector3f(2, 0,0),
//				new Vector3f(1, 0.01f,0.002f));
//		lights.add(moveLight);
//		Entity moveEntity = new Entity(lamp,
//				new Vector3f(400, 9, -293),
//				0,
//				0,
//				0,
//				1);
//		entityList.add(moveEntity);
//
//
//		lights.add(new Light(
//				new Vector3f(370, 17, -293),
//				new Vector3f(0, 2,0),
//				new Vector3f(1, 0.01f,0.002f)));
//		entityList.add(new Entity(lamp,
//				new Vector3f(370, 4.2f, -293),
//				0,
//				0,
//				0,
//				1));


		TexturedModel playerOBJ = new TexturedModel(ObjLoader.loadObjModel("person.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("playerTexture.png")));

		float centerX = world.getXSize()/2;
		float centerZ = world.getZSize()/2;
		worldCenter = world.getTerrainPoint(centerX, centerZ, 10);

		player = new Player(world, playerOBJ, new Vector3f(worldCenter), 0,0,0,0.6f);
		entityList.add(player);
		camera = new Camera(player);
		clientPlayer = new ClientPlayer(player);

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

		chatSystem = new ChatSystem(consolas, clientPlayer);
	}

	@Override
	public void update(KeyEvent keyEvent) {
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
			game.setCurrentScene(new MenuScene(game, modelLoader, masterRenderer, guiRenderer));
		}

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_R)){
			player.getPosition().set(new Vector3f(worldCenter));
		}

		chatSystem.update(keyEvent);

		if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_T) && chatSystem.notIsInChat()){
			chatSystem.setInChat(true);
		}

		if(chatSystem.notIsInChat()) {
			player.checkInputs();
			camera.move();
			picker.update();
		}

		player.move();
		camera.update();

		Vector3f pos = new Vector3f(worldCenter);
		pos.y += 20;
		particleSystem.generateParticles(pos);
		ParticleMaster.update(camera);
	}

	@Override
	public void stop() {
		ParticleMaster.stop();
		ParticleMaster.update(camera);
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
	public Player getPlayer() {
		return player;
	}

	@Override
	public List<Entity> getEntities() {
		return entityList;
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
