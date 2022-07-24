package net.ddns.minersonline.HistorySurvival.scenes;

import com.sun.tools.javac.Main;
import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import net.ddns.minersonline.HistorySurvival.DelayedTask;
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
import net.ddns.minersonline.HistorySurvival.engine.terrains.TestWorld;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuScene extends Scene {
	private static final Logger logger = LoggerFactory.getLogger(MenuScene.class);
	private final ModelLoader modelLoader;
	private final MasterRenderer masterRenderer;
	private final GuiRenderer guiRenderer;

	private final List<ClientEntity> entityList = new ArrayList<>();
	private final List<Light> lights = new ArrayList<>();
	private final List<GuiTexture> guis = new ArrayList<>();

	private Camera camera;
	private Light sun;

	private final Game game;
	private static final List<JSONTextComponent> intro = new ArrayList<>();
	private static final List<JSONTextComponent> sub_intro = new ArrayList<>();

	public MenuScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
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
	public void update(float deltaTime) {
		camera.update();
	}

	@Override
	public void gui(boolean debugAllowed) {
		ImGui.setNextWindowSize(500, 440);
		ImGui.begin("Menu");
		if(ImGui.button("New World")){

			DelayedTask task = () -> {
				MenuScene scene = this;
				Game.queue.add(() -> Game.setCurrentScene(new MainScene(
						scene,
						game,
						modelLoader,
						masterRenderer,
						guiRenderer
				)));
			};
			Game.addTask(task);
		}
		if(ImGui.button("Single Player")){
			ImGuiFileDialog.openModal("browse-save", "Choose World", "Json File (*.hsjs){.hsjs}", ".", callback, 150, 1, 1, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType);
		}
		ImGui.button("Multiplayer");
		ImGui.button("Options");
		ImGui.end();

		if (ImGuiFileDialog.display("browse-save", ImGuiFileDialogFlags.None, 150, 400, 800, 600)) {
			if (ImGuiFileDialog.isOk()) {
				Map<String, String> selection = ImGuiFileDialog.getSelection();
				long userData = ImGuiFileDialog.getUserDatas();
				if(userData == 1) {
					MainScene world = new MainScene();
					world.setModelLoader(modelLoader);
					world.setMasterRenderer(masterRenderer);
					world.setGuiRenderer(guiRenderer);
					world.setGame(game);
					world.setPrevScene(this);
					DelayedTask task = () -> Game.queue.add(() -> {
						Game.setCurrentScene(world);
						load(world, selection.values().stream().findFirst().get());
					});
					Game.addTask(task);
				}
			}
			ImGuiFileDialog.close();
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
