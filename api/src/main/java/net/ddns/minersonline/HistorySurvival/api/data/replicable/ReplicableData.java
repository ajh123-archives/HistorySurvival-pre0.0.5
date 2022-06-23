package net.ddns.minersonline.HistorySurvival.api.data.replicable;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReplicableData {
	private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap<>();
	private static final int EOF_MARKER = 255;
	private static final int MAX_ID_VALUE = 254;
	private final Entity entity;
	private final Int2ObjectMap<DataItem<?>> itemsById = new Int2ObjectOpenHashMap<>();
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean isEmpty = true;
	private boolean isDirty;

	public ReplicableData(Entity entity) {
		this.entity = entity;
	}

	public static <T> ReplicableDataAccessor<T> defineId(Class<? extends Entity> entity, ReplicableDataSerializer<T> dataSerializer) {
		int j;
		if (ENTITY_ID_POOL.containsKey(entity)) {
			j = ENTITY_ID_POOL.getInt(entity) + 1;
		} else {
			int i = 0;
			Class<?> oclass1 = entity;

			while(oclass1 != Entity.class) {
				oclass1 = oclass1.getSuperclass();
				if (ENTITY_ID_POOL.containsKey(oclass1)) {
					i = ENTITY_ID_POOL.getInt(oclass1) + 1;
					break;
				}
			}

			j = i;
		}

		if (j > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + j + "! (Max is 254)");
		} else {
			ENTITY_ID_POOL.put(entity, j);
			return dataSerializer.createAccessor(j);
		}
	}

	public <T> void define(ReplicableDataAccessor<T> accessor, T value) {
		int i = accessor.getId();
		if (i > MAX_ID_VALUE) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is "+MAX_ID_VALUE+")");
		} else if (this.itemsById.containsKey(i)) {
			throw new IllegalArgumentException("Duplicate id value for " + i + "!");
		} else if (ReplicableDataSerializers.getSerializedId(accessor.getSerializer()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + accessor.getSerializer() + " for " + i + "!");
		} else {
			this.createDataItem(accessor, value);
		}
	}

	private <T> void createDataItem(ReplicableDataAccessor<T> accessor, T value) {
		ReplicableData.DataItem<T> dataitem = new ReplicableData.DataItem<>(accessor, value);
		this.lock.writeLock().lock();
		this.itemsById.put(accessor.getId(), dataitem);
		this.isEmpty = false;
		this.lock.writeLock().unlock();
	}

	private <T> ReplicableData.DataItem<T> getItem(ReplicableDataAccessor<T> accessor) {
		this.lock.readLock().lock();

		ReplicableData.DataItem<T> dataitem = null;
		try {
			dataitem = (ReplicableData.DataItem<T>)this.itemsById.get(accessor.getId());
		} catch (Throwable throwable) {
			Logger logger = LoggerFactory.getLogger(ReplicableData.class);
			logger.error("An error occurred!", throwable);
		} finally {
			this.lock.readLock().unlock();
		}

		return dataitem;
	}

	public <T> T get(ReplicableDataAccessor<T> accessor) {
		return this.getItem(accessor).getValue();
	}

	public <T> void set(ReplicableDataAccessor<T> accessor, T value) {
		ReplicableData.DataItem<T> dataitem = this.getItem(accessor);
		if (ObjectUtils.notEqual(value, dataitem.getValue())) {
			dataitem.setValue(value);
			this.entity.onSyncedDataUpdated(accessor);
			dataitem.setDirty(true);
			this.isDirty = true;
		}

	}

	public boolean isDirty() {
		return this.isDirty;
	}

	public static void pack(@Nullable List<ReplicableData.DataItem<?>> dataItems, ByteBuf buf) {
		if (dataItems != null) {
			for(ReplicableData.DataItem<?> dataitem : dataItems) {
				writeDataItem(buf, dataitem);
			}
		}

		buf.writeByte(EOF_MARKER);
	}

	@Nullable
	public List<ReplicableData.DataItem<?>> packDirty() {
		List<ReplicableData.DataItem<?>> list = null;
		if (this.isDirty) {
			this.lock.readLock().lock();

			for(ReplicableData.DataItem<?> dataitem : this.itemsById.values()) {
				if (dataitem.isDirty()) {
					dataitem.setDirty(false);
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(dataitem.copy());
				}
			}

			this.lock.readLock().unlock();
		}

		this.isDirty = false;
		return list;
	}

	@Nullable
	public List<ReplicableData.DataItem<?>> getAll() {
		List<ReplicableData.DataItem<?>> list = null;
		this.lock.readLock().lock();

		for(ReplicableData.DataItem<?> dataitem : this.itemsById.values()) {
			if (list == null) {
				list = Lists.newArrayList();
			}

			list.add(dataitem.copy());
		}

		this.lock.readLock().unlock();
		return list;
	}

	private static <T> void writeDataItem(ByteBuf buf, ReplicableData.DataItem<T> dataItem) {
		ReplicableDataAccessor<T> entitydataaccessor = dataItem.getAccessor();
		int i = ReplicableDataSerializers.getSerializedId(entitydataaccessor.getSerializer());
		if (i < 0) {
			throw new EncoderException("Unknown serializer type " + entitydataaccessor.getSerializer());
		} else {
			buf.writeByte(entitydataaccessor.getId());
			buf.writeInt(i);
			entitydataaccessor.getSerializer().write(buf, dataItem.getValue());
		}
	}

	@Nullable
	public static List<ReplicableData.DataItem<?>> unpack(ByteBuf buf) {
		List<ReplicableData.DataItem<?>> list = null;

		int i;
		while((i = buf.readUnsignedByte()) != EOF_MARKER) {
			if (list == null) {
				list = Lists.newArrayList();
			}

			int j = buf.readInt();
			ReplicableDataSerializer<?> replicableDataSerializer = ReplicableDataSerializers.getSerializer(j);
			if (replicableDataSerializer == null) {
				throw new DecoderException("Unknown serializer type " + j);
			}

			list.add(genericHelper(buf, i, replicableDataSerializer));
		}

		return list;
	}

	private static <T> ReplicableData.DataItem<T> genericHelper(ByteBuf buf, int id, ReplicableDataSerializer<T> replicableDataSerializer) {
		return new ReplicableData.DataItem<>(replicableDataSerializer.createAccessor(id), replicableDataSerializer.read(buf));
	}

	public void assignValues(List<ReplicableData.DataItem<?>> dataItems) {
		this.lock.writeLock().lock();

		try {
			for(ReplicableData.DataItem<?> dataitem : dataItems) {
				ReplicableData.DataItem<?> dataitem1 = this.itemsById.get(dataitem.getAccessor().getId());
				if (dataitem1 != null) {
					this.assignValue(dataitem1, dataitem);
					this.entity.onSyncedDataUpdated(dataitem.getAccessor());
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}

		this.isDirty = true;
	}

	private <T> void assignValue(ReplicableData.DataItem<T> dst, ReplicableData.DataItem<?> src) {
		if (!Objects.equals(src.accessor.getSerializer(), dst.accessor.getSerializer())) {
			throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", dst.accessor.getId(), this.entity, dst.value, dst.value.getClass(), src.value, src.value.getClass()));
		} else {
			dst.setValue((T)src.getValue());
		}
	}

	public boolean isEmpty() {
		return this.isEmpty;
	}

	public void clearDirty() {
		this.isDirty = false;
		this.lock.readLock().lock();

		for(ReplicableData.DataItem<?> dataitem : this.itemsById.values()) {
			dataitem.setDirty(false);
		}

		this.lock.readLock().unlock();
	}

	public static class DataItem<T> {
		final ReplicableDataAccessor<T> accessor;
		T value;
		private boolean dirty;

		public DataItem(ReplicableDataAccessor<T> accessor, T value) {
			this.accessor = accessor;
			this.value = value;
			this.dirty = true;
		}

		public ReplicableDataAccessor<T> getAccessor() {
			return this.accessor;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public T getValue() {
			return this.value;
		}

		public boolean isDirty() {
			return this.dirty;
		}

		public void setDirty(boolean dirty) {
			this.dirty = dirty;
		}

		public ReplicableData.DataItem<T> copy() {
			return new ReplicableData.DataItem<>(this.accessor, this.accessor.getSerializer().copy(this.value));
		}
	}
}
