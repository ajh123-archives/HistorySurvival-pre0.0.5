package net.ddns.minersonline.HistorySurvival.api.data.replicable;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.api.util.IncrementalIdMap;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class ReplicableDataSerializers {
	private static final IncrementalIdMap<ReplicableDataSerializer<?>> SERIALIZERS = IncrementalIdMap.create(16);
	public static final ReplicableDataSerializer<Byte> BYTE = new ReplicableDataSerializer<>() {
		public void write(ByteBuf buf, Byte binary) {
			buf.writeByte(binary);
		}

		public Byte read(ByteBuf buf) {
			return buf.readByte();
		}

		public Byte copy(Byte binary) {
			return binary;
		}
	};
	public static final ReplicableDataSerializer<Integer> INT = new ReplicableDataSerializer<>() {
		public void write(ByteBuf buf, Integer number) {
			buf.writeInt(number);
		}

		public Integer read(ByteBuf buf) {
			return buf.readInt();
		}

		public Integer copy(Integer number) {
			return number;
		}
	};
	public static final ReplicableDataSerializer<Float> FLOAT = new ReplicableDataSerializer<>() {
		public void write(ByteBuf buf, Float number) {
			buf.writeFloat(number);
		}

		public Float read(ByteBuf buf) {
			return buf.readFloat();
		}

		public Float copy(Float number) {
			return number;
		}
	};
	public static final ReplicableDataSerializer<String> STRING = new ReplicableDataSerializer<>() {
		public void write(ByteBuf buf, String text) {
			byte[] text_bytes = text.getBytes(StandardCharsets.UTF_8);
			buf.writeInt(text_bytes.length);
			buf.writeBytes(text_bytes);
		}

		public String read(ByteBuf buf) {
			int length = buf.readInt();
			ByteBuf buf2 = buf.readBytes(length);
			byte[] bytes = new byte[length];
			buf2.readBytes(bytes);
			buf2.release();
			return new String(bytes);
		}

		public String copy(String text) {
			return text;
		}
	};

	public static void registerSerializer(ReplicableDataSerializer<?> dataSerializer) {
		if (SERIALIZERS.add(dataSerializer) >= Integer.MAX_VALUE) throw new RuntimeException("Vanilla DataSerializer ID limit exceeded");
	}

	@Nullable
	public static ReplicableDataSerializer<?> getSerializer(int id) {
		return SERIALIZERS.byId(id);
	}

	public static int getSerializedId(ReplicableDataSerializer<?> dataSerializer) {
		return SERIALIZERS.getId(dataSerializer);
	}

	private ReplicableDataSerializers() {
	}

	static {
		registerSerializer(BYTE);
		registerSerializer(INT);
		registerSerializer(FLOAT);
		registerSerializer(STRING);
	}
}
