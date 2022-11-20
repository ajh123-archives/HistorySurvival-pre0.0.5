package net.ddns.minersonline.HistorySurvival.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.AttributeKey;
import net.ddns.minersonline.HistorySurvival.api.EnvironmentType;
import net.ddns.minersonline.HistorySurvival.api.GameHook;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<Packet> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		Utils.EncryptionMode mode;
		if (GameHook.getInstance().getType() == EnvironmentType.CLIENT) {
			mode = Utils.ENCRYPTION_MODE;
		} else {
			mode = (Utils.EncryptionMode) ctx.channel().attr(AttributeKey.valueOf("encryption")).get();
		}
		try {
			Packet data = Packet.fromBytes(in, mode);
			out.add(data);
		} catch (IOException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		} //catch (BadPaddingException ignored) {}
	}
}
