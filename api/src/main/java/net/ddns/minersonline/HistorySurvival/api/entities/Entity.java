package net.ddns.minersonline.HistorySurvival.api.entities;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.replicable.ReplicableDataAccessor;
import net.ddns.minersonline.HistorySurvival.api.data.replicable.ReplicableData;
import org.joml.Vector3f;

import java.util.List;

public abstract class Entity implements CommandSender {
	protected final ReplicableData entityData;
	protected Vector3f position;
	protected float rotationX, rotationY, rotationZ;
	protected float scale;
	private final EntityType<?> type;

	protected Entity(EntityType<?> type) {
		this.entityData = new ReplicableData(this);
		this.position = new Vector3f(0, 0, 0);
		this.defineSyncedData();
		this.type = type;
	}

	protected abstract void defineSyncedData();

	public ReplicableData getEntityData() {
		return this.entityData;
	}

	public void onSyncedDataUpdated(ReplicableDataAccessor<?> accessor){}

	public void save(ByteBuf buf){
		buf.writeFloat(position.x);
		buf.writeFloat(position.y);
		buf.writeFloat(position.z);
		buf.writeFloat(rotationX);
		buf.writeFloat(rotationY);
		buf.writeFloat(rotationZ);
		buf.writeFloat(scale);
		this.saveAdditional(buf);

		List<ReplicableData.DataItem<?>> entityData = this.entityData.getAll();
		ReplicableData.pack(entityData, buf);
	}

	protected abstract void saveAdditional(ByteBuf buf);

	public void load(ByteBuf buf){
		position.x = buf.readFloat();
		position.y = buf.readFloat();
		position.z = buf.readFloat();
		rotationX = buf.readFloat();
		rotationY = buf.readFloat();
		rotationZ = buf.readFloat();
		scale = buf.readFloat();
		this.readAdditional(buf);
		List<ReplicableData.DataItem<?>> entityData = ReplicableData.unpack(buf);
		if (entityData != null) {
			this.entityData.assignValues(entityData);
		}
	}

	protected abstract void readAdditional(ByteBuf buf);

	public EntityType<?> getType() {
		return type;
	}
}
