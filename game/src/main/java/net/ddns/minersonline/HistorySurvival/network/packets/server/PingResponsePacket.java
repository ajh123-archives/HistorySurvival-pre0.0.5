package net.ddns.minersonline.HistorySurvival.network.packets.server;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class PingResponsePacket extends Packet {
	@PacketValue
	private String json;

	public PingResponsePacket() {
	}

	public PingResponsePacket(String json) {
		super(Utils.GAME_ID, "pingResponse");
		this.json = json;
		CompoundTag data = new CompoundTag();
		data.putString("json", json);
		setValue(data);
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
