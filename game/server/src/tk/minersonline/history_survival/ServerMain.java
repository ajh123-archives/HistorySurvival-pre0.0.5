package tk.minersonline.history_survival;

import net.fabricmc.loader.impl.launch.knot.KnotServer;
import tk.minersonline.history_survival.main.Server;

public class ServerMain {
	Server server;
	public static void main(String[] args) {
		KnotServer.main(args);
	}

	public void run() {
		server = new Server();
		server.create();
		while (true) {
			server.render();
		}
	}
}
