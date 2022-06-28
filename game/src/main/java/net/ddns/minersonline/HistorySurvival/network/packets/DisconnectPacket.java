package net.ddns.minersonline.HistorySurvival.network.packets;

import net.ddns.minersonline.HistorySurvival.api.entities.PlayerEntity;
import net.ddns.minersonline.HistorySurvival.engine.EntityManager;
import net.ddns.minersonline.HistorySurvival.network.Packet;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.querz.nbt.tag.CompoundTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class DisconnectPacket extends Packet {
	private static final Logger logger = LoggerFactory.getLogger(DisconnectPacket.class);

	@PacketValue
	private String reason = "";

	@PacketValue
	private String title = "";

	public DisconnectPacket(String reason, String title, @Nullable PlayerEntity entity) {
		super(Utils.GAME_ID, "disconnect");
		CompoundTag data = new CompoundTag();
		data.putString("reason", reason);
		data.putString("title", title);
		setValue(data);
		this.title = title;
		this.reason = reason;
		if(entity != null) {
			EntityManager.getEntities().remove(entity.getId());
			logger.info("Disconnected "+entity.getProfile().getName()+" with "+this.title+":"+this.reason);
		}
	}

	private DisconnectPacket(){}

	public String getReason() {
		return reason;
	}

	public String getTitle() {
		return title;
	}
}
