package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;

public class ClientCommandExecutor extends Component implements CommandSender {
	private final GameObject object;

	public ClientCommandExecutor(GameObject object) {
		this.object = object;
	}

	@Override
	public void sendMessage(JSONTextComponent message) {
		ChatSystem.addChatMessage(message);
	}

	@Override
	public GameObject getGameObject() {
		return object;
	}

	@Override
	public boolean hasPermission(String perm) {
		return true;
	}
}
