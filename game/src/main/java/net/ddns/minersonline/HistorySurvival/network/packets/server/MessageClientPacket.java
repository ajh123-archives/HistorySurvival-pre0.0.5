package net.ddns.minersonline.HistorySurvival.network.packets.server;

import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class MessageClientPacket extends Packet {
	@PacketValue
	private String text;

	public MessageClientPacket() {
	}

	public MessageClientPacket(JSONTextComponent text, Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "msgClient", mode);
		this.text = text.toJSON();

		CompoundTag data = new CompoundTag();
		data.putString("text", this.text);
		setValue(data);
	}

	public JSONTextComponent getText() {
		return JSONTextComponent.fromJSON(this.text);
	}
}
