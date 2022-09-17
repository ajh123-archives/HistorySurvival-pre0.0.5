package net.ddns.minersonline.HistorySurvival.scenes;

import imgui.ImGui;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.ddns.minersonline.HistorySurvival.*;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.network.ClientMain;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.packets.server.PingResponsePacket;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	private transient static final List<SavedServer> SAVED_SERVERS = new ArrayList<>();
	private transient static SavedServer currentServer = null;

	public transient static ImBoolean ENABLE_MULTIPLAYER = new ImBoolean(false);
	public transient static ImBoolean ENABLE_MULTIPLAYER_OPTIONS = new ImBoolean(false);
	public transient static ImBoolean ENABLE_ERRORS = new ImBoolean(false);
	public transient static boolean ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;

	private transient final ImString ip = new ImString();
	private transient final ImString name = new ImString();
	private transient final ImString port = new ImString();
	private transient int iconId = -1;
	public transient int refresh = 100;

	public transient Throwable error = null;

	public MenuScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		super();
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
	}

	@Override
	public void init() {
		iconId = Game.modelLoader.loadTexture("grass.png");

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
		if (ImGui.button("New World")){
			DelayedTask task = () -> {
				MenuScene scene = this;
				Game.queue.add(() -> Game.setCurrentScene(new MainScene(
						scene,
						modelLoader,
						masterRenderer
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

		if (ImGuiFileDialog.display("browse-save", ImGuiFileDialogFlags.None, 150, 400, 800, 600)) {
			if (ImGuiFileDialog.isOk()) {
				Map<String, String> selection = ImGuiFileDialog.getSelection();
				long userData = ImGuiFileDialog.getUserDatas();
				if (userData == 1) {
					MainScene world = new MainScene();
					world.setModelLoader(modelLoader);
					world.setMasterRenderer(masterRenderer);
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

			ImGui.beginListBox("Servers", ImGui.getContentRegionAvail().x, ImGui.getContentRegionAvail().y);
			for (int i=0; i<SAVED_SERVERS.size(); i++){
				SavedServer server = SAVED_SERVERS.get(i);

				ImGui.beginChild("Servers/list/"+i+1, ImGui.getContentRegionAvail().x, 100);
				ImGui.columns(2, "Servers/list", false);
				ImGui.setColumnWidth(ImGui.getColumnIndex(), 80);


				if (refresh == 0) {
					DelayedTask pingTask = () -> Game.queue.add(() -> {
						ClientMain client = new ClientMain(server.ip, Integer.parseInt(server.port));
						try {
							client.call(1, (ctx, state, packet) -> {
								if (state == 1){
									if (packet.getId().equals("pingResponse")) {
										PingResponsePacket pingResponsePacket = Packet.cast(packet, PingResponsePacket.class);
										if (pingResponsePacket != null) {
											server.motd = pingResponsePacket.getJson();
											logger.info(server.motd);
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
				ImGui.text("Server: "+server.name);
				ImGui.text("MOTD: "+server.motd);
				if (ImGui.button("Join Server")){
					ENABLE_ERRORS.set(false);
					DelayedTask task = () -> Game.queue.add(() -> {
						ClientMain client = new ClientMain(server.ip, Integer.parseInt(server.port));
						try {
							client.call(2, null);
						} catch (Exception e) {
							ENABLE_ERRORS.set(true);
							e.printStackTrace();
							error = e;
						}
					});
					Game.addTask(task);
				}

				ImGui.sameLine();
				if (ImGui.button("Edit Server")){
					ENABLE_MULTIPLAYER_OPTIONS.set(true);
					ENABLE_MULTIPLAYER_OPTIONS_JOIN = false;
					ip.set(server.ip);
					name.set(server.name);
					port.set(server.port);
					currentServer = server;
				}
				ImGui.sameLine();
				if (ImGui.button("Delete Server")){
					SAVED_SERVERS.remove(i);
					currentServer = null;
				}
				ImGui.endChild();
			}

			ImGui.endListBox();

			ImGui.end();
		}

		if (ENABLE_MULTIPLAYER_OPTIONS.get()){
			openEditServer(ENABLE_MULTIPLAYER_OPTIONS, ENABLE_MULTIPLAYER_OPTIONS_JOIN, currentServer);
		}

		if (ENABLE_ERRORS.get() && this.error != null){
			ImGui.setNextWindowSize(400, 200);
			ImGui.begin("An error occurred :(", ENABLE_ERRORS);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			error.printStackTrace(pw);
			String stackTrace = sw.toString();
			ImGui.textWrapped(stackTrace);
			ImGui.end();
		}
	}

	public void openEditServer(ImBoolean pOpen, boolean join, SavedServer editMe){
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
				ENABLE_ERRORS.set(false);
				DelayedTask task = () -> Game.queue.add(() -> {
					ClientMain client = new ClientMain(ip.get(), Integer.parseInt(port.get()));
					try {
						client.call(2, null);

					} catch (Exception e) {
						ENABLE_ERRORS.set(true);
						e.printStackTrace();
						error = e;
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
