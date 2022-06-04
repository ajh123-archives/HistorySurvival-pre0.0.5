package net.ddns.minersonline.HistorySurvival.network;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NBTSerializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Packet {
	private int length;
	private NamedTag value = null;
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
		return owner.trim();
	}

	public String getId() {
		return id.trim();
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

	public static Packet fromBytes(ByteBuf in) throws IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
		int length = in.readInt();
		ByteBuf buf = in.readBytes(length);
		byte[] bytes = new byte[buf.readableBytes()];
		byte[] new_bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		byte[] old_bytes = bytes;

		Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);

		try {
			if (Utils.ENCRYPTION_MODE != Utils.EncryptionMode.NONE) {
				if (Utils.ENCRYPTION_MODE == Utils.EncryptionMode.CLIENT) {
					cipher.init(Cipher.DECRYPT_MODE, Utils.ENC_PUBLIC);
					new_bytes = cipher.doFinal(bytes);
					bytes = new_bytes;
				}
				if (Utils.ENCRYPTION_MODE == Utils.EncryptionMode.SERVER) {
					cipher.init(Cipher.DECRYPT_MODE, Utils.ENC_PRIVATE);
					new_bytes = cipher.doFinal(bytes);
					bytes = new_bytes;
				}
			}

		} catch (BadPaddingException e){
			bytes = old_bytes;
			e.printStackTrace();
		}

		Packet packet = new Packet();
		NamedTag message = new NBTDeserializer().fromBytes(bytes);
		packet.setValue(message);
		packet.setLength(length);
		CompoundTag tag = (CompoundTag) message.getTag();
		packet.id = tag.getString("id");
		packet.owner = tag.getString("owner");
		buf.release();

 		return packet;
	}

	public void toBytes(ByteBuf out) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
		byte[] data = new NBTSerializer().toBytes(value);
		Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);

		if(Utils.ENCRYPTION_MODE != Utils.EncryptionMode.NONE){
			if(Utils.ENCRYPTION_MODE == Utils.EncryptionMode.CLIENT){
				cipher.init(Cipher.ENCRYPT_MODE, Utils.ENC_PUBLIC);
				data = cipher.doFinal(data);
			}
			if(Utils.ENCRYPTION_MODE == Utils.EncryptionMode.SERVER){
				cipher.init(Cipher.ENCRYPT_MODE, Utils.ENC_PRIVATE);
				data = cipher.doFinal(data);
			}
		}

		setLength(data.length);
		out.writeInt(getLength());
		out.writeBytes(data);
	}

	public static <T extends Packet> T cast(Packet packet, Class<T> c) {
		try {
			try {
				Constructor<T> ctor = c.getDeclaredConstructor();
				ctor.setAccessible(true);
				T castPacket = ctor.newInstance();
				ctor.setAccessible(false);

				for(Field field : castPacket.getClass().getDeclaredFields())
				{
					if (field.isAnnotationPresent(PacketValue.class))
					{
						field.setAccessible(true);
						Tag<?> data = packet.getData().get(field.getName());
						Method method;
						if(field.getType() == byte[].class) {
							method = data.getClass().getSuperclass().getDeclaredMethod("getValue");
							method.setAccessible(true);
							var value = method.invoke(data);
							field.set(castPacket, value);
						}else if(field.getType() == Integer.class || field.getType() == int.class){
							method = data.getClass().getSuperclass().getDeclaredMethod("asInt");
							method.setAccessible(true);
							var value = method.invoke(data);
							field.set(castPacket, value);
						} else {
							method = data.getClass().getDeclaredMethod("getValue");
							method.setAccessible(true);
							var value = method.invoke(data);
							field.set(castPacket, value);
						}
						method.setAccessible(false);

						field.setAccessible(false);
					}
				}
				return castPacket;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}
}
