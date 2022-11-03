package net.ddns.minersonline.HistorySurvival;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.flag.ImGuiCond;
import imgui.type.ImBoolean;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.engine.TextureLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelWorld;
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

import static net.ddns.minersonline.HistorySurvival.network.Utils.gson;

public abstract class Scene {
	protected boolean isRunning = false;
	protected boolean ENABLE_FILES = false;
	protected boolean levelLoaded = false;
	protected static ImVec2 barSize;
	protected static ImVec2 barPos;
	protected SceneMetaData metaData;
	public File savePath;

	public Scene() {
		metaData = new SceneMetaData();
	}

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

	public final void putGameObject(int index, GameObject go){
		GameObjectManager.putGameObject(index, go);
		if (!isRunning){
			metaData.gameObjects.add(go);
		} else {
			metaData.gameObjects.add(go);
			go.start();
		}
	}

	public final VoxelWorld getWorld(){
		return metaData.world;
	}
	public abstract Camera getCamera();
	public abstract TransformComponent getPlayer();

	public abstract List<GuiTexture> getGUIs();
	public abstract List<Light> getLights();
	public abstract Light getSun();

	protected transient Scene prevScene = null;

	public void gui(ImBoolean debugAllowed){}

	public static boolean ENABLE_SCENE_DEBUGGER = false;
	public static boolean ENABLE_DEMO = false;
	public static boolean ENABLE_FILE_DEMO = false;

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

	public void setPrevScene(Scene prevScene){
		this.prevScene = prevScene;
	}

	public final void renderDebug(){
		if (ImGui.beginMainMenuBar())
		{
			if (ImGui.beginMenu("File"))
			{
				if (ENABLE_FILES && ImGui.menuItem("Save", "Ctrl+S")) {
					//save();
					if(savePath!=null) {
						ImGuiFileDialog.openDialog("browse-save", "Save World As", "Json File (*.hsjs){.hsjs},Binary File (*.hsbs){.hsbs}", savePath.getParent(), savePath.getName(), 1, 2, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.ConfirmOverwrite);
					} else {
						ImGuiFileDialog.openDialog("browse-save", "Save World As", "Json File (*.hsjs){.hsjs},Binary File (*.hsbs){.hsbs}", ".", "", 1, 2, ImGuiFileDialogFlags.HideColumnSize | ImGuiFileDialogFlags.HideColumnType | ImGuiFileDialogFlags.ConfirmOverwrite);
					}
				}
				if (ENABLE_FILES && ImGui.menuItem("Open", "Ctrl+O")) {
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
					ENABLE_SCENE_DEBUGGER = !ENABLE_SCENE_DEBUGGER;
				}
				if (ImGui.menuItem("Demo Window", null, ENABLE_DEMO)){
					ENABLE_DEMO = !ENABLE_DEMO;
				}
				if (ImGui.menuItem("File Demo Window", null, ENABLE_FILE_DEMO)){
					ENABLE_FILE_DEMO = !ENABLE_FILE_DEMO;
				}
				ImGui.endMenu();
			}
			barSize = ImGui.getWindowSize();
			barPos = ImGui.getWindowPos();
			ImGui.endMainMenuBar();
		}

		gui(new ImBoolean(ENABLE_SCENE_DEBUGGER));

		if(ENABLE_SCENE_DEBUGGER)
		ShowInspector(new ImBoolean(ENABLE_SCENE_DEBUGGER));

		if(ENABLE_DEMO)
		ImGui.showDemoWindow(new ImBoolean(ENABLE_DEMO));

		if(ENABLE_FILE_DEMO)
		showFileDemo(new ImBoolean(ENABLE_FILE_DEMO));

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
			if (ImGui.beginTabBar("##MainTabs")) {
				if (ImGui.beginTabItem("Other")) {
					ImGui.text("Texture Atlas");
					ModelTexture textureAtlas = Game.getLoader().getTextureAtlas();
					ImGui.image(textureAtlas.getTextureId(), TextureLoader.images.size()*32, 32);
					ImGui.endTabItem();
				}

				if (ImGui.beginTabItem("GameObjects")) {
					// Left
					{
						ImGui.beginChild("left pane", 150, 0);
						for (GameObject object : GameObjectManager.getGameObjects()) {
							if (ImGui.selectable("GameObject " + object.getId(), selected == object.getId())) {
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
						if (active != null) {
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
							if (ImGui.checkbox("Serialise", serialise)) {
								serialise = !serialise;
							}
							if (serialise) {
								String object = gson.toJson(active);
								ImGui.begin("GameObject: " + selected + " serialised");
								ImGui.text(object);
								ImGui.end();
							}
							ImGui.endGroup();
						}
					}
					ImGui.endTabItem();
				}
			}
			ImGui.endTabBar();
		}
		ImGui.end();
	}

	public void load(Scene from, String path){
		try {
			String file = new String(Files.readAllBytes(Paths.get(path)));
			if (!file.equals("")){
				JsonObject jsonScene = JsonParser.parseString(file).getAsJsonObject();
				if (jsonScene.has("version")){
					String version = jsonScene.get("version").getAsString();
					if (version.equalsIgnoreCase("0.0.2") || version.equalsIgnoreCase("0.0.1")){
						DelayedTask task = () -> Game.queue.add(() -> {
							MenuScene menuScene = (MenuScene) Game.getStartSceneScene();
							menuScene.error = new Exception("Worlds from versions before 0.0.3 are incompatible.");
							MenuScene.ENABLE_ERRORS.set(true);
							Game.setCurrentScene(menuScene);
						});
						Game.addTask(task);
						return;
					}
				} else {
					DelayedTask task = () -> Game.queue.add(() -> {
						MenuScene menuScene = (MenuScene) Game.getStartSceneScene();
						menuScene.error = new Exception("Worlds from versions before 0.0.3 are incompatible.");
						MenuScene.ENABLE_ERRORS.set(true);
						Game.setCurrentScene(menuScene);
					});
					Game.addTask(task);
					return;
				}

				SceneMetaData scene = gson.fromJson(file, SceneMetaData.class);

				from.metaData.world = scene.world;

				from.metaData.gameObjects.clear();
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
			writer.write(gson.toJson(this.metaData));
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

	public Scene getPrevScene() {
		return prevScene;
	}
}
