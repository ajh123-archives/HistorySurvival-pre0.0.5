package net.ddns.minersonline.HistorySurvival.scenes.menus;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.ddns.minersonline.HistorySurvival.DelayedTask;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceType;
import net.ddns.minersonline.HistorySurvival.api.data.resources.types.TextureResource;
import net.ddns.minersonline.HistorySurvival.network.ClientMain;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.packets.server.PingResponsePacket;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import net.ddns.minersonline.HistorySurvival.scenes.SceneMetaData;

import java.util.ArrayList;
import java.util.List;

public class WorldMenu {
	public transient static ImBoolean ENABLE_WORLD_OPTIONS = new ImBoolean(false);

	private transient static final List<SceneMetaData> WORLDS = new ArrayList<>();
	private transient static SceneMetaData currentWorld = null;

	private transient static final ImString name = new ImString();

	private final transient static int iconId = ResourceType.VOXEL_TEXTURE.load(
			new ResourceLocation("grass"),
			TextureResource.TextureFormat.PNG
	).getTextureId();

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

			ImGui.beginListBox("Worlds", ImGui.getContentRegionAvail().x, ImGui.getContentRegionAvail().y);
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
//						ClientMain client = new ClientMain(world.ip, Integer.parseInt(world.port));
//						try {
//							client.call(2, null);
//						} catch (Exception e) {
//							MenuScene.ENABLE_ERRORS.set(true);
//							e.printStackTrace();
//							MenuScene.ERROR = e;
//						}
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
