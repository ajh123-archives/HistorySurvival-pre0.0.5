package net.ddns.minersonline.HistorySurvival.network;

import com.google.gson.Gson;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import net.ddns.minersonline.HistorySurvival.BrokenHash;
import net.ddns.minersonline.HistorySurvival.NettyServer;
import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.DisconnectPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.EncryptionResponsePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.HandshakePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.LoginStartPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.EncryptionRequestPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.LoginSuccessPacket;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	private final ChannelGroup channelGroup;
	private Integer currentState = -1;
	private String userName = null;
	private GameProfile profile;

	public ServerHandler(ChannelGroup channelGroup) {
		Utils.ENCRYPTION_MODE = Utils.EncryptionMode.NONE;
		this.channelGroup = channelGroup;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		channelGroup.add(ctx.channel());
	}

	@Override
	public void channelInactive(@NotNull ChannelHandlerContext ctx) {
		channelGroup.remove(ctx.channel());
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg){
		Packet requestData = (Packet) msg;

		if(requestData.getOwner().equals(Utils.GAME_ID)) {
			if(requestData.getId().equals("handshake")) {
				Packet responseData = new Packet(requestData);
				HandshakePacket handshakePacket = Packet.cast(responseData, HandshakePacket.class);
				if (handshakePacket != null) {
					currentState = handshakePacket.getNextState();
				}
			}
			if(requestData.getId().equals("test")) {
				Packet responseData = new Packet(requestData);
				ctx.writeAndFlush(responseData);
			}
			if(requestData.getId().equals("alive")) {
				Packet responseData = new AlivePacket();
				ctx.writeAndFlush(responseData);
			}
			if(currentState == 2){
				if(requestData.getId().equals("loginStart")) {
					Packet responseData = new Packet(requestData);
					LoginStartPacket loginStartPacket = Packet.cast(responseData, LoginStartPacket.class);
					if (loginStartPacket != null) {
						userName = loginStartPacket.getName();

						ctx.writeAndFlush(new EncryptionRequestPacket(
								NettyServer.serverId,
								NettyServer.publicKey.getEncoded(),
								NettyServer.verifyToken
						));
					}
				}
				if (requestData.getId().equals("encryptionResponse")) {
					Packet responseData = new Packet(requestData);
					EncryptionResponsePacket encryptionResponsePacket = Packet.cast(responseData, EncryptionResponsePacket.class);
					if (encryptionResponsePacket != null) {
						if (encryptionResponsePacket.getVerifyToken().equals(NettyServer.verifyToken)) {
							HttpClient httpClient = HttpClientBuilder.create().build();
							boolean loggedIn = false;
							try {
								Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);
								cipher.init(Cipher.DECRYPT_MODE, NettyServer.privateKey);
								byte[] dec_secret = cipher.doFinal(encryptionResponsePacket.getSharedSecret());
								String secret = new String(dec_secret, StandardCharsets.UTF_8);

								String hash = BrokenHash.hash(NettyServer.serverId+
										secret+
										Arrays.toString(NettyServer.publicKey.getEncoded())
								);

								String URL = Utils.HAS_JOINED_URL+"?username="+userName+"&serverId="+hash;
								HttpGet request = new HttpGet(URL);
								HttpResponse response = httpClient.execute(request);

								String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
								if (!responseBody.equals("{}")){
									loggedIn = true;
									Gson gson = new Gson();
									profile = gson.fromJson(responseBody, GameProfile.class);
									Utils.ENCRYPTION_MODE = Utils.EncryptionMode.SERVER;
									Utils.ENC_PRIVATE = NettyServer.privateKey;
									Utils.ENC_PUBLIC = NettyServer.publicKey;

									ctx.writeAndFlush(new LoginSuccessPacket(
											profile.getName(),
											profile.getID().toString()
									));
								} else {
									ctx.writeAndFlush(new DisconnectPacket(
											"Unable to login",
											"Authentication Error"
									));
									ctx.close();
								}

							} catch (Exception ignored) {}

							if(loggedIn) {
								logger.info("UUID of player "+profile.getName()+" is "+profile.getID());
								String remote = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
								int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
								logger.info(profile.getName()+"[/"+remote+":"+port+"] logged in with entity id ? at (x, y, z)");
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								//ctx.writeAndFlush(new AlivePacket());
								logger.info(profile.getID().toString());
								ctx.writeAndFlush(new DisconnectPacket(
										profile.getName(),
										profile.getID().toString()
								));

								ctx.close();
							}
						} else {
							ctx.writeAndFlush(new DisconnectPacket(
									"Incorrect verify token",
									"Authentication Error"
							));
							ctx.close();
						}
					} else {
						ctx.writeAndFlush(new DisconnectPacket(
								"Malformed encryption response",
								"Authentication Error"
						));
						ctx.close();
					}
				}
			}
		}
	}
}