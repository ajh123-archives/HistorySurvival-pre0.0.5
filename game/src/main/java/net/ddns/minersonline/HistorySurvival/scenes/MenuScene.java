package net.ddns.minersonline.HistorySurvival.scenes;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.ddns.minersonline.HistorySurvival.*;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.scenes.menus.MultiplayerMenu;
import net.ddns.minersonline.HistorySurvival.scenes.menus.SavedServer;
import net.ddns.minersonline.HistorySurvival.scenes.menus.WorldMenu;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class MenuScene extends Scene {
	private static final Logger logger = LoggerFactory.getLogger(MenuScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;

	private final List<Light> lights = new ArrayList<>();
	private final List<GuiTexture> guis = new ArrayList<>();

	private transient Camera camera;
	private transient Light sun;

	private transient final Game game;
	private transient static SavedServer currentServer = null;

	public transient static ImBoolean ENABLE_MULTIPLAYER = new ImBoolean(false);
	public transient static ImBoolean ENABLE_WORLDS = new ImBoolean(false);
	public transient static ImBoolean ENABLE_ERRORS = new ImBoolean(false);

	public transient static Throwable ERROR = null;

	public MenuScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		super();
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
	}

	@Override
	public void init() {
		masterRenderer.setBackgroundColour(new Vector3f(0.5f, 0.5f, 0.5f));
		sun = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(0.6f, 0.6f,0.6f));
		lights.add(sun);

		camera = new Camera(null);
	}

	@Override
	public void update(float deltaTime) {
		camera.update();
	}

	@Override
	public void gui(ImBoolean debugAllowed) {
		ImGui.setNextWindowSize(500, 440);
		ImGui.begin("Menu");

		if (ImGui.button("Single Player")){
			ENABLE_WORLDS.set(true);
		}
		if (ImGui.button("Multiplayer")){
			ENABLE_MULTIPLAYER.set(true);
		}
		if (ImGui.button("Editor")){
			DelayedTask task = () -> Game.queue.add(() -> {
				ENABLE_ERRORS.set(true);
				EditorScene scene = new EditorScene(game, modelLoader, masterRenderer, guiRenderer);
				scene.setPrevScene(this);
				Game.setCurrentScene(scene);
			});
			Game.addTask(task);
		}
		ImGui.button("Options");
		ImGui.end();

		WorldMenu.run(ENABLE_WORLDS);
		MultiplayerMenu.run(ENABLE_MULTIPLAYER);

		if (ENABLE_ERRORS.get() && ERROR != null){
			ImGui.setNextWindowSize(400, 200);
			ImGui.begin("An error occurred :(", ENABLE_ERRORS);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ERROR.printStackTrace(pw);
			String stackTrace = sw.toString();
			ImGui.textWrapped(stackTrace);
			ImGui.end();
		}
	}

	@Override
	public void stop() {

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
