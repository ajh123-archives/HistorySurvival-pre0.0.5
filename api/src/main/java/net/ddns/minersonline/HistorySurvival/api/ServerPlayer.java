package net.ddns.minersonline.HistorySurvival.api;

import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ddns.minersonline.HistorySurvival.api.commands.PlayerSender;
import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;

public class ServerPlayer extends PlayerSender {
	private ChannelInboundHandlerAdapter handlerAdapter;


	@Override
	public void sendMessage(JSONTextComponent message) {

	}
}
