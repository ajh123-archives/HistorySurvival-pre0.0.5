package net.ddns.minersonline.HistorySurvival.api.entities;

import io.netty.buffer.ByteBuf;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

public class PlayerEntity extends Entity{
	private float currentSpeed;
	private float currentTurnSpeed;
	private float upwardsSpeed;
	private boolean isJump;
	private ChatHandler messageHandler;

	protected PlayerEntity(EntityType<?> type) {
		super(type);
	}

	@Override
	protected void defineSyncedData() {}

	@Override
	protected void saveAdditional(ByteBuf buf) {
		buf.writeFloat(currentSpeed);
		buf.writeFloat(currentTurnSpeed);
		buf.writeFloat(upwardsSpeed);
		buf.writeBoolean(isJump);
	}

	@Override
	protected void readAdditional(ByteBuf buf) {
		currentSpeed = buf.readFloat();
		currentTurnSpeed = buf.readFloat();
		upwardsSpeed = buf.readFloat();
		isJump = buf.readBoolean();
	}

	@Override
	public void sendMessage(JSONTextComponent message) {
		if(this.messageHandler != null) {
			this.messageHandler.run(message);
		}
	}

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public float getCurrentTurnSpeed() {
		return currentTurnSpeed;
	}

	public void setCurrentTurnSpeed(float currentTurnSpeed) {
		this.currentTurnSpeed = currentTurnSpeed;
	}

	public float getUpwardsSpeed() {
		return upwardsSpeed;
	}

	public void setUpwardsSpeed(float upwardsSpeed) {
		this.upwardsSpeed = upwardsSpeed;
	}

	public boolean isJump() {
		return isJump;
	}

	public void setJump(boolean jump) {
		isJump = jump;
	}

	public void onChatMessage(ChatHandler handler){
		this.messageHandler = handler;
	}
	public interface ChatHandler {
		void run(JSONTextComponent message);
	}
}
