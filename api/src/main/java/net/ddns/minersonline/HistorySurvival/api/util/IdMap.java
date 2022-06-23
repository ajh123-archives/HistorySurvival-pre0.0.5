package net.ddns.minersonline.HistorySurvival.api.util;

import javax.annotation.Nullable;

public interface IdMap<T> extends Iterable<T> {
	int DEFAULT = -1;

	int getId(T id);

	@Nullable
	T byId(int id);

	default T byIdOrThrow(int id) {
		T t = this.byId(id);
		if (t == null) {
			throw new IllegalArgumentException("No value with id " + id);
		} else {
			return t;
		}
	}

	int size();
}