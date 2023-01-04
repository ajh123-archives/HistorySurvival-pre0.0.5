package tk.minersonline.history_survival;

import tk.minersonline.history_survival.main.ServerStart;

public class ServerEntry {
	public void create() {
		run();
	}

	public void run() {
		ServerStart server = new ServerStart();
		server.create();
	}
}
