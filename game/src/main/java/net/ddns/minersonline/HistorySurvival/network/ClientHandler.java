package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Packet msg = new Packet();
		CompoundTag data = new CompoundTag();
		data.putString("hello", "world");
		NamedTag message = new NamedTag("data", data);
		msg.setValue(message);
		ChannelFuture future = ctx.writeAndFlush(msg);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(((Packet) msg).getValue().getTag().valueToString());
		ctx.close();
	}
}