package net.ddns.minersonline.HistorySurvival.api.commands;


import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;

public abstract class ClientPlayerSender extends CommandSender {
	@Override
	public abstract void sendMessage(JSONTextComponent message);
}
