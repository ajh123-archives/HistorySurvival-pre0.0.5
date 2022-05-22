package net.ddns.minersonline.HistorySurvival.network;

import net.querz.nbt.io.NamedTag;

public class Packet {
	private int length;
	private NamedTag value;

	public Packet() {
	}

	public Packet(NamedTag value) {
		this.value = value;
	}

	public NamedTag getValue() {
		return value;
	}

	public void setValue(NamedTag value) {
		this.value = value;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
