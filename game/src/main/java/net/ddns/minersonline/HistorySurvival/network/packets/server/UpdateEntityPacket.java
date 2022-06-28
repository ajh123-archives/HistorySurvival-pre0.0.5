package net.ddns.minersonline.HistorySurvival.network.packets.server;

import io.netty.buffer.ByteBuf;
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
	private byte[] entityData;

	public UpdateEntityPacket(Entity entity, ByteBuf buf) {
		super(Utils.GAME_ID, "updateEntity");
		CompoundTag data = new CompoundTag();
		data.putInt("entityId", entity.getId());
		entity.save(buf);
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		data.putByteArray("entityData", bytes);
		setValue(data);
		this.entityData = bytes;
		this.entityId = entity.getId();
	}

	private UpdateEntityPacket(){}

	public byte[] getEntityData() {
		return entityData;
	}

	public int getEntityId() {
		return entityId;
	}
}
