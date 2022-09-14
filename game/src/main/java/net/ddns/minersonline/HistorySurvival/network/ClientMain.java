package net.ddns.minersonline.HistorySurvival.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.ddns.minersonline.HistorySurvival.Game;

public class ClientMain {
	String host;
	int port;

	public ClientMain(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void call(int state, PacketHandler handler) throws Exception {
		Runnable r = () -> {
			try {
				EventLoopGroup workerGroup = new NioEventLoopGroup();

				try {
					Bootstrap b = new Bootstrap();
					b.group(workerGroup);
					b.channel(NioSocketChannel.class);
					b.option(ChannelOption.SO_KEEPALIVE, true);
					b.option(ChannelOption.TCP_NODELAY, true);
					b.option(ChannelOption.SO_RCVBUF, 4096);
					b.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024,16*1024,1024*1024));
					b.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
									new PacketEncoder(),
									new PacketDecoder(),
									new ClientHandler(host, port, state, handler),
									new InterruptingExceptionHandler()
							);
							ch.pipeline().addFirst(new OutboundExceptionRouter());
						}
					});

					ChannelFuture f = b.connect(host, port).sync();

					f.channel().closeFuture().sync();
				} finally {
					workerGroup.shutdownGracefully();
				}
			} catch (Exception e){
				Game.logger.error("An error occurred!", e);
			}
		};

		Game.executor.submit(r);
	}

	public interface PacketHandler {
		void run(ChannelHandlerContext ctx, int state, Packet message);
	}
}