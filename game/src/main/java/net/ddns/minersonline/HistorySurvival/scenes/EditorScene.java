package net.ddns.minersonline.HistorySurvival.scenes;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imnodes.flag.ImNodesStyleVar;
import imgui.extension.nodeditor.NodeEditor;
import imgui.extension.nodeditor.NodeEditorConfig;
import imgui.extension.nodeditor.NodeEditorContext;
import imgui.extension.nodeditor.flag.NodeEditorPinKind;
import imgui.extension.nodeditor.flag.NodeEditorStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImLong;
import net.ddns.minersonline.HistorySurvival.*;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.PlayerComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.util.Graph;
import net.ddns.minersonline.HistorySurvival.api.util.ImRect;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.engine.MasterRenderer;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiRenderer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EditorScene extends Scene {
	private transient final Logger logger = LoggerFactory.getLogger(EditorScene.class);
	private transient final ModelLoader modelLoader;
	private transient final MasterRenderer masterRenderer;
	private transient final GuiRenderer guiRenderer;

	private transient final List<Light> lights = new ArrayList<>();
	private transient final List<GuiTexture> guis = new ArrayList<>();

	private transient Camera camera;
	private transient Light sun;

	private transient final Game game;
	private transient static final NodeEditorContext CONTEXT;
	private final Graph GRAPH = new Graph();

	static {
		NodeEditorConfig config = new NodeEditorConfig();
		config.setSettingsFile(null);
		CONTEXT = new NodeEditorContext(config);
	}

	public EditorScene(Game game, ModelLoader modelLoader, MasterRenderer masterRenderer, GuiRenderer guiRenderer) {
		super();
		this.masterRenderer = masterRenderer;
		this.modelLoader = modelLoader;
		this.guiRenderer = guiRenderer;
		this.game = game;
	}

	private static ImRect ImGui_GetItemRect() {
		return new ImRect(ImGui.getItemRectMin(), ImGui.getItemRectMax());
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
		show(new ImBoolean(true), GRAPH);
	}

	public static void show(final ImBoolean showImNodeEditorWindow, final Graph graph) {

		ImGuiIO io = ImGui.getIO();
		ImGui.setNextWindowSize(io.getDisplaySizeX(), io.getDisplaySizeY()-barSize.y);
		ImGui.setNextWindowPos(barPos.x, barPos.y+barSize.y);

		if (ImGui.begin("Plugin editor", showImNodeEditorWindow, ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize)) {
			if (ImGui.button("Navigate to content")) {
				NodeEditor.navigateToContent(1);
			}

			NodeEditor.setCurrentEditor(CONTEXT);
			NodeEditor.begin("Plugin Editor");

			for (Graph.Node node : graph.nodes.values()) {
				NodeEditor.beginNode(node.nodeId);

				ImGui.text(node.name);
				for (Graph.Pin pin: node.pins){
					if (pin.kind == Graph.PinKind.Input){
						NodeEditor.beginPin(pin.pinId, NodeEditorPinKind.Input);
						ImGui.text("-> In");
						NodeEditor.endPin();
					} else {
						NodeEditor.beginPin(pin.pinId, NodeEditorPinKind.Output);
						ImGui.text("Out ->");
						NodeEditor.endPin();
					}
				}

				NodeEditor.endNode();
			}

			if (NodeEditor.beginCreate()) {
				final ImLong a = new ImLong();
				final ImLong b = new ImLong();
				if (NodeEditor.queryNewLink(a, b)) {
					final Graph.Pin source = graph.findByOutput(a.get());
					final Graph.Pin target = graph.findByInput(b.get());

					if (source != null) {
						if (target != null) {
							graph.linkPins(source, target);
						}
					}
				}
			}
			NodeEditor.endCreate();

			for (Graph.Link link : graph.links) {
				Vector3f color = link.color;
				NodeEditor.link(link.linkId, link.startPinId, link.endPinId, color.x, color.y, color.z, 1,1);
			}

			NodeEditor.suspend();

			final long nodeWithContextMenu = NodeEditor.getNodeWithContextMenu();
			if (nodeWithContextMenu != -1) {
				ImGui.openPopup("node_context");
				ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), (int) nodeWithContextMenu);
			}

			if (ImGui.isPopupOpen("node_context")) {
				final int targetNode = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"));
				if (ImGui.beginPopup("node_context")) {
					if (ImGui.button("Delete " + graph.nodes.get(targetNode).name)) {
						graph.nodes.remove(targetNode);
						ImGui.closeCurrentPopup();
					}
					ImGui.endPopup();
				}
			}

			if (NodeEditor.showBackgroundContextMenu()) {
				ImGui.openPopup("node_editor_context");
			}

			if (ImGui.beginPopup("node_editor_context")) {
				if (ImGui.button("Create New Node")) {
					final Graph.Node node = graph.createGraphNode();
					final float canvasX = NodeEditor.toCanvasX(ImGui.getMousePosX());
					final float canvasY = NodeEditor.toCanvasY(ImGui.getMousePosY());
					NodeEditor.setNodePosition(node.nodeId, canvasX, canvasY);
					ImGui.closeCurrentPopup();
				}
				ImGui.endPopup();
			}

			NodeEditor.resume();
			NodeEditor.end();
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
	public TransformComponent getTransform() {
		return null;
	}

	@Override
	public PlayerComponent getPlayer() {
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
