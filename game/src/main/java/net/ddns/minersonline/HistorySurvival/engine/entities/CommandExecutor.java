package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;

public class CommandExecutor extends Component implements CommandSender {
	@Override
	public void sendMessage(JSONTextComponent message) {
		ChatSystem.addChatMessage(message);
	}
}
