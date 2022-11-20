package net.ddns.minersonline.HistorySurvival.network.packets.client;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class MessageServerPacket extends Packet {
	@PacketValue
	private String text;

	public MessageServerPacket() {
	}

	public MessageServerPacket(String text, Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "msgServer", mode);
		this.text = text;

		CompoundTag data = new CompoundTag();
		data.putString("text", text);
		setValue(data);
	}

	public String getText() {
		return text;
	}
}
