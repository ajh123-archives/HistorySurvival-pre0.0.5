package net.ddns.minersonline.HistorySurvival.api.commands;

import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;

public interface CommandSender {
	void sendMessage(JSONTextComponent message);
}
