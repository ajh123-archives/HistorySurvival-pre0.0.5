package net.ddns.minersonline.HistorySurvival;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.ddns.minersonline.HistorySurvival.network.PacketDecoder;
import net.ddns.minersonline.HistorySurvival.network.PacketEncoder;
import net.ddns.minersonline.HistorySurvival.network.ServerHandler;

public class NettyServer {
	private final int port;

	public NettyServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {

		int port = args.length > 0 ? Integer.parseInt(args[0]) : 36676;

		new NettyServer(port).run();
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new PacketDecoder(),
									new PacketEncoder(),
									new ServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}