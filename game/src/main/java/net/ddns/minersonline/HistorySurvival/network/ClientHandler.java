package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ddns.minersonline.HistorySurvival.DelayedTask;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.scenes.ErrorScene;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;

public class ClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Packet msg = new AlivePacket();
		ChannelFuture future = ctx.writeAndFlush(msg);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Packet packet = (Packet) msg;
		MenuScene menu = (MenuScene) Game.getStartSceneScene();

		if (packet.getOwner().equals(Utils.GAME_ID)) {
			System.out.println(packet.getId());
			System.out.println(packet.getOwner());
			System.out.println(packet.getData());

			if (packet.getId().equals("test") || packet.getId().equals("null")) {
				System.out.println(packet.getOwner());
				System.out.println(packet.getId());
				System.out.println(packet.getData().valueToString());
			}
			if (packet.getId().equals("alive")) {
				ctx.writeAndFlush(new AlivePacket());
			}
			if (packet.getId().equals("disconnect")) {
				ctx.close();
				DelayedTask task = () -> Game.queue.add(() -> menu.getGame().setCurrentScene(new ErrorScene(menu,
						packet.getData().getString("title"),
						packet.getData().getString("reason"),
						menu.getGame(),
						menu.getModelLoader(),
						menu.getMasterRenderer(),
						menu.getGuiRenderer()
				)));
				menu.getGame().addTask(task, 4);
			}
		}
	}
}