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
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.network.ClientHandler;
import net.ddns.minersonline.HistorySurvival.network.ClientMain;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.packets.server.PingResponsePacket;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultiplayerMenu {
	public transient static ImBoolean ENABLE_MULTIPLAYER_OPTIONS = new ImBoolean(false);
	public transient static boolean ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;

	private transient static final List<SavedServer> SAVED_SERVERS = new ArrayList<>();
	private transient static SavedServer currentServer = null;

	private transient static final ImString ip = new ImString();
	private transient static final ImString name = new ImString();
	private transient static final ImString port = new ImString();
	private transient static UUID lastPingHandler = null;
	private final transient static int iconId = ResourceType.VOXEL_TEXTURE.load(
			new ResourceLocation("grass"),
			TextureResource.TextureFormat.PNG
	).getTextureId();
	public transient static int refresh = 100;

	public static void run(ImBoolean enable) {
		if (enable.get()) {
			ImGui.setNextWindowSize(600, 540);
			ImGui.begin("Multiplayer", enable);
			ImGui.text("Logged in as: "+ GameSettings.username);
			ImGui.sameLine();
			ImGui.button("Change");
			if (ImGui.button("Add server")){
				ENABLE_MULTIPLAYER_OPTIONS.set(true);
				ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;

				currentServer = new SavedServer();
				ip.set(currentServer.ip);
				name.set(currentServer.name);
				port.set(currentServer.port);
				SAVED_SERVERS.add(currentServer);
			}
			ImGui.sameLine();
			if (ImGui.button("Direct Connect")){
				ENABLE_MULTIPLAYER_OPTIONS.set(true);
				ENABLE_MULTIPLAYER_OPTIONS_JOIN = true;

				currentServer = new SavedServer();
				ip.set(currentServer.ip);
				name.set(currentServer.name);
				port.set(currentServer.port);
			}
			ImGui.separator();

			boolean ok = ImGui.beginListBox("Servers", ImGui.getContentRegionAvail().x, ImGui.getContentRegionAvail().y);
			if (ok) {
				for (int i = 0; i < SAVED_SERVERS.size(); i++) {
					SavedServer server = SAVED_SERVERS.get(i);

					ImGui.beginChild("Servers/list/" + i + 1, ImGui.getContentRegionAvail().x, 100);
					ImGui.columns(2, "Servers/list", false);
					ImGui.setColumnWidth(ImGui.getColumnIndex(), 80);


					if (refresh == 0) {
						DelayedTask pingTask = () -> Game.queue.add(() -> {
							ClientMain client = new ClientMain(server.ip, Integer.parseInt(server.port));
							try {
								if (lastPingHandler != null) {
									ClientHandler.delHandler(lastPingHandler);
								}
								lastPingHandler = client.call(1, false, (ctx, state, packet) -> {
									if (state == 1) {
										if (packet.getId().equals("pingResponse")) {
											PingResponsePacket pingResponsePacket = Packet.cast(packet, PingResponsePacket.class);
											if (pingResponsePacket != null) {
												server.motd = pingResponsePacket.getJson();
											}
											ctx.close();
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
						Game.addTask(pingTask);
						refresh = 300;
					}
					refresh -= 1;

					ImGui.image(iconId, 64, 64);
					ImGui.nextColumn();
					ImGui.text("Server: " + server.name);
					ImGui.text("MOTD: " + server.motd);
					if (ImGui.button("Join Server")) {
						MenuScene.ENABLE_ERRORS.set(false);
						DelayedTask task = () -> Game.queue.add(() -> {
							ClientMain client = new ClientMain(server.ip, Integer.parseInt(server.port));
							try {
								client.call(2);
								ChatSystem.network = client.getNetwork();
							} catch (Exception e) {
								MenuScene.THROWN = true;
								MenuScene.ENABLE_ERRORS.set(true);
								e.printStackTrace();
								MenuScene.ERROR = e;
								ChatSystem.network = null;
							}
						});
						Game.addTask(task);
					}

					ImGui.sameLine();
					if (ImGui.button("Edit Server")) {
						ENABLE_MULTIPLAYER_OPTIONS.set(true);
						ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;
						ip.set(server.ip);
						name.set(server.name);
						port.set(server.port);
						currentServer = server;
					}
					ImGui.sameLine();
					if (ImGui.button("Delete Server")) {
						SAVED_SERVERS.remove(i);
						currentServer = null;
					}
					ImGui.endChild();
				}

				ImGui.endListBox();
			}

			ImGui.end();
		}

		if (ENABLE_MULTIPLAYER_OPTIONS.get()){
			openEditServer(ENABLE_MULTIPLAYER_OPTIONS, ENABLE_MULTIPLAYER_OPTIONS_JOIN, currentServer);
		}
	}

	public static void openEditServer(ImBoolean pOpen, boolean join, SavedServer editMe){
		int height = 150;
		if (join){height = 120;}
		ImGui.setNextWindowSize(400, height);

		String title = "Edit Server";
		if (join){title = "Direct Connect";}

		ImGui.begin(title, pOpen);

		if (!join) {
			if (ImGui.inputText("Name", name)){editMe.name = name.get();}
			ImGui.spacing();
		}

		if (ImGui.inputText("IP", ip)){editMe.ip = ip.get();}

		ImGui.spacing();
		if (ImGui.inputText("Port", port)){editMe.port = port.get();}

		ImGui.spacing();
		if (!join) {
			if (ImGui.button("Accept")){
				pOpen.set(false);
				currentServer = null;
			}
			ImGui.sameLine();
		} else {
			if (ImGui.button("Join")){
				MenuScene.ENABLE_ERRORS.set(false);
				DelayedTask task = () -> Game.queue.add(() -> {
					ClientMain client = new ClientMain(ip.get(), Integer.parseInt(port.get()));
					try {
						client.call(2);
						ChatSystem.network = client.getNetwork();
					} catch (Exception e) {
						MenuScene.THROWN = false;
						MenuScene.ENABLE_ERRORS.set(true);
						e.printStackTrace();
						MenuScene.ERROR = e;
						ChatSystem.network = null;
					}
				});
				Game.addTask(task);
			}
			ImGui.sameLine();
		}

		if (ImGui.button("Cancel")){
			pOpen.set(false);
			currentServer = null;
		}
		if (join) {
			ImGui.sameLine();
			if (ImGui.button("Add to saves")){
				SavedServer server = new SavedServer();
				server.port = port.get();
				server.ip = ip.get();
				SAVED_SERVERS.add(server);
			}
		}

		ImGui.end();
	}
}
