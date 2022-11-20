package net.ddns.minersonline.HistorySurvival.network.packets.auth.client;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class EncryptionResponsePacket extends Packet {
	@PacketValue
	private Integer sharedSecretLength = -1;

	@PacketValue
	private byte[] sharedSecret;

	@PacketValue
	private Integer verifyTokenLength = -1;

	@PacketValue
	private String verifyToken = "";

	public EncryptionResponsePacket(byte[] sharedSecret, String verifyToken, Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "encryptionResponse", mode);

		sharedSecretLength = sharedSecret.length;
		verifyTokenLength = verifyToken.length();

		CompoundTag data = new CompoundTag();
		data.putInt("sharedSecretLength", sharedSecretLength);
		data.putByteArray("sharedSecret", sharedSecret);
		data.putInt("verifyTokenLength", verifyTokenLength);
		data.putString("verifyToken", verifyToken);
		setValue(data);

		this.sharedSecret = sharedSecret;
		this.verifyToken = verifyToken;
	}

	private EncryptionResponsePacket(){}

	public int getSharedSecretLength() {
		return sharedSecretLength;
	}

	public byte[] getSharedSecret() {
		return sharedSecret;
	}

	public int getVerifyTokenLength() {
		return verifyTokenLength;
	}

	public String getVerifyToken() {
		return verifyToken;
	}
}
