package net.ddns.minersonline.HistorySurvival;

import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;
import net.ddns.minersonline.HistorySurvival.scenes.MainScene;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import net.ddns.minersonline.HistorySurvival.scenes.SceneMetaData;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public abstract class Scene {
	protected boolean isRunning = false;
	protected boolean loadingAllowed = false;
	protected boolean levelLoaded = false;
	protected SceneMetaData metaData = new SceneMetaData();
	public File savePath;

	public void init(){}
	public void update(float deltaTime){}//KeyEvent keyEvent
	public void stop(){}
	public final void start(){
		for (GameObject go : metaData.gameObjects){
			go.start();
		}
		isRunning = true;
	}

	public final void addGameObject(GameObject go){
		GameObjectManager.addGameObject(go);
		if (!isRunning){
			metaData.gameObjects.add(go);
		} else {
			metaData.gameObjects.add(go);
			go.start();
		}
	}

	public final World getWorld(){
		return metaData.world;
	}
	public abstract Camera getCamera();
	public abstract TransformComponent getPlayer();

	public abstract List<GuiTexture> getGUIs();
	public abstract List<Light> getLights();
	public abstract Light getSun();

	protected transient Scene prevScene = null;

	public void gui(ImBoolean debugAllowed){}

	public static ImBoolean ENABLE_SCENE_DEBUGGER = new ImBoolean(false);
	public static ImBoolean ENABLE_DEMO = new ImBoolean(false);
	public static ImBoolean ENABLE_FILE_DEMO = new ImBoolean(false);

	private static Map<String, String> demo_selection = null;
	private static long demo_userData = 0;


	protected static ImGuiFileDialogPaneFun callback = new ImGuiFileDialogPaneFun() {
		@Override
		public void paneFun(String filter, long userDatas, boolean canContinue) {
			ImGui.text("Filter: " + filter);
			Map<String, String>  selection = ImGuiFileDialog.getSelection();
			String selected = null;
			if(selection.values().stream().findFirst().isPresent()) {
				selected = selection.values().stream().findFirst().get();
			}
			ImGui.text("Selected: "+selected);
		}
	};

	public final void renderDebug(){
		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu("File"))
			{
				if (loadingAllowed && ImGui.menuItem("Save", "Ctrl+S")) {
					//save();
					if(savePath!=null) {
						ImGuiFileDialog.openDialog("browse-save", "Save World As", "Json File (*.hsjs){.hsjs},Binary File (*.hsbs){.hsbs}", savePath.getParent(), savePath.getName(), 1, 2, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.ConfirmOverwrite);
					} else {
						ImGuiFileDialog.openDialog("browse-save", "Save World As", "Json File (*.hsjs){.hsjs},Binary File (*.hsbs){.hsbs}", ".", "", 1, 2, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.ConfirmOverwrite);
					}
				}
				if (loadingAllowed && ImGui.menuItem("Open", "Ctrl+O")) {
					//load();
					ImGuiFileDialog.openModal("browse-save", "Choose World", "Json File (*.hsjs){.hsjs}", ".", callback, 150, 1, 1, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType);
				}
				ImGui.separator();
				if (prevScene!=null && ImGui.menuItem("Exit", "Esc")) {
					DelayedTask task = () -> {
						Game.queue.add(() -> Game.setCurrentScene(prevScene));
					};
					Game.addTask(task);
				}
				if (ImGui.menuItem("Quit", "Alt+F4")) {
					GLFW.glfwSetWindowShouldClose(DisplayManager.getWindow(), true);
				}
				ImGui.endMenu();
			}
			if (ImGui.beginMenu("Debug"))
			{
				if (ImGui.menuItem("Scene Debug", null, ENABLE_SCENE_DEBUGGER)){
					ENABLE_SCENE_DEBUGGER.set(!ENABLE_SCENE_DEBUGGER.get());
				}
				if (ImGui.menuItem("Demo Window", null, ENABLE_DEMO)){
					ENABLE_DEMO.set(!ENABLE_DEMO.get());
				}
				if (ImGui.menuItem("File Demo Window", null, ENABLE_FILE_DEMO)){
					ENABLE_FILE_DEMO.set(!ENABLE_FILE_DEMO.get());
				}
				ImGui.endMenu();
			}
			ImGui.endMainMenuBar();
		}

		gui(ENABLE_SCENE_DEBUGGER);

		if(ENABLE_SCENE_DEBUGGER.get())
		ShowInspector(ENABLE_SCENE_DEBUGGER);

		if(ENABLE_DEMO.get())
		ImGui.showDemoWindow(ENABLE_DEMO);

		if(ENABLE_FILE_DEMO.get())
		showFileDemo(ENABLE_FILE_DEMO);

		if (ImGuiFileDialog.display("browse-save", ImGuiFileDialogFlags.None, 150, 400, 800, 600)) {
			if (ImGuiFileDialog.isOk()) {
				Map<String, String> selection = ImGuiFileDialog.getSelection();
				long userData = ImGuiFileDialog.getUserDatas();
				if(userData == 1) {
					load(this, selection.values().stream().findFirst().get());
				}
				if(userData == 2) {
					save(ImGuiFileDialog.getFilePathName());
				}
			}
			ImGuiFileDialog.close();
		}
	}

	int selected = 0;
	boolean serialise = false;
	void ShowInspector(ImBoolean p_open)
	{
		ImGui.setNextWindowSize(500, 440);
		if (ImGui.begin("Inspector", p_open))
		{
			// Left
			{
				ImGui.beginChild("left pane", 150, 0);
				for (GameObject object: GameObjectManager.getGameObjects())
				{
					if (ImGui.selectable("GameObject "+object.getId(), selected == object.getId())) {
						selected = object.getId();
						serialise = false;
					}
				}
				ImGui.endChild();
			}
			ImGui.sameLine();

			// Right
			{
				GameObject active = GameObjectManager.getGameObject(selected);
				if(active != null) {
					ImGui.beginGroup();
					ImGui.beginChild("item view", 0, -ImGui.getFrameHeightWithSpacing()); // Leave room for 1 line below us
					ImGui.text("GameObject: " + selected);
					ImGui.separator();
					if (ImGui.beginTabBar("##Tabs")) {
						if (ImGui.beginTabItem("Details")) {
							ImGui.text("ID: " + active.getId());
							ImGui.endTabItem();
						}
						for (Component component : active.getComponents()) {
							if (ImGui.beginTabItem(component.getClass().getSimpleName())) {
								ImGui.beginChild("GameObject: " + selected + ":" + component.getClass().getSimpleName());
								component.debug();
								ImGui.endChild();
								ImGui.endTabItem();
							}
						}
						ImGui.endTabBar();
					}
					ImGui.endChild();
					if (ImGui.checkbox("Serialise", serialise)){
						serialise = !serialise;
					}
					if (serialise) {
						String object = Game.gson.toJson(active);
						ImGui.begin("GameObject: "+selected+" serialised");
						ImGui.text(object);
						ImGui.end();
					}
					ImGui.endGroup();
				}
			}
		}
		ImGui.end();
	}

	public void load(Scene from, String path){
		try {
			String file = new String(Files.readAllBytes(Paths.get(path)));
			if (!file.equals("")){
				SceneMetaData scene = Game.gson.fromJson(file, SceneMetaData.class);

				from.metaData.world.setTerrains(scene.world.getTerrains());

				from.metaData.gameObjects.clear();
				from.metaData.world.updateWorld();
				GameObjectManager.reset();
				from.isRunning = false;
				for (GameObject go : scene.gameObjects) {
					from.addGameObject(go);
				}
				from.levelLoaded = true;
				from.start();
				savePath = new File(path);
			}
		} catch (IOException e){
			Game.logger.info("An error occurred! :(", e);
		}
	}

	public void exit(){
		metaData.gameObjects.clear();
		GameObjectManager.reset();
	}

	public void save(String path){
		try {
			FileWriter writer = new FileWriter(path);
			writer.write(Game.gson.toJson(this.metaData));
			writer.close();
		} catch (IOException e){
			Game.logger.info("An error occurred! :(", e);
		}
	}

	public void showFileDemo(ImBoolean showImGuiFileDialogDemo) {
		ImGui.setNextWindowSize(800, 200, ImGuiCond.Once);
		ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
		if (ImGui.begin("ImGuiFileDialogDemo Demo", showImGuiFileDialogDemo)) {
			ImGui.text("This a demo for ImGuiFileDialog");

			if (ImGui.button("Browse File")) {
				ImGuiFileDialog.openModal("browse-key", "Choose File", ".java", ".", callback, 250, 1, 42, ImGuiFileDialogFlags.None);
			}

			if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
				if (ImGuiFileDialog.isOk()) {
					demo_selection = ImGuiFileDialog.getSelection();
					demo_userData = ImGuiFileDialog.getUserDatas();
				}
				ImGuiFileDialog.close();
			}

			if (ImGui.button("Browse Folder")) {
				ImGuiFileDialog.openDialog("browse-folder-key", "Choose Folder", null, ".", "", 1, 7, ImGuiFileDialogFlags.None);
			}

			if (ImGuiFileDialog.display("browse-folder-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
				if (ImGuiFileDialog.isOk()) {
					demo_selection = ImGuiFileDialog.getSelection();
					demo_userData = ImGuiFileDialog.getUserDatas();
				}
				ImGuiFileDialog.close();
			}
		}

		if (demo_selection != null && !demo_selection.isEmpty()) {
			ImGui.text("Selected: " + demo_selection.values().stream().findFirst().get());
			ImGui.text("User Data: " + demo_userData);
		}

		ImGui.end();
	}

	public boolean isLevelLoaded() {
		return levelLoaded;
	}

	public void setLevelLoaded(boolean levelLoaded) {
		this.levelLoaded = levelLoaded;
	}
}
