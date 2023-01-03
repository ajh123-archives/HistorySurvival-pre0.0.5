package tk.minersonline.history_survival;

import tk.minersonline.history_survival.main.ServerLauncher;

public class ServerMain {
	ServerLauncher server;
	public static void main(String[] args) {
		new ServerMain().run();
	}

	public void run() {
		server = new ServerLauncher();
		server.create();
		while (true) {
			server.render();
		}
	}
}
