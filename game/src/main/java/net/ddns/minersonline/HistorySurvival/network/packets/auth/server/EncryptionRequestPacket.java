package net.ddns.minersonline.HistorySurvival.network.packets.auth.server;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class EncryptionRequestPacket extends Packet {
	@PacketValue
	private String serverId = "";

	@PacketValue
	private Integer publicKeyLength = -1;

	@PacketValue
	private byte[] publicKey;

	@PacketValue
	private Integer verifyTokenLength = -1;

	@PacketValue
	private String verifyToken = "";

	public EncryptionRequestPacket(String serverId, byte[] publicKey, String verifyToken) {
		super(Utils.GAME_ID, "encryptionRequest");

		publicKeyLength = publicKey.length;
		verifyTokenLength = verifyToken.length();

		CompoundTag data = new CompoundTag();
		data.putString("serverId", serverId);
		data.putInt("publicKeyLength", publicKeyLength);
		data.putByteArray("publicKey", publicKey);
		data.putInt("verifyTokenLength", verifyTokenLength);
		data.putString("verifyToken", verifyToken);
		setValue(data);

		this.serverId = serverId;
		this.publicKey = publicKey;
		this.verifyToken = verifyToken;
	}

	private EncryptionRequestPacket(){}

	public String getServerId() {
		return serverId;
	}

	public int getPublicKeyLength() {
		return publicKeyLength;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public int getVerifyTokenLength() {
		return verifyTokenLength;
	}

	public String getVerifyToken() {
		return verifyToken;
	}
}
