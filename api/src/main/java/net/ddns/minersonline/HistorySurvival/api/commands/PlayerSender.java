package net.ddns.minersonline.HistorySurvival.api.commands;


import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;

/**
 * A {@link CommandSender} used for player commands.
 */
public abstract class PlayerSender implements CommandSender {
	/**
	 * A function used to send a message into the chat
	 * @param message A {@link JSONTextComponent} that will be put in the chat
	 */
	@Override
	public abstract void sendMessage(JSONTextComponent message);
}
