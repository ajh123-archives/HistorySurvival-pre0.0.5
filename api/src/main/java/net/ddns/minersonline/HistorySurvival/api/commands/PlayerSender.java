package net.ddns.minersonline.HistorySurvival.api.commands;


import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

/**
 * A {@link CommandSender} used for player commands.
 */
public interface PlayerSender extends CommandSender {
	/**
	 * A function used to send a message into the chat
	 * @param message A {@link JSONTextComponent} that will be put in the chat
	 */
	@Override
	void sendMessage(JSONTextComponent message);
}
