package net.ddns.minersonline.HistorySurvival.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.querz.nbt.io.NBTSerializer;


public class PacketEncoder extends MessageToByteEncoder<Packet> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
		byte[] data = new NBTSerializer().toBytes(msg.getValue());
		msg.setLength(data.length);
		out.writeInt(msg.getLength());
		out.writeBytes(data);
	}
}
