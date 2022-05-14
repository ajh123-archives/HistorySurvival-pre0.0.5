package net.ddns.minersonline.HistorySurvival.engine.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONSafeText {
	private final String text;

	public JSONSafeText(String text) {
		this.text = text;
	}

	public String getText() {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		return gson.toJson(this);
	}
}
