package net.ddns.minersonline.HistorySurvival.api.exceptions;

public class ResourceLocationException extends RuntimeException {
	public ResourceLocationException(String reason) {
		super(reason);
	}

	public ResourceLocationException(String reason, Throwable throwable) {
		super(reason, throwable);
	}
}
