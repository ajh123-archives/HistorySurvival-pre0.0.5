package net.ddns.minersonline.HistorySurvival.api.data.replicable;

public class ReplicableDataAccessor<T> {
	private final int id;
	private final ReplicableDataSerializer<T> serializer;

	public ReplicableDataAccessor(int id, ReplicableDataSerializer<T> serializer) {
		this.id = id;
		this.serializer = serializer;
	}

	public int getId() {
		return this.id;
	}

	public ReplicableDataSerializer<T> getSerializer() {
		return this.serializer;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			ReplicableDataAccessor<?> replicableDataAccessor = (ReplicableDataAccessor)object;
			return this.id == replicableDataAccessor.id;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.id;
	}

	public String toString() {
		return "<entity data: " + this.id + ">";
	}
}
