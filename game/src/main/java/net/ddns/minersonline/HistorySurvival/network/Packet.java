package net.ddns.minersonline.HistorySurvival.network;

import io.netty.buffer.ByteBuf;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NBTSerializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

public class Packet {
	private int length;
	private NamedTag value;
	private String owner = "";
	private String id = "";

	public Packet(String owner, String id) {
		this.owner = owner;
		this.id = id;
	}

	public Packet() {
	}

	public Packet(Packet from) {
		this.length = from.length;
		this.value = from.value;
		this.owner = from.owner;
		this.id = from.id;
	}

	public NamedTag getRaw() {
		return value;
	}

	public CompoundTag getData() {
		return ((CompoundTag) value.getTag()).getCompoundTag("data");
	}

	public String getOwner() {
		return owner;
	}

	public String getId() {
		return id;
	}

	private void setValue(NamedTag value) {
		this.value = value;
	}

	public void setValue(CompoundTag value) {
		CompoundTag data = new CompoundTag();
		data.putString("owner", this.owner);
		data.putString("id", this.id);
		data.put("data", value);
		this.value = new NamedTag("data", data);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public static Packet fromBytes(ByteBuf in) throws IOException {
		int length = in.readInt();
		ByteBuf buf = in.readBytes(length);
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);

		Packet packet = new Packet();
		NamedTag message = new NBTDeserializer().fromBytes(bytes);
		packet.setValue(message);
		packet.setLength(length);
		CompoundTag tag = (CompoundTag) message.getTag();
		packet.id = tag.getString("id");
		packet.owner = tag.getString("owner");
 		return packet;
	}

	public void toBytes(ByteBuf out) throws IOException {
		byte[] data = new NBTSerializer().toBytes(value);
		setLength(data.length);
		out.writeInt(getLength());
		out.writeBytes(data);
	}
}
