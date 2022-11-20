package net.ddns.minersonline.HistorySurvival.network.packets.auth.server;

import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class LoginSuccessPacket extends Packet {
	@PacketValue
	private String name = "";

	@PacketValue
	private String uuid = "";

	@PacketValue
	private int entityId;

	public LoginSuccessPacket(GameObject entity, String name, String uuid, Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "loginSuccess", mode);
		CompoundTag data = new CompoundTag();
		data.putInt("entityId", entity.getId());
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

	public int getEntityId() {
		return entityId;
	}
}
