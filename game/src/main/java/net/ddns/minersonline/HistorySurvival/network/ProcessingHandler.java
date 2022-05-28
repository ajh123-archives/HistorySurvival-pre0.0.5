package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet requestData = (Packet) msg;
		Packet responseData = new Packet(requestData);

		ChannelFuture future = ctx.writeAndFlush(responseData);
		future.addListener(ChannelFutureListener.CLOSE);
		System.out.println(requestData.getOwner());
		System.out.println(requestData.getId());
		System.out.println(requestData.getData().valueToString());
	}
}