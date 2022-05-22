package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Entity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MenuScene extends Scene{
	private static final Logger logger = LoggerFactory.getLogger(MenuScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;

	private List<Entity> entityList = new ArrayList<>();
	private List<Light> lights = new ArrayList<>();
	private List<GuiTexture> guis = new ArrayList<>();

	private FontGroup consolas;

	private Camera camera;
	private Light sun;

	private Game game;
	private GUIText playText;
	private GUIText playParent;
	private static final List<JSONTextComponent> intro = new ArrayList<>();

	public MenuScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;

		masterRenderer.setBackgroundColour(new Vector3f(0.5f, 0.5f, 0.5f));

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

		intro.clear();
		intro.add(new JSONTextComponent("Welcome to History Survival!\n"));
		intro.add(new JSONTextComponent("Press Enter to play.\n"));

		camera = new Camera(null);
	}

	@Override
	public void update(KeyEvent keyEvent) {
		camera.update();

		if(playText != null && playParent != null) {
			playText.setVisible(false);
			playParent.remove();
			playText.remove();
		}
		playParent = new GUIText("", 1.3f, consolas, new Vector2f(10f, 0.5f), -1, false);
		playText = JSONTextBuilder.build_string_array(intro, playParent);
		playText.setVisible(true);

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_ENTER)){
			game.setCurrentScene(new MainScene(game, modelLoader, masterRenderer, guiRenderer));
		}
	}

	@Override
	public void stop() {
		playText.setVisible(false);
		playText.remove();
		playText = null;
		playParent.remove();
		playParent = null;
		logger.info("Exited from menu");
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
	public Player getPlayer() {
		return null;
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
