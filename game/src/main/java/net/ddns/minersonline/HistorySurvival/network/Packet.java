package net.ddns.minersonline.HistorySurvival.network;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NBTSerializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Packet {
	private int length;
	private NamedTag value = null;
	private String owner = "";
	private String id = "";
	public Utils.EncryptionMode encrypt = Utils.EncryptionMode.NONE;

	public Packet(String owner, String id, Utils.EncryptionMode mode) {
		this.owner = owner;
		this.id = id;
		this.encrypt = mode;
	}

	public Packet() {
	}

	public Packet(Packet from) {
		this.length = from.length;
		this.value = from.value;
		this.owner = from.owner;
		this.id = from.id;
		this.encrypt = from.encrypt;
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

	public static Packet fromBytes(ByteBuf in, Utils.EncryptionMode mode) throws IOException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
		Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);
		byte[] bytes;
		byte[] old_bytes = new byte[0];
		int length = 0;

		try {
			if (mode!= Utils.EncryptionMode.NONE) {
				int sec_key_length = in.readInt();
				byte[] sec_bytes = new byte[sec_key_length];
				ByteBuf buf = in.readBytes(sec_key_length);
				buf.readBytes(sec_bytes);
				buf.release();

				length = in.readInt();
				ByteBuf buf2 = in.readBytes(length);
				bytes = new byte[length];
				buf2.readBytes(bytes);
				old_bytes = bytes;
				buf2.release();

				byte[] sec_bytes_dec = new byte[0];

				if (mode == Utils.EncryptionMode.CLIENT) {
					cipher.init(Cipher.DECRYPT_MODE, Utils.ENC_PUBLIC);
					sec_bytes_dec = cipher.doFinal(sec_bytes);
				}
				if (mode == Utils.EncryptionMode.SERVER) {
					cipher.init(Cipher.DECRYPT_MODE, Utils.ENC_PRIVATE);
					sec_bytes_dec = cipher.doFinal(sec_bytes);
				}

				SecretKey originalKey = new SecretKeySpec(sec_bytes_dec , 0, sec_bytes_dec.length, "AES");
				Cipher aesCipher = Cipher.getInstance("AES");
				aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
				bytes = aesCipher.doFinal(bytes);
			} else {
				length = in.readInt();
				ByteBuf buf = in.readBytes(length);
				bytes = new byte[length];
				buf.readBytes(bytes);
				old_bytes = bytes;
				buf.release();
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

 		return packet;
	}

	public void toBytes(ByteBuf out) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
		byte[] data = new NBTSerializer().toBytes(value);
		Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);

		if(encrypt != Utils.EncryptionMode.NONE){
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			generator.init(128); // The AES key size in number of bits
			SecretKey secKey = generator.generateKey();

			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
			byte[] byteCipherText = aesCipher.doFinal(data);
			byte[] secData = new byte[0];

			if(encrypt == Utils.EncryptionMode.CLIENT){
				cipher.init(Cipher.ENCRYPT_MODE, Utils.ENC_PUBLIC);
				secData = cipher.doFinal(secKey.getEncoded());
			}
			if(encrypt == Utils.EncryptionMode.SERVER){
				cipher.init(Cipher.ENCRYPT_MODE, Utils.ENC_PRIVATE);
				secData = cipher.doFinal(secKey.getEncoded());
			}
			out.writeInt(secData.length);
			out.writeBytes(secData);
			setLength(byteCipherText.length);
			out.writeInt(getLength());
			out.writeBytes(byteCipherText);
		} else {
			setLength(data.length);
			out.writeInt(getLength());
			out.writeBytes(data);
		}
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
