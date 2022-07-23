package net.ddns.minersonline.HistorySurvival;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.ddns.minersonline.HistorySurvival.engine.EntityManager;
import net.ddns.minersonline.HistorySurvival.network.GenerateKeys;
import net.ddns.minersonline.HistorySurvival.network.PacketDecoder;
import net.ddns.minersonline.HistorySurvival.network.PacketEncoder;
import net.ddns.minersonline.HistorySurvival.network.ServerHandler;
import net.ddns.minersonline.HistorySurvival.network.packets.server.UpdateEntityPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private final int port;

	private static GenerateKeys keys;

	public static PublicKey publicKey;
	public static PrivateKey privateKey;
	public static String verifyToken = "";
	public static String serverId = "";
	public static boolean running = true;
	public static ChannelGroup group;
	private static Thread logicThread = null;

	public NettyServer(int port) {
		this.port = port;
	}

	public static void update(){
		while (running) {
			if (group != null) {
//				EntityManager.getClientEntities().forEach(((id, entity) -> {
//
//					group.forEach((channel -> {
//						if ((Integer) (channel.attr(AttributeKey.valueOf("state")).get()) == 3) {
//							ByteBuf buf = channel.alloc().buffer();
//							entity.save(buf);
//							ChannelFuture future = channel.writeAndFlush(new UpdateEntityPacket(entity, buf));
//							buf.release();
//						}
//					}));
//
//					entity.updateMe = false;
//
//				}));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 36676;

		try {
			keys = new GenerateKeys(2048);
			keys.createKeys();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		publicKey = keys.getPublicKey();
		privateKey = keys.getPrivateKey();

		new NettyServer(port).run();
	}

	public void run() throws Exception {
		RandomString session = new RandomString();
		verifyToken = session.nextString();
		serverId = session.nextString();

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		group = new DefaultChannelGroup("ClientList", GlobalEventExecutor.INSTANCE);
		try {
			ServerBootstrap b = new ServerBootstrap();
			  logicThread = new Thread(() -> {
				while (running){
					try {
						update();
					} catch (Exception e){
						logger.error("An error :(", e);
					}
				}
			});
			logicThread.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				logger.info("Stopping");
				running = false;
				if (logicThread != null) {
					try {
						logicThread.join();
					} catch (InterruptedException ignored) {}
				}
				workerGroup.shutdownGracefully();
				bossGroup.shutdownGracefully();
			}));

			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
									new PacketDecoder(),
									new PacketEncoder(),
									new ServerHandler(group)
							);
						}
					}).option(ChannelOption.SO_BACKLOG, 1024)
					.childOption(ChannelOption.SO_RCVBUF, 4096)
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024,16*1024,1024*1024))
					.childOption(ChannelOption.TCP_NODELAY, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally {
			running = false;
			if (logicThread != null) {
				logicThread.join();
			}
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}