package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.network.Utils;
import org.checkerframework.checker.units.qual.C;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CrashReports {
	public static List<String> CRASH_JOKES = Arrays.asList(
		"Who did that?",
		"Boom!",
		"Again?",
		"Could you do that later?"
	);

	public static void crash(Throwable e){
		Random random = new Random();
		int joke = random.nextInt(CRASH_JOKES.size()-1);
		String crashLog = "--------------------\n";
		crashLog = crashLog + "Game " + Utils.GAME + ":" + Utils.VERSION + " has crashed!\n";
		crashLog = crashLog + CRASH_JOKES.get(joke) + "\n";
		crashLog = crashLog + "--------------------\n";

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String stackTrace = sw.toString();
		crashLog = crashLog + stackTrace + "\n";

		boolean isModded = !Utils.GAME_ID.equals("history_survival");
		String moddedMsg = "Client Modified: Probably not";
		if (isModded) {
			moddedMsg = "Client Modified: Defiantly, client's brand changed to \""+Utils.GAME_ID+"\" ";
		}

		crashLog = crashLog + "--------------------\n";
		crashLog = crashLog + "Client Version: "+Utils.VERSION+"\n";
		crashLog = crashLog + "Client Brand: "+Utils.GAME_ID+"\n";
		crashLog = crashLog + moddedMsg + "\n";
		crashLog = crashLog + "--------------------\n";

		System.err.println(crashLog);
		System.exit(-1);
	}
}
