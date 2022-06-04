package net.ddns.minersonline.HistorySurvival.network.packets.auth.server;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class LoginSuccessPacket extends Packet {
	@PacketValue
	private String name = "";

	@PacketValue
	private String uuid = "";

	public LoginSuccessPacket(String name, String uuid) {
		super(Utils.GAME_ID, "loginSuccess");
		CompoundTag data = new CompoundTag();
		data.putString("name", name);
		data.putString("uuid", uuid);
		setValue(data);
		this.name = name;
		this.uuid = uuid;
	}

	private LoginSuccessPacket(){}

	public String getName() {
		return name;
	}

	public String getUuid() {
		return uuid;
	}
}
