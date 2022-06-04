package net.ddns.minersonline.HistorySurvival.network.packets.auth.client;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class LoginStartPacket extends Packet {
	@PacketValue
	private String name = "";


	public LoginStartPacket(String name) {
		super(Utils.GAME_ID, "loginStart");
		CompoundTag data = new CompoundTag();
		data.putString("name", name);
		setValue(data);
		this.name = name;
	}

	private LoginStartPacket(){}

	public String getName() {
		return name;
	}
}
