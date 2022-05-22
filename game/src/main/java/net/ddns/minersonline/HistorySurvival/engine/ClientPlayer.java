package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.api.commands.ClientPlayerSender;
import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;

public class ClientPlayer extends ClientPlayerSender {
	Player player;

	public ClientPlayer(Player player) {
		this.player = player;
	}

	@Override
	public void sendMessage(JSONTextComponent message) {
		ChatSystem.addChatMessage(message);
	}
}
