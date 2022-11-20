package net.ddns.minersonline.HistorySurvival.network.packets;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.querz.nbt.tag.CompoundTag;

public class AlivePacket extends Packet {
	public AlivePacket(Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "alive", mode);
		setValue(new CompoundTag());
	}
}
