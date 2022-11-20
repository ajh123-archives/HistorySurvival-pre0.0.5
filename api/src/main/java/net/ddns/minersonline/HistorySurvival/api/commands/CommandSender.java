package net.ddns.minersonline.HistorySurvival.api.commands;

import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.PlayerComponent;

/**
 * An api used for commands to communicate with the object that executed the command.
 */
public interface CommandSender {
	/**
	 * A function used to send a message to the implementing object
	 * @param message A {@link JSONTextComponent} that sent to the implementing object
	 */
	void sendMessage(JSONTextComponent message);

	GameObject getGameObject();

	default GameProfile getProfile() {
		GameObject object = getGameObject();
		PlayerComponent component = object.getComponent(PlayerComponent.class);
		if (component != null) {
			return component.profile;
		}
		return null;
	}

	boolean hasPermission(String perm);
}
