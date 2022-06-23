package net.ddns.minersonline.HistorySurvival.api.util;

public class MathHelper {
	public static int murmurHash3Mixer(int p_14184_) {
		p_14184_ ^= p_14184_ >>> 16;
		p_14184_ *= -2048144789;
		p_14184_ ^= p_14184_ >>> 13;
		p_14184_ *= -1028477387;
		return p_14184_ ^ p_14184_ >>> 16;
	}
}
