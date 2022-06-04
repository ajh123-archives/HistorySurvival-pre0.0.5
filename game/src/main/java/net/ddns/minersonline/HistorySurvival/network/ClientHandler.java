package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ddns.minersonline.HistorySurvival.*;
import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.DisconnectPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.EncryptionResponsePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.HandshakePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.LoginStartPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.EncryptionRequestPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.LoginSuccessPacket;
import net.ddns.minersonline.HistorySurvival.scenes.ErrorScene;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

	private final String serverAddress;
	private final int serverPort;
	private int state = 0;
	RandomString session = new RandomString();
	private final String secret = session.nextString();
	private GameProfile profile;

	public ClientHandler(String serverAddress, Integer serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		Utils.ENCRYPTION_MODE = Utils.EncryptionMode.NONE;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(new HandshakePacket(serverAddress, serverPort, 2));
		ctx.writeAndFlush(new LoginStartPacket(GameSettings.username));
		state = 2;
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
		Packet packet = (Packet) msg;
		MenuScene menu = (MenuScene) Game.getStartSceneScene();

		if (packet.getOwner().equals(Utils.GAME_ID)) {
			if(state == 2) {
				if (packet.getId().equals("loginSuccess")) {
					LoginSuccessPacket loginSuccessPacket = Packet.cast(packet, LoginSuccessPacket.class);
					if(loginSuccessPacket != null){
						profile = new GameProfile(loginSuccessPacket.getUuid(), loginSuccessPacket.getName());
						logger.info("Logged in as "+profile.getName()+":"+profile.getRawId()
						);
					}
				}
				if (packet.getId().equals("encryptionRequest")) {
					EncryptionRequestPacket encryptionRequestPacket = Packet.cast(packet, EncryptionRequestPacket.class);
					if (encryptionRequestPacket != null) {
						try {
							PublicKey key = GenerateKeys.getPublic(encryptionRequestPacket.getPublicKey());
							String hash = BrokenHash.hash(encryptionRequestPacket.getServerId()+
									secret+
									Arrays.toString(key.getEncoded())
							);

							logger.info("Authenticating");
							String content = "  {\n" +
									"    \"accessToken\": \""+GameSettings.accessToken+"\",\n" +
									"    \"selectedProfile\": \""+GameSettings.uuid.replace("-", "")+"\",\n"+
									"    \"serverId\": \""+hash+"\"\n" +
									"  }";

							HttpClient httpClient = HttpClientBuilder.create().build();
							try {
								HttpPost request = new HttpPost(Utils.JOIN_URL);
								StringEntity params = new StringEntity(content);
								request.addHeader("content-type", "application/json");
								request.setEntity(params);
								HttpResponse response = httpClient.execute(request);
								int code = response.getStatusLine().getStatusCode();
								if(code == 200){
									logger.info("Authenticated");
									String verifyToken = encryptionRequestPacket.getVerifyToken();

									Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);
									cipher.init(Cipher.ENCRYPT_MODE, key);
									byte[] enc_secret = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));

									ctx.writeAndFlush(new EncryptionResponsePacket(
											enc_secret,
											verifyToken
									));

									Utils.ENCRYPTION_MODE = Utils.EncryptionMode.CLIENT;
									Utils.ENC_PUBLIC = key;
								}

							} catch (Exception ignored) {}
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
			if (packet.getId().equals("alive")) {
				ctx.writeAndFlush(new AlivePacket());
			}
			if (packet.getId().equals("disconnect")) {
				ctx.close();
				DisconnectPacket disconnectPacket = Packet.cast(packet, DisconnectPacket.class);
				DelayedTask task = () -> Game.queue.add(() -> {
					if (disconnectPacket != null) {
						menu.getGame().setCurrentScene(new ErrorScene(menu,
								disconnectPacket.getTitle(),
								disconnectPacket.getReason(),
								menu.getGame(),
								menu.getModelLoader(),
								menu.getMasterRenderer(),
								menu.getGuiRenderer()
						));
					}
				});
				menu.getGame().addTask(task, 4);
			}
		}
	}
}