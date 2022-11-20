package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ddns.minersonline.HistorySurvival.*;
import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.commands.ChatSystem;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.DisconnectPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.EncryptionResponsePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.HandshakePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.LoginStartPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.EncryptionRequestPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.LoginSuccessPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.client.StartPingPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.server.MessageClientPacket;
import net.ddns.minersonline.HistorySurvival.scenes.ClientScene;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import net.ddns.minersonline.HistorySurvival.scenes.SceneMetaData;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.*;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	public final String serverAddress;
	public final int serverPort;
	public int state;
	RandomString session = new RandomString();
	public final String secret = session.nextString();
	public GameProfile profile;
	public int entityId = -1;
	public ChannelHandlerContext ctx;
	public static final Map<UUID, ClientMain.PacketHandler> HANDLERS = new HashMap<>();

	public ClientHandler(String serverAddress, Integer serverPort, int state, ClientMain.PacketHandler handler) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		Utils.ENCRYPTION_MODE = Utils.EncryptionMode.NONE;
		this.state = state;
		if (handler != null) {
			HANDLERS.put(handler.getId(), handler);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(new HandshakePacket(serverAddress, serverPort, state, Utils.ENCRYPTION_MODE));
		if (state == 2) {
			ctx.writeAndFlush(new LoginStartPacket(GameSettings.username, Utils.ENCRYPTION_MODE));
		} else {
			ctx.writeAndFlush(new StartPingPacket(Utils.ENCRYPTION_MODE));
		}
		this.ctx = ctx;
	}

	@Override
	public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
		if (state == 2 || state == 3) {
			Utils.ENCRYPTION_MODE = Utils.EncryptionMode.NONE;
			DelayedTask task = () -> Game.queue.add(() -> {
				MenuScene.THROWN = true;
				MenuScene.ERROR = new ConnectException("Channel is inactive");
				MenuScene.ENABLE_ERRORS.set(true);
				Game.setCurrentScene(Game.getStartScene());
			});
			Game.addTask(task);
		}
		state = 0;
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
		Packet packet = (Packet) msg;
		if (!packet.getId().equals("alive")) {
			logger.debug("Got " + packet.getId());
		}

		if (packet.getOwner().equals(Utils.GAME_ID)) {
			if (state == 2) {
				if (packet.getId().equals("loginSuccess")) {
					LoginSuccessPacket loginSuccessPacket = Packet.cast(packet, LoginSuccessPacket.class);
					if (loginSuccessPacket != null) {
						profile = new GameProfile(loginSuccessPacket.getUuid(), loginSuccessPacket.getName());
						entityId = loginSuccessPacket.getEntityId();
						logger.info("Logged in as " + profile.getName() + ":" + profile.getRawId());
						DelayedTask task = () -> Game.queue.add(() -> {
							ctx.writeAndFlush(new AlivePacket(Utils.ENCRYPTION_MODE));
							ChatSystem.network = this;
							ChatSystem.clear();
							ClientScene scene = new ClientScene(
									Game.getStartScene(),
									Game.modelLoader,
									Game.masterRenderer,
									new SceneMetaData()
							);
							scene.start();
							Game.setCurrentScene(scene);
							state = 3;
						});
						Game.addTask(task);
					}
				}
				if (packet.getId().equals("encryptionRequest")) {
					EncryptionRequestPacket encryptionRequestPacket = Packet.cast(packet, EncryptionRequestPacket.class);
					if (encryptionRequestPacket != null) {
						try {
							PublicKey key = GenerateKeys.getPublic(encryptionRequestPacket.getPublicKey());
							String hash = BrokenHash.hash(encryptionRequestPacket.getServerId() +
									secret +
									Arrays.toString(key.getEncoded())
							);

							logger.info("Authenticating");
							String content = "  {\n" +
									"    \"accessToken\": \"" + GameSettings.accessToken + "\",\n" +
									"    \"selectedProfile\": \"" + GameSettings.uuid.replace("-", "") + "\",\n" +
									"    \"serverId\": \"" + hash + "\"\n" +
									"  }";

							HttpClient httpClient = HttpClientBuilder.create().build();
							try {
								HttpPost request = new HttpPost(Utils.JOIN_URL);
								StringEntity params = new StringEntity(content);
								request.addHeader("content-type", "application/json");
								request.setEntity(params);
								HttpResponse response = httpClient.execute(request);
								int code = response.getStatusLine().getStatusCode();
								if (code == 200) {
									logger.info("Authenticated");
									String verifyToken = encryptionRequestPacket.getVerifyToken();

									Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);
									cipher.init(Cipher.ENCRYPT_MODE, key);
									byte[] enc_secret = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));

									ctx.writeAndFlush(new EncryptionResponsePacket(
											enc_secret,
											verifyToken,
											Utils.ENCRYPTION_MODE
									));

									Utils.ENCRYPTION_MODE = Utils.EncryptionMode.CLIENT;
									Utils.ENC_PUBLIC = key;
								}

							} catch (Exception ignored) {
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (packet.getId().equals("test") || packet.getId().equals("null")) {
				logger.debug(packet.getOwner());
				logger.debug(packet.getId());
				logger.debug(packet.getData().valueToString());
			}
			if (state == 1 || state == 2 || state == 3) {
				if (packet.getId().equals("alive")) {
					ctx.writeAndFlush(new AlivePacket(Utils.ENCRYPTION_MODE));
				}
				if (packet.getId().equals("disconnect")) {
					ctx.close();
					DisconnectPacket disconnectPacket = Packet.cast(packet, DisconnectPacket.class);
					DelayedTask task = () -> Game.queue.add(() -> {
						DelayedTask task2;
						if (disconnectPacket == null) {
							task2 = () -> Game.queue.add(() -> {
								MenuScene.ERROR = new Exception("Malformed Disconnect Packet");
								Game.setCurrentScene(Game.getStartScene());
							});
						} else {
							task2 = () -> Game.queue.add(() -> {
								MenuScene.THROWN = true;
								MenuScene.ERROR = new Exception("Server closed the connection [" + disconnectPacket.getReason() + "]");
								Game.setCurrentScene(Game.getStartScene());
							});
						}
						Game.addTask(task2);
					});
					Game.addTask(task, 4);
				}
			}

			if (state == 3) {
				if (packet.getId().equals("msgClient")) {
					MessageClientPacket messageClientPacket = Packet.cast(packet, MessageClientPacket.class);
					if (messageClientPacket != null) {
						ChatSystem.addChatMessage(messageClientPacket.getText());
					}
				}
			}
		}

		for (ClientMain.PacketHandler handler : HANDLERS.values()) {
			if (handler != null) {
				handler.run(ctx, state, packet);
			}
		}
	}

	public static void addHandler(ClientMain.PacketHandler handler) {
		HANDLERS.put(handler.getId(), handler);
	}

	public static void delHandler(UUID handler) {
		HANDLERS.remove(handler);
	}
}