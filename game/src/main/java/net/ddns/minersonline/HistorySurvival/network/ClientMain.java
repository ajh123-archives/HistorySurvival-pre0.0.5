package net.ddns.minersonline.HistorySurvival.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.ConnectException;
import java.util.UUID;

public class ClientMain {
	String host;
	int port;

	public ClientMain(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void call(int state) {
		call(state, true, null);
	}

	public UUID call(int state, boolean canThrow, @Nullable PacketHandler handler) {
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
						public void initChannel(@NotNull SocketChannel ch) {
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
				// Ignore warnings here! There can be a ConnectException!
				if (e instanceof ConnectException) {
					MenuScene.THROWN = true;
					MenuScene.ERROR = e;
					MenuScene.ENABLE_ERRORS.set(false);
					return;
				}
				if (canThrow) {
					MenuScene.THROWN = false;
					MenuScene.ERROR = e;
					MenuScene.ENABLE_ERRORS.set(true);
				}
			}
		};

		Game.executor.submit(r);
		if (handler != null) {
			return handler.getId();
		}
		return null;
	}

	public interface PacketHandler {
		void run(ChannelHandlerContext ctx, int state, Packet message);
		default UUID getId() {
			return UUID.randomUUID();
		}
	}
}