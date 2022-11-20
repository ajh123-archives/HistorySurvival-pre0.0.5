package net.ddns.minersonline.HistorySurvival.commands;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.server.MessageClientPacket;

public class ServerCommandExecutor extends Component implements CommandSender {
	private final ChannelHandlerContext ctx;

	public ServerCommandExecutor(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void sendMessage(JSONTextComponent message) {
		Utils.EncryptionMode encryption = (Utils.EncryptionMode) ctx.channel().attr(AttributeKey.valueOf("encryption")).get();
		ctx.writeAndFlush(new MessageClientPacket(message, encryption));
	}
}