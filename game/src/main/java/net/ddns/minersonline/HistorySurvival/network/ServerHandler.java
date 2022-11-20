package net.ddns.minersonline.HistorySurvival.network;

import com.google.gson.Gson;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.AttributeKey;
import net.ddns.minersonline.HistorySurvival.BrokenHash;
import net.ddns.minersonline.HistorySurvival.ServerMain;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.MeshComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.PlayerComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import net.ddns.minersonline.HistorySurvival.commands.ServerCommandExecutor;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.DisconnectPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.EncryptionResponsePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.HandshakePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.LoginStartPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.EncryptionRequestPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.LoginSuccessPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.server.MessageClientPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.server.PingResponsePacket;
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

	public ServerHandler(ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		channelGroup.add(ctx.channel());
		ctx.channel().attr(AttributeKey.valueOf("state")).set(-1);
		ctx.channel().attr(AttributeKey.valueOf("userName")).set(null);
		ctx.channel().attr(AttributeKey.valueOf("object")).set(null);
		ctx.channel().attr(AttributeKey.valueOf("profile")).set(null);
		ctx.channel().attr(AttributeKey.valueOf("encryption")).set(Utils.EncryptionMode.NONE);
	}

	@Override
	public void channelInactive(@NotNull ChannelHandlerContext ctx) {
		channelGroup.remove(ctx.channel());
		GameObject go = (GameObject) ctx.channel().attr(AttributeKey.valueOf("object")).get();
		if (go != null) {
			GameObjectManager.removeGameObject(go);
		}
		ctx.channel().attr(AttributeKey.valueOf("object")).set(null);
		Object obj = ctx.channel().attr(AttributeKey.valueOf("profile")).get();
		if (obj instanceof GameProfile profile) {
			logger.info(profile.getName()+" has left the game");
		}
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg){
		Utils.EncryptionMode encryption = (Utils.EncryptionMode) ctx.channel().attr(AttributeKey.valueOf("encryption")).get();
		Packet requestData = (Packet) msg;
		logger.debug("Got "+requestData.getId());
		if (requestData.getOwner().equals(Utils.GAME_ID)) {
			if (requestData.getId().equals("handshake")) {
				Packet responseData = new Packet(requestData);
				HandshakePacket handshakePacket = Packet.cast(responseData, HandshakePacket.class);
				if (handshakePacket != null) {
					ctx.channel().attr(AttributeKey.valueOf("state")).set(handshakePacket.getNextState());
				}
			}
			if (requestData.getId().equals("test")) {
				Packet responseData = new Packet(requestData);
				ctx.writeAndFlush(responseData);
			}
			if (requestData.getId().equals("alive")) {
				ctx.writeAndFlush(new AlivePacket(encryption));
				//TODO: check frequency of alive packets then kick player if they don't send enough alive packets
			}
			if (((Integer)ctx.channel().attr(AttributeKey.valueOf("state")).get()) == 1){
				if (requestData.getId().equals("startPing")) {
					ctx.writeAndFlush(new PingResponsePacket(
						"Placing pickles on the sea floor!",
						encryption
					));
					ctx.close();
				}
			}
			if (((Integer)ctx.channel().attr(AttributeKey.valueOf("state")).get()) == 2){
				if (requestData.getId().equals("loginStart")) {
					Packet responseData = new Packet(requestData);
					LoginStartPacket loginStartPacket = Packet.cast(responseData, LoginStartPacket.class);
					if (loginStartPacket != null) {
						ctx.channel().attr(AttributeKey.valueOf("userName")).set(loginStartPacket.getName());

						ctx.writeAndFlush(new EncryptionRequestPacket(
								ServerMain.serverId,
								ServerMain.publicKey.getEncoded(),
								ServerMain.verifyToken,
								encryption
						));
					}
				}
				if (requestData.getId().equals("encryptionResponse")) {
					Packet responseData = new Packet(requestData);
					EncryptionResponsePacket encryptionResponsePacket = Packet.cast(responseData, EncryptionResponsePacket.class);
					if (encryptionResponsePacket != null) {
						if (encryptionResponsePacket.getVerifyToken().equals(ServerMain.verifyToken)) {
							HttpClient httpClient = HttpClientBuilder.create().build();
							try {
								Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);
								cipher.init(Cipher.DECRYPT_MODE, ServerMain.privateKey);
								byte[] dec_secret = cipher.doFinal(encryptionResponsePacket.getSharedSecret());
								String secret = new String(dec_secret, StandardCharsets.UTF_8);

								String hash = BrokenHash.hash(ServerMain.serverId+
										secret+
										Arrays.toString(ServerMain.publicKey.getEncoded())
								);

								String userName = (String) ctx.channel().attr(AttributeKey.valueOf("userName")).get();
								String URL = Utils.HAS_JOINED_URL+"?username="+userName+"&serverId="+hash;
								HttpGet request = new HttpGet(URL);
								HttpResponse response = httpClient.execute(request);

								String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
								if (!responseBody.equals("{}")){
									Gson gson = new Gson();
									GameProfile profile = gson.fromJson(responseBody, GameProfile.class);
									ctx.channel().attr(AttributeKey.valueOf("encryption")).set(Utils.EncryptionMode.SERVER);
									Utils.ENC_PRIVATE = ServerMain.privateKey;
									Utils.ENC_PUBLIC = ServerMain.publicKey;

									GameObject player = new GameObject();
									player.addComponent(new TransformComponent());
									player.addComponent(new MeshComponent(ModelType.PLAYER_MODEL.create()));
									player.addComponent(new PlayerComponent(new GameProfile(profile.getRawId(), profile.getName())));
									player.addComponent(new ServerCommandExecutor(ctx, player));
									GameObjectManager.addGameObject(player);
									ctx.channel().attr(AttributeKey.valueOf("object")).set(player);
									ctx.channel().attr(AttributeKey.valueOf("profile")).set(profile);

									logger.info("UUID of player "+profile.getName()+" is "+profile.getID());
									String remote = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
									int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();

									TransformComponent pos = player.getComponent(TransformComponent.class);
									if (pos != null) {
										logger.info(profile.getName()+"[/"+remote+":"+port+"] logged in with entity id "+player.getId()+" at "+pos.position);
										ctx.channel().attr(AttributeKey.valueOf("entityId")).set(player.getId());
										ctx.channel().attr(AttributeKey.valueOf("state")).set(3);
										ctx.writeAndFlush(new LoginSuccessPacket(
												player,
												profile.getName(),
												profile.getID().toString(),
												Utils.EncryptionMode.SERVER
										));
									} else {
										ctx.writeAndFlush(new DisconnectPacket(
												"Transform Component is null.",
												"Couldn't spawn player",
												null,
												encryption
										));
										ctx.close();
									}
								} else {
									ctx.writeAndFlush(new DisconnectPacket(
											"Unable to login. Try restarting your game and launcher.",
											"Authentication Error",
											null,
											encryption
									));
									ctx.close();
								}

							} catch (Exception ignored) {}
						} else {
							ctx.writeAndFlush(new DisconnectPacket(
									"Incorrect verify token",
									"Authentication Error",
									null,
									encryption
							));
							ctx.close();
						}
					} else {
						ctx.writeAndFlush(new DisconnectPacket(
								"Malformed encryption response",
								"Authentication Error",
								null,
								encryption
						));
						ctx.close();
					}
				}
			}
			if (((Integer)ctx.channel().attr(AttributeKey.valueOf("state")).get()) == 3) {
				String userName = (String) ctx.channel().attr(AttributeKey.valueOf("userName")).get();

				if (requestData.getId().equals("msgServer")) {
					GameObject obj = (GameObject) ctx.channel().attr(AttributeKey.valueOf("object")).get();
					CommandSender executor = obj.getComponent(ServerCommandExecutor.class);
					String message = requestData.getData().getString("text");
					if (message.length() > 0 && message.charAt(0) == '/') {
						String command = message.replaceFirst("/", "");
						ParseResults<CommandSender> parse = GameHook.getInstance().getDispatcher().parse(command, executor);
						try {
							GameHook.getInstance().getDispatcher().execute(parse);
						} catch (CommandSyntaxException e) {
							JSONTextComponent msg2 = new JSONTextComponent();
							msg2.setColor(ChatColor.RED);
							msg2.setText(e.getMessage() + "\n");
							ctx.writeAndFlush(new MessageClientPacket(msg2, encryption));
							JSONTextComponent msg3 = new JSONTextComponent();
							msg3.setColor(ChatColor.RED);
							msg3.setText(message + "\n");
							ctx.writeAndFlush(new MessageClientPacket(msg3, encryption));
						}
						logger.info("[Chat] " + userName + " executed command " + message);
					} else {
						logger.info("[Chat] " + "<" + userName + "> " + message);
						JSONTextComponent data = new JSONTextComponent();
						data.setText("<" + userName + "> " + message);
						channelGroup.writeAndFlush(new MessageClientPacket(data, encryption));
					}
				}
			}
		}
	}
}