package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.DelayedTask;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.GameSettings;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.ClientPlayer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTextBox;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.network.NettyClient;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MenuScene extends Scene {
	private static final Logger logger = LoggerFactory.getLogger(MenuScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;

	private final List<ClientEntity> entityList = new ArrayList<>();
	private final List<Light> lights = new ArrayList<>();
	private final List<GuiTexture> guis = new ArrayList<>();

	private final FontGroup consolas;

	private Camera camera;
	private Light sun;

	private final Game game;
	private GUIText playText;
	private GUIText playParent;
	private GUIText multiStatus;
	private final GuiTextBox serverIP;
	private static final List<JSONTextComponent> intro = new ArrayList<>();
	private static final List<JSONTextComponent> sub_intro = new ArrayList<>();
	private boolean inMultiplayer = false;
	private boolean ignoreIP = true;

	public MenuScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;

		FontType font = new FontType(modelLoader.loadTexture("font/consolas.png"), "font/consolas.fnt");
		FontType font_bold = new FontType(modelLoader.loadTexture("font/consolas_bold.png"), "font/consolas_bold.fnt");
		FontType font_bold_italic = new FontType(modelLoader.loadTexture("font/consolas_bold_italic.png"), "font/consolas_bold_italic.fnt");
		FontType font_italic = new FontType(modelLoader.loadTexture("font/consolas_italic.png"), "font/consolas_italic.fnt");
		consolas = new FontGroup(font, font_bold, font_bold_italic, font, font, font_italic, font, font);

		this.serverIP = new GuiTextBox(consolas, new Vector2f(0, 0.4f), -1);
	}

	@Override
	public void init() {
		masterRenderer.setBackgroundColour(new Vector3f(0.5f, 0.5f, 0.5f));
		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
		lights.add(sun);

		intro.clear();
		intro.add(new JSONTextComponent("Welcome to History Survival!\n"));
		intro.add(new JSONTextComponent("Press Enter to play.\n"));
		intro.add(new JSONTextComponent("Press Space to play multiplayer.\n"));

		sub_intro.clear();
		sub_intro.add(new JSONTextComponent("Welcome to History Survival multiplayer!\n"));
		sub_intro.add(new JSONTextComponent("Press Escape to go back.\n"));
		sub_intro.add(new JSONTextComponent("Type the server IP below...\n"));

		camera = new Camera(null);
	}

	@Override
	public void update(KeyEvent keyEvent) {
		camera.update();

		if(playText != null && playParent != null) {
			playText.setVisible(false);
			playParent.remove();
			playParent = null;
			playText.remove();
			playText = null;
		}

		if(multiStatus != null) {
			multiStatus.setVisible(false);
			multiStatus.remove();
			multiStatus = null;
		}
		playParent = new GUIText("", 1.3f, consolas, new Vector2f(0f, 0f), -1, false);

		serverIP.setFocused(inMultiplayer);
		serverIP.setVisible(inMultiplayer);

		if(!inMultiplayer) {
			playText = JSONTextBuilder.build_string_array(intro, playParent, playText);
			if(multiStatus != null) {
				multiStatus.setVisible(false);
			}
		} else {
			playText = JSONTextBuilder.build_string_array(sub_intro, playParent, playText);
			multiStatus = new GUIText("Logged in as: "+ GameSettings.username, 1.3f, consolas, new Vector2f(0.6f, 0f), 10, false);
			multiStatus.setVisible(true);
			multiStatus.load();

			serverIP.setPosition(new Vector2f(0, 0.4f));
			serverIP.render();
			serverIP.setOnExecute(message -> {
				playText.setVisible(false);
				multiStatus.setVisible(false);

				AtomicBoolean isError = new AtomicBoolean(false);
				NettyClient client = new NettyClient(message.toString(), 36676);
				game.setCurrentScene(new ConnectingScene(
						"Connecting to server",
						"",
						game,
						modelLoader,
						masterRenderer,
						guiRenderer
				));

				DelayedTask task = () -> {
					MenuScene scene = this;
					try {
						client.call();
					} catch (Exception e) {
						Game.queue.add(() -> game.setCurrentScene(new ErrorScene(scene,
								"Disconnected",
								e.getMessage(),
								game,
								modelLoader,
								masterRenderer,
								guiRenderer
						)));

						isError.set(true);
					}
					if(!isError.get()) {
						Game.queue.add(() -> game.setCurrentScene(scene));
					}
				};
				game.addTask(task);
				return null;
			});
		}
		playText.setVisible(true);

		if(inMultiplayer){
			serverIP.update(keyEvent, ignoreIP);
			ignoreIP = false;
		}

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE) && !inMultiplayer){
			inMultiplayer = true;
		}

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_ENTER) && !inMultiplayer){
			game.setCurrentScene(new ConnectingScene(
					"Loading world...",
					"",
					game,
					modelLoader,
					masterRenderer,
					guiRenderer
			));

			DelayedTask task = () -> {
				MenuScene scene = this;
				Game.queue.add(() -> game.setCurrentScene(new MainScene(
						scene,
						game,
						modelLoader,
						masterRenderer,
						guiRenderer
				)));
			};
			game.addTask(task);
		}

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) && inMultiplayer){
			inMultiplayer = false;
			ignoreIP = true;
		}
	}

	@Override
	public void stop() {
		if(multiStatus != null){
			multiStatus.setVisible(false);
			multiStatus.remove();
			multiStatus = null;
		}
		if(playText != null){
			playText.setVisible(false);
			playText.remove();
			playText = null;
		}
		if(playParent != null) {
			playParent.remove();
			playParent = null;
		}
		if(serverIP != null) {
			serverIP.setVisible(false);
			serverIP.setFocused(false);
		}
	}

	@Override
	public World getWorld() {
		return null;
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
		return null;
	}

	@Override
	public List<ClientEntity> getEntities() {
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

	public Game getGame() {
		return game;
	}

	public ModelLoader getModelLoader() {
		return modelLoader;
	}

	public MasterRenderer getMasterRenderer() {
		return masterRenderer;
	}

	public GuiRenderer getGuiRenderer() {
		return guiRenderer;
	}
}
