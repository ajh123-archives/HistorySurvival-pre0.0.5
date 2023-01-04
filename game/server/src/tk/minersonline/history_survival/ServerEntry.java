package tk.minersonline.history_survival;

import tk.minersonline.history_survival.main.Server;

public class ServerEntry {
	public void create() {
		run();
	}

	public void run() {
		Server server = new Server();
		server.create();
		while (true) {
			server.render();
		}
	}
}
