package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.querz.nbt.tag.CompoundTag;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Packet msg = new Packet("history_survival", "test");
		CompoundTag data = new CompoundTag();
		data.putString("hello", "world");
		msg.setValue(data);
		ChannelFuture future = ctx.writeAndFlush(msg);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet packet = (Packet) msg;
		System.out.println(packet.getOwner());
		System.out.println(packet.getId());
		System.out.println(packet.getData().valueToString());
		ctx.close();
	}
}