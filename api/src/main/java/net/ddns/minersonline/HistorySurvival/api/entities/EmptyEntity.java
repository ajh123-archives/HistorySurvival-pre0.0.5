package net.ddns.minersonline.HistorySurvival.api.entities;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

public class EmptyEntity extends Entity{
	public EmptyEntity(EntityType<?> type) {
		super(type);
	}

	@Override
	public void sendMessage(JSONTextComponent message) {}

	@Override
	protected void defineSyncedData() {}

	@Override
	protected void saveAdditional(ByteBuf buf) {}

	@Override
	protected void readAdditional(ByteBuf buf) {}
}
