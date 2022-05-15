package net.ddns.minersonline.HistorySurvival.api.text;

public class JSONClickEvent {
	private String action;
	private String value;

	public JSONClickEvent(String action, String value) {
		this.action = action;
		this.value = value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
