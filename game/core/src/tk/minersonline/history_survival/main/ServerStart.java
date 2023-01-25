package tk.minersonline.history_survival.main;

import com.badlogic.gdx.ApplicationAdapter;
//import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryonet.Connection;
//import com.esotericsoftware.kryonet.Listener;
//import com.esotericsoftware.kryonet.Server;
//import tk.minersonline.history_survival.net.Packet;
//
//import java.io.IOException;

public class ServerStart extends ApplicationAdapter {
//	Server server;

	@Override
	public void create() {
//		try {
//			server = new Server();
//			Listener.TypeListener typeListener = new Listener.TypeListener();
//
//			typeListener.addTypeHandler(Packet.class, (con, msg) -> {
//				System.out.println(msg.message);
//				con.sendTCP(new Packet(msg.message));
//			});
//			server.addListener(typeListener);
//			Kryo kryo = server.getKryo();
//			kryo.register(Packet.class);
//
//			server.bind(36676, 36676);
//			server.run();
//			logger.info("Server stopped");
//		} catch (IOException e) {
//			logger.info("Server unable to bind port", e);
//		}
		System.exit(-1);
	}
}
