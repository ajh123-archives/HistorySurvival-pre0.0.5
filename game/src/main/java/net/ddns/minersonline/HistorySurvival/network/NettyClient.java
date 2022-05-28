package net.ddns.minersonline.HistorySurvival.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.ddns.minersonline.HistorySurvival.Scene;

import java.util.concurrent.Callable;

public class NettyClient {
	String host;
	int port;

	public NettyClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public Object call() throws Exception {
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new PacketEncoder(), new PacketDecoder(), new ClientHandler());
				}
			});

			ChannelFuture f = b.connect(host, port).sync();

			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
		return null;
	}
}