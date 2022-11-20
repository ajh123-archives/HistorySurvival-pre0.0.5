package net.ddns.minersonline.HistorySurvival.scenes.menus;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.ddns.minersonline.HistorySurvival.DelayedTask;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.GameSettings;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import net.ddns.minersonline.HistorySurvival.api.data.resources.types.TextureResource;
import net.ddns.minersonline.HistorySurvival.scenes.MainScene;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import net.ddns.minersonline.HistorySurvival.scenes.SceneMetaData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.ddns.minersonline.HistorySurvival.network.Utils.gson;

public class WorldMenu {
	public transient static ImBoolean ENABLE_WORLD_OPTIONS = new ImBoolean(false);

	private transient static final List<SceneMetaData> WORLDS = new ArrayList<>();
	private transient static SceneMetaData currentWorld = null;

	private transient static final ImString name = new ImString();

	private final transient static int iconId = ResourceType.VOXEL_TEXTURE.load(
			new ResourceLocation("grass"),
			TextureResource.TextureFormat.PNG
	).getTextureId();

	public static void init() {
		File dir = new File(GameSettings.gameDir+"/saves/");
		WORLDS.clear();
		for (File file : Objects.requireNonNull(dir.listFiles())) {
			if (file.isDirectory()) {
				File levelFile = new File(file.getAbsolutePath()+"/level.json");
				if (levelFile.exists()) {
					try {
						String level = new String(Files.readAllBytes(levelFile.toPath()));
						SceneMetaData data = gson.fromJson(level, SceneMetaData.class);
						WORLDS.add(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void run(ImBoolean enable) {
		if (enable.get()) {
			ImGui.setNextWindowSize(600, 540);
			ImGui.begin("Single Player", enable);
			if (ImGui.button("Create new World")) {
				ENABLE_WORLD_OPTIONS.set(true);

				currentWorld = new SceneMetaData();
				name.set(currentWorld.name);
				WORLDS.add(currentWorld);
			}

			ImGui.separator();

			boolean ok = ImGui.beginListBox("Worlds", ImGui.getContentRegionAvail().x, ImGui.getContentRegionAvail().y);
			if (ok) {
				for (int i = 0; i < WORLDS.size(); i++) {
					SceneMetaData world = WORLDS.get(i);

					ImGui.beginChild("Worlds/list/" + i + 1, ImGui.getContentRegionAvail().x, 100);
					ImGui.columns(2, "Worlds/list", false);
					ImGui.setColumnWidth(ImGui.getColumnIndex(), 80);

					ImGui.image(iconId, 64, 64);
					ImGui.nextColumn();
					ImGui.text("World: " + world.name);
					if (ImGui.button("Join World")) {
						MenuScene.ENABLE_ERRORS.set(false);
						DelayedTask task = () -> Game.queue.add(() -> {
							MainScene scene = new MainScene(
									Game.getStartScene(),
									Game.modelLoader,
									Game.masterRenderer,
									world
							);
							scene.load(GameSettings.gameDir + "/saves/" + world.name);
							Game.setCurrentScene(scene);
						});
						Game.addTask(task);
					}
					ImGui.sameLine();
					if (ImGui.button("Delete World")) {
						WORLDS.remove(i);
						currentWorld = null;
					}
					ImGui.endChild();
				}

				ImGui.endListBox();
			}

			ImGui.end();
		}

		if (ENABLE_WORLD_OPTIONS.get()) {
			openEditServer(ENABLE_WORLD_OPTIONS, currentWorld);
		}
	}



	public static void openEditServer(ImBoolean pOpen, SceneMetaData editMe){
		int height = 150;
		ImGui.setNextWindowSize(400, height);

		String title = "Edit World";

		ImGui.begin(title, pOpen);

		if (ImGui.inputText("Name", name)){editMe.name = name.get();}
		ImGui.spacing();

		ImGui.spacing();
		if (ImGui.button("Accept")){
			pOpen.set(false);
			currentWorld = null;
		}
		ImGui.sameLine();

		if (ImGui.button("Cancel")){
			pOpen.set(false);
			currentWorld = null;
		}

		ImGui.end();
	}
}
