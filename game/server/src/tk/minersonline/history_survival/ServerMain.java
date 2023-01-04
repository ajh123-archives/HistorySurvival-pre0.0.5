package tk.minersonline.history_survival;

import net.fabricmc.loader.impl.launch.knot.KnotServer;
import tk.minersonline.history_survival.main.ServerStart;

public class ServerMain {
	ServerStart server;
	public static void main(String[] args) {
		KnotServer.main(args);
	}

	public void run() {
		server = new ServerStart();
		server.create();
		while (true) {
			server.render();
		}
	}
}
