package net.ddns.minersonline.HistorySurvival.network.packets;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.querz.nbt.tag.CompoundTag;

public class DisconnectPacket extends Packet {
	private final String reason;
	private final String title;

	public DisconnectPacket(String reason, String title) {
		super(Utils.GAME_ID, "disconnect");
		CompoundTag data = new CompoundTag();
		data.putString("reason", reason);
		data.putString("title", title);
		setValue(data);
		this.title = title;
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public String getTitle() {
		return title;
	}
}