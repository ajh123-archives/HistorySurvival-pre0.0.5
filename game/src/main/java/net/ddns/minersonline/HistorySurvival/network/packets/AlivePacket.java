package net.ddns.minersonline.HistorySurvival.network.packets;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.querz.nbt.tag.CompoundTag;

public class AlivePacket extends Packet {
	public AlivePacket() {
		super(Utils.GAME_ID, "alive");
		setValue(new CompoundTag());
	}
}
