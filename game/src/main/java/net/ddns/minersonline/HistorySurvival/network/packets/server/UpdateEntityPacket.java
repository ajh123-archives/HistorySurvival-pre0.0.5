package net.ddns.minersonline.HistorySurvival.network.packets.server;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class UpdateEntityPacket extends Packet {
	@PacketValue
	private int entityId;

	@PacketValue
	private String entityData;

	public UpdateEntityPacket(GameObject entity, Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "updateEntity", mode);
		this.entityData = Utils.gson.toJson(entity);
		this.entityId = entity.getId();
		CompoundTag data = new CompoundTag();
		data.putInt("entityId", entity.getId());
		data.putString("entityData", this.entityData);
		setValue(data);
	}

	private UpdateEntityPacket(){}

	public String getEntityData() {
		return entityData;
	}

	public int getEntityId() {
		return entityId;
	}
}
