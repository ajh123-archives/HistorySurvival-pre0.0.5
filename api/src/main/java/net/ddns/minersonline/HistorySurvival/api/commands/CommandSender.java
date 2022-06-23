package net.ddns.minersonline.HistorySurvival.api.commands;

import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

/**
 * An api used for commands to interface with the object that executes commands.
 */
public interface CommandSender {
	/**
	 * A function used to send a message to the implementing object
	 * @param message A {@link JSONTextComponent} that sent to the implementing object
	 */
	void sendMessage(JSONTextComponent message);
}
