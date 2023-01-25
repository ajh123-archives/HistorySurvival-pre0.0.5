package tk.minersonline.history_survival;

import tk.minersonline.history_survival.main.ServerStart;

public class ServerMain {
	ServerStart server;
	public static void main(String[] args) {
		new ServerMain().run();
	}

	public void run() {
		server = new ServerStart();
		server.create();
		while (true) {
			server.render();
		}
	}
}
