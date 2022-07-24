package net.ddns.minersonline.HistorySurvival.scenes;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
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

public class ErrorScene extends Scene {
	private static final Logger logger = LoggerFactory.getLogger(ErrorScene.class);
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
	private GUIText headerParent;
	private GUIText bodyParent;
	private GUIText header;
	private GUIText body;
	private Vector2f headerPos = new Vector2f(0, 0);
	private Vector2f bodyPos = new Vector2f(0, 0.2f);
	private static final List<JSONTextComponent> header_text = new ArrayList<>();
	private static final List<JSONTextComponent> body_text = new ArrayList<>();
	private Scene prevScene;

	private String errorName;
	private String errorBody;

	public ErrorScene(Scene prevScene, String errorName, String errorBody, Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
		this.prevScene = prevScene;
		this.errorBody = errorBody;
		this.errorName = errorName;

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

		header_text.clear();
		header_text.add(new JSONTextComponent(this.errorName+"\n"));

		body_text.clear();
		body_text.add(new JSONTextComponent(this.errorBody+"\n\n"));
		body_text.add(new JSONTextComponent("Press Escape to go back.\n"));

		camera = new Camera(null);
	}

	@Override
	public void update(float deltaTime) {
		camera.update();

		if(header != null && headerParent != null) {
			header.setVisible(false);
			headerParent.remove();
			headerParent = null;
			header.remove();
		}
		headerParent = new GUIText("", 1.3f, consolas, new Vector2f(headerPos), 20, false);
		header = JSONTextBuilder.build_string_array(header_text, headerParent, header);
		header.setVisible(true);

		if(body != null && bodyParent != null) {
			body.setVisible(false);
			bodyParent.remove();
			bodyParent = null;
			body.remove();
		}
		bodyParent = new GUIText("", 1.3f, consolas, new Vector2f(bodyPos), 20, false);
		body = JSONTextBuilder.build_string_array(body_text, bodyParent, body);
		body.setVisible(true);
//		bodyPos.x = body.getTextString().length()/10f;
//		headerPos.x = header.getTextString().length()/10f;

		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
			game.setCurrentScene(prevScene);
		}
	}

	@Override
	public void gui(boolean debugAllowed) {

	}

	@Override
	public void stop() {
		if (this.header != null) {
			header.setVisible(false);
			header.remove();
			header = null;
		}
		if (this.headerParent != null) {
			headerParent.remove();
			headerParent = null;
		}

		if (this.body != null) {
			body.setVisible(false);
			body.remove();
			body = null;
		}
		if(this.bodyParent != null) {
			bodyParent.remove();
			bodyParent = null;
		}
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
		return null;
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
