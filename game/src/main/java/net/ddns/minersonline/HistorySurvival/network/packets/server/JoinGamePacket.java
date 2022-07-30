package net.ddns.minersonline.HistorySurvival.network.packets.server;

import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class JoinGamePacket extends Packet {
	@PacketValue
	private int entityId;

	public JoinGamePacket(GameObject entity) {
		super(Utils.GAME_ID, "joinGame");
		CompoundTag data = new CompoundTag();
		data.putInt("entityId", entity.getId());
		setValue(data);
		this.entityId = entity.getId();
	}

	private JoinGamePacket(){}

	public int getEntityId() {
		return entityId;
	}
}
