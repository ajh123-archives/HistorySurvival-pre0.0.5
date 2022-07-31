package net.ddns.minersonline.HistorySurvival.api.util;

import imgui.ImVec2;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Graph {
	public int nextNodeId = 1;
	public int nextLinkId = 1;
	public int nextPinId = 100;

	public final Map<Integer, Node> nodes = new HashMap<>();
	public final List<Link> links = new ArrayList<>();

	public Graph() {}

	public Node createGraphNode() {
		final Node node = new Node(nextNodeId++, "Hello");
		Pin out = new Pin(nextPinId++, PinType.Flow, PinKind.Output);
		out.setNode(node);
		Pin in = new Pin(nextPinId++, PinType.Flow, PinKind.Input);
		in.setNode(node);
		node.pins.add(out);
		node.pins.add(in);

		this.nodes.put(node.nodeId, node);
		return node;
	}

	public Pin findByInput(final long inputPinId) {
		for (Node node : nodes.values()) {
			for (Pin pin : node.pins) {
				if (pin.kind == PinKind.Input && pin.pinId == inputPinId) {
					return pin;
				}
			}
		}
		return null;
	}

	public Pin findByOutput(final long outputPinId) {
		for (Node node : nodes.values()) {
			for (Pin pin : node.pins) {
				if (pin.kind == PinKind.Output && pin.pinId == outputPinId) {
					return pin;
				}
			}
		}
		return null;
	}

	public void linkPins(Pin start, Pin end){
		if (start.node != end.node) {
			Link link = new Link(nextLinkId, start.pinId, end.pinId);
			nextLinkId++;
			links.add(link);
		}
	}

	public static final class Node {
		public final int nodeId;
		public final String name;
		public List<Pin> pins = new ArrayList<>();
		public Vector3f color = new Vector3f(0, 0, 0);
		public ImVec2 size;

		public Node(int nodeId, String name) {
			this.nodeId = nodeId;
			this.name = name;
		}

		public Vector3f getColor() {
			return color;
		}

		public void setColor(Vector3f color) {
			this.color = color;
		}

		public ImVec2 getSize() {
			return size;
		}

		public void setSize(ImVec2 size) {
			this.size = size;
		}
	}

	public static final class Pin {
		public final int pinId;
		public final PinType type;
		public final PinKind kind;
		public Node node;
		public Vector3f color = new Vector3f(0, 0, 0);

		public int outputNodeId = -1;

		public Pin(final int pinId, final PinType type, final PinKind kind) {
			this.pinId = pinId;
			this.type = type;
			this.kind = kind;
		}

		public Node getNode() {
			return node;
		}

		public void setNode(Node node) {
			this.node = node;
		}

		public PinType getType() {
			return type;
		}

		public PinKind getKind() {
			return kind;
		}

		public String getName() {
			return "Pin " + (char) (64 + pinId);
		}
	}

	public static final class Link{
		public final int linkId;
		public final int startPinId;
		public final int endPinId;
		public Vector3f color = new Vector3f(0, 0, 0);

		public Link(int linkId, int startPinId, int endPinId) {
			this.linkId = linkId;
			this.startPinId = startPinId;
			this.endPinId = endPinId;
		}

		public Vector3f getColor() {
			return color;
		}

		public void setColor(Vector3f color) {
			this.color = color;
		}

		public int getLinkId() {
			return linkId;
		}

		public int getStartPinId() {
			return startPinId;
		}

		public int getEndPinId() {
			return endPinId;
		}
	}

	public enum PinType
	{
		Flow,
		Bool,
		Int,
		Float,
		String,
		Object,
		Function,
		Delegate,
	}

	public enum PinKind
	{
		Output,
		Input
	}
}