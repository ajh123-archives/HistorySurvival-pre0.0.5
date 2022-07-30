package net.ddns.minersonline.HistorySurvival.scenes;

import com.sun.tools.javac.Main;
import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.ddns.minersonline.HistorySurvival.DelayedTask;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.GameSettings;
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

	public static ImBoolean ENABLE_MULTIPLAYER = new ImBoolean(false);
	public static ImBoolean ENABLE_MULTIPLAYER_OPTIONS = new ImBoolean(false);
	public static boolean ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;

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
	public void gui(ImBoolean debugAllowed) {
		ImGui.setNextWindowSize(500, 440);
		ImGui.begin("Menu");
		if (ImGui.button("New World")){
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
		if (ImGui.button("Single Player")){
			ImGuiFileDialog.openModal("browse-save", "Choose World", "Json File (*.hsjs){.hsjs}", ".", callback, 150, 1, 1, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType);
		}
		if (ImGui.button("Multiplayer")){
			ENABLE_MULTIPLAYER.set(true);
		}
		ImGui.button("Options");
		ImGui.end();

		if (ImGuiFileDialog.display("browse-save", ImGuiFileDialogFlags.None, 150, 400, 800, 600)) {
			if (ImGuiFileDialog.isOk()) {
				Map<String, String> selection = ImGuiFileDialog.getSelection();
				long userData = ImGuiFileDialog.getUserDatas();
				if (userData == 1) {
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

		if (ENABLE_MULTIPLAYER.get()) {
			ImGui.setNextWindowSize(600, 540);
			ImGui.begin("Multiplayer", ENABLE_MULTIPLAYER);
			ImGui.text("Logged in as: "+ GameSettings.username);
			ImGui.sameLine();
			ImGui.button("Change");
			if (ImGui.button("Add server")){
				ENABLE_MULTIPLAYER_OPTIONS.set(true);
				ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;
			}
			ImGui.sameLine();
			if (ImGui.button("Direct Connect")){
				ENABLE_MULTIPLAYER_OPTIONS.set(true);
				ENABLE_MULTIPLAYER_OPTIONS_JOIN = true;
			}
			ImGui.separator();

			ImGui.beginListBox("Servers", 600, ImGui.getContentRegionAvail().y);

			ImGui.beginChild(1);
			ImGui.columns(2, "Servers/list", false);
			int id = Game.modelLoader.loadTexture("grass.png");
			ImGui.setColumnWidth(ImGui.getColumnIndex(), 80);
			ImGui.image(id, 64, 64);
			ImGui.nextColumn();
			ImGui.text("Server: ");
			ImGui.text("MOTD: ");
			ImGui.button("Join Server");
			ImGui.sameLine();
			if (ImGui.button("Edit Server")){
				ENABLE_MULTIPLAYER_OPTIONS.set(true);
			}
			ImGui.sameLine();
			ImGui.button("Delete Server");
			ImGui.endChild();

			ImGui.endListBox();

			ImGui.end();
		}

		if (ENABLE_MULTIPLAYER_OPTIONS.get()){
			openEditServer(ENABLE_MULTIPLAYER_OPTIONS, ENABLE_MULTIPLAYER_OPTIONS_JOIN);
		}
	}

	public void openEditServer(ImBoolean pOpen, boolean join){
		int height = 150;
		if (join){height = 112;}
		ImGui.setNextWindowSize(400, height);
		ImGui.begin("Server", pOpen);

		if (!join) {
			ImString name = new ImString("");
			ImGui.inputText("Name", name);
			ImGui.spacing();
		}
		ImString ip = new ImString("");
		ImGui.inputText("IP", ip);
		ImGui.spacing();
		ImString port = new ImString("36676");
		ImGui.inputText("Port", port);
		ImGui.spacing();
		if (!join) {
			ImGui.button("Accept");
			ImGui.sameLine();
		} else {
			ImGui.button("Join");
			ImGui.sameLine();
		}

		if (ImGui.button("Cancel")){
			pOpen.set(false);
		}
		if (join) {
			ImGui.button("Add to saves");
		}

		ImGui.end();
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
