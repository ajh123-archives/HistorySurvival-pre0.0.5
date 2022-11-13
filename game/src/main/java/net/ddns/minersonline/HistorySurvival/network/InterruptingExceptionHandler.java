package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.ddns.minersonline.HistorySurvival.DelayedTask;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.scenes.MenuScene;

public class InterruptingExceptionHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		DelayedTask task = () -> Game.queue.add(() -> {
			Scene scene = Game.currentScene;
			MenuScene menuScene = (MenuScene) scene.getPrevScene();
			MenuScene.ERROR = cause;
			MenuScene.ENABLE_ERRORS.set(true);
			Game.setCurrentScene(scene.getPrevScene());
		});
		Game.addTask(task);
	}
}