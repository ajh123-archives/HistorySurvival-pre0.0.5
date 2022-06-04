package net.ddns.minersonline.HistorySurvival.network;

import io.netty.channel.*;

import java.net.SocketAddress;

public class ExceptionHandler extends ChannelDuplexHandler {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Uncaught exceptions from inbound handlers will propagate up to this handler
		System.out.println(cause.toString());
	}

	@Override
	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		ctx.connect(remoteAddress, localAddress, promise.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					// Handle connect exception here...
					Throwable failureCause = future.cause();
					System.out.println(failureCause.toString());
				}
			}
		}));
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		ctx.write(msg, promise.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					// Handle write exception here...
					Throwable failureCause = future.cause();
					System.out.println(failureCause.toString());
				}
			}
		}));
	}

	// ... override more outbound methods to handle their exceptions as well
}