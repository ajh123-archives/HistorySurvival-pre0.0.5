package net.ddns.minersonline.HistorySurvival.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import java.util.List;

public class PacketDecoder extends ReplayingDecoder<Packet> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		Packet data = new Packet();

		int length = in.readInt();
		ByteBuf buf = in.readBytes(length);
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);

		NamedTag message = new NBTDeserializer().fromBytes(bytes);
		data.setValue(message);
		data.setLength(length);
		out.add(data);
	}
}
