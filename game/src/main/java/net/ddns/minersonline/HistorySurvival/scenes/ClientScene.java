package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.EntityManager;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.ObjLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.ClientPlayer;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.particles.ParticleMaster;
import net.ddns.minersonline.HistorySurvival.engine.terrains.VoidWorld;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.utils.MousePicker;
import net.ddns.minersonline.HistorySurvival.network.ClientHandler;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ClientScene extends Scene {
	private static final Logger logger = LoggerFactory.getLogger(ClientScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;
	List<ClientEntity> entityList = new ArrayList<>();

	List<Light> lights = new ArrayList<>();
	List<GuiTexture> guis = new ArrayList<>();

	ClientHandler network;

	FontGroup consolas;

	ClientPlayer player;
	Camera camera;
	ChatSystem chatSystem;
	MousePicker picker;
	Light sun;

	World world;

	Game game;
	Scene prevScene;

	public ClientScene(ClientHandler handler, Scene prevScene, Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
		this.prevScene = prevScene;
		this.network = handler;
		Keyboard.clear();

		masterRenderer.setBackgroundColour(new Vector3f(0.65f, 0.9f, 0.97f));

		FontType font = new FontType(modelLoader.loadTexture("font/consolas.png"), "font/consolas.fnt");
		FontType font_bold = new FontType(modelLoader.loadTexture("font/consolas_bold.png"), "font/consolas_bold.fnt");
		FontType font_bold_italic = new FontType(modelLoader.loadTexture("font/consolas_bold_italic.png"), "font/consolas_bold_italic.fnt");
		FontType font_italic = new FontType(modelLoader.loadTexture("font/consolas_italic.png"), "font/consolas_italic.fnt");
		consolas = new FontGroup(font, font_bold, font_bold_italic, font, font, font_italic, font, font);
	}

	@Override
	public void init() {
		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
		lights.add(sun);
		world = new VoidWorld();

		TexturedModel playerOBJ = new TexturedModel(ObjLoader.loadObjModel("person.obj", modelLoader), new ModelTexture(modelLoader.loadTexture("playerTexture.png")));
		player = new ClientPlayer(world, playerOBJ, new Vector3f(0, 0, 0), 0,0,0,0.6f);
		player.getEntity().setId(network.entityId);
		EntityManager.addPlayer(player.getEntity());
		entityList.add(player);
		camera = new Camera(player);

		GuiTexture gui = new GuiTexture(modelLoader.loadTexture("health.png"), new Vector2f(-0.75f, -0.85f), new Vector2f(0.25f, 0.15f));
		guis.add(gui);

		picker = new MousePicker(world, masterRenderer.getProjectionMatrix(), camera);
		chatSystem = new ChatSystem(consolas, player);
		network.state = 3;
	}

	@Override
	public void update() {
		//chatSystem.update(keyEvent);

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

		ParticleMaster.update(camera);
		logger.info("UPDATE");

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
			game.setCurrentScene(prevScene);
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
	public List<Light> getLights() {
		return lights;
	}

	@Override
	public Light getSun() {
		return sun;
	}
}
