package net.ddns.minersonline.HistorySurvival.api.auth;

import java.util.UUID;

public class GameProfile {
	private String id;
	private String name;

	public GameProfile(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getRawId() {
		return id;
	}

	public UUID getID(){
		String localId = id;
		if(!localId.contains("-")){
			localId = id.replaceFirst(
					"(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
					"$1-$2-$3-$4-$5"
			);
		}
		return UUID.fromString(localId);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
