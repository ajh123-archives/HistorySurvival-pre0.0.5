package net.ddns.minersonline.HistorySurvival.network.packets.client;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.querz.nbt.tag.CompoundTag;

public class StartPingPacket extends Packet {
	public StartPingPacket() {
		super(Utils.GAME_ID, "startPing");
		CompoundTag data = new CompoundTag();
		data.putString("dataType", "JSON");
		setValue(data);
	}
}
