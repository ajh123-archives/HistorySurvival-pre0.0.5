package net.ddns.minersonline.HistorySurvival.engine.utils;

public class StringUtils {
	public static String removeLastChar(String str) {
		return removeLastChars(str, 1);
	}

	public static String removeLastChars(String str, int chars) {
		return str.substring(0, str.length() - chars);
	}
}
