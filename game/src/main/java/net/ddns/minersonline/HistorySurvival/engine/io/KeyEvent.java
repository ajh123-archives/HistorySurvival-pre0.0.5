package net.ddns.minersonline.HistorySurvival.engine.io;

public class KeyEvent {
	public final int key_type, type, keycode;

	public KeyEvent(int key_type, int keycode, int type) {
		this.key_type = key_type;
		this.keycode = keycode;
		this.type = type;
	}

	public String getChar() {
		if (type == 2) {
			return String.valueOf(Character.toChars(keycode));
		}
		return null;
	}
}
