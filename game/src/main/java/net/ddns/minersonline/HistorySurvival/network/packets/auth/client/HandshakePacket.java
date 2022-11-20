package net.ddns.minersonline.HistorySurvival.network.packets.auth.client;

import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.PacketValue;
import net.querz.nbt.tag.CompoundTag;

public class HandshakePacket extends Packet {
	@PacketValue
	private String protocolVersion = Utils.VERSION;

	@PacketValue
	private String serverAddress = "";

	@PacketValue
	private Integer serverPort = -1;

	@PacketValue
	private Integer	 nextState = -1;

	public HandshakePacket(String serverAddress, Integer serverPort, Integer nextState, Utils.EncryptionMode mode) {
		super(Utils.GAME_ID, "handshake", mode);
		CompoundTag data = new CompoundTag();
		data.putString("protocolVersion", protocolVersion);
		data.putString("serverAddress", serverAddress);
		data.putInt("serverPort", serverPort);
		data.putInt("nextState", nextState);
		setValue(data);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.nextState = nextState;
	}

	private HandshakePacket(){}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public Integer getServerPort() {
		return serverPort;
	}

	public Integer getNextState() {
		return nextState;
	}
}
