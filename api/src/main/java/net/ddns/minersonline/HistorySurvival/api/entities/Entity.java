package net.ddns.minersonline.HistorySurvival.api.entities;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.replicable.ReplicableDataAccessor;
import net.ddns.minersonline.HistorySurvival.api.data.replicable.ReplicableData;
import net.ddns.minersonline.HistorySurvival.api.registries.Registry;
import org.joml.Vector3f;

import java.util.List;

/**
 * Represents an object that is visible in the world
 * @since 0.0.2
 */
public abstract class Entity implements CommandSender {
	private final ReplicableData entityData;
	private Vector3f position;
	private float rotationX, rotationY, rotationZ;
	private float scale;
	//private final EntityType<?> type;
	private int id;
	public boolean updateMe = false;

	/**
	 * Every entity contains an {@link EntityType}, this is used to identify what type of Entity we are.
	 * Every {@link EntityType} are registered in the {@link EntityType} class using {@link Registry}'s
	 * @since 0.0.2
	 */
	protected Entity() {//(EntityType<?> type
		this.entityData = new ReplicableData(this);
		this.position = new Vector3f(0, 0, 0);
		this.defineSyncedData();
		//this.type = type;
	}

	protected abstract void defineSyncedData();

	public final ReplicableData getEntityData() {
		return this.entityData;
	}

	public void onSyncedDataUpdated(ReplicableDataAccessor<?> accessor){}

	public final void save(ByteBuf buf){
		buf.writeInt(id);
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

	public final void load(ByteBuf buf){
		id = buf.readInt();
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

	//public EntityType<?> getType() {
	//	return type;
	//}

	public final void setId(int id) {
		this.id = id;
		updateMe = true;
	}

	public final int getId() {
		return id;
	}

	public final Vector3f getPosition() {
		return position;
	}

	public final void setPosition(Vector3f position) {
		this.position = position;
		updateMe = true;
	}

	public final float getRotationX() {
		return rotationX;
	}

	public final void setRotationX(float rotationX) {
		this.rotationX = rotationX;
		updateMe = true;
	}

	public final float getRotationY() {
		return rotationY;
	}

	public final void setRotationY(float rotationY) {
		this.rotationY = rotationY;
		updateMe = true;
	}

	public final float getRotationZ() {
		return rotationZ;
	}

	public final void setRotationZ(float rotationZ) {
		this.rotationZ = rotationZ;
		updateMe = true;
	}

	public final float getScale() {
		return scale;
	}

	public final void setScale(float scale) {
		this.scale = scale;
		updateMe = true;
	}

	public final boolean isUpdate() {
		return updateMe;
	}

	@Override
	public boolean equals(Object otherObject) {
		// check for reference equality.
		return this == otherObject;
	}
}
