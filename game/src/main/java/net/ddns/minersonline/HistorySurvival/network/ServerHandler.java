package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ddns.minersonline.HistorySurvival.NettyServer;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.DisconnectPacket;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(new AlivePacket());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet requestData = (Packet) msg;

		ChannelFuture future = null;
		if(requestData.getOwner().equals(Utils.GAME_ID)) {
			if(requestData.getId().equals("test")) {
				Packet responseData = new Packet(requestData);

				System.out.println(requestData.getOwner());
				System.out.println(requestData.getData().valueToString());
				future = ctx.writeAndFlush(responseData);
			}
			if(requestData.getId().equals("alive")) {
				Packet responseData = new DisconnectPacket(
						"For joining this server!",
						"You've been kicked."
				);//new AlivePacket();
				future = ctx.writeAndFlush(responseData);
			}
		}

		//ctx.writeAndFlush(responseData);
		//Thread.sleep(2);
		//ChannelFuture future = ctx.writeAndFlush(new DisconnectPacket("For joining this server!","You've been kicked."));
		if (future != null) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}