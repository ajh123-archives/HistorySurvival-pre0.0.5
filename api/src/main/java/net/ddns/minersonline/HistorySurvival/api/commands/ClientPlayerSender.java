package net.ddns.minersonline.HistorySurvival.api.commands;

import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;

public interface ClientPlayerSender extends CommandSender {
	@Override
	void sendMessage(JSONTextComponent message);
}
