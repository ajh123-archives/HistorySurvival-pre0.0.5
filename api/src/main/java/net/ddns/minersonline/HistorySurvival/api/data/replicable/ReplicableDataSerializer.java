package net.ddns.minersonline.HistorySurvival.api.data.replicable;

import io.netty.buffer.ByteBuf;

public interface ReplicableDataSerializer<T> {
	void write(ByteBuf buf, T type);

	T read(ByteBuf buf);

	default ReplicableDataAccessor<T> createAccessor(int id) {
		return new ReplicableDataAccessor<>(id, this);
	}

	T copy(T type);
}
