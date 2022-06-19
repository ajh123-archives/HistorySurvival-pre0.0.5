package net.ddns.minersonline.HistorySurvival;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import net.ddns.minersonline.HistorySurvival.network.GenerateKeys;
import net.ddns.minersonline.HistorySurvival.network.PacketDecoder;
import net.ddns.minersonline.HistorySurvival.network.PacketEncoder;
import net.ddns.minersonline.HistorySurvival.network.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class NettyServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private final int port;

	private static GenerateKeys keys;

	public static PublicKey publicKey;
	public static PrivateKey privateKey;
	public static String verifyToken = "";
	public static String serverId = "";

	public NettyServer(int port) {
		this.port = port;
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
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(
									new PacketDecoder(),
									new PacketEncoder(),
									new ServerHandler()
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
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}