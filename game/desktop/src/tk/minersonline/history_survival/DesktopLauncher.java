package tk.minersonline.history_survival;

import net.fabricmc.loader.impl.launch.knot.KnotClient;


// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		new DesktopLauncher().run(arg);
	}

	public void run(String[] arg) {
		KnotClient.main(arg);
	}
}
