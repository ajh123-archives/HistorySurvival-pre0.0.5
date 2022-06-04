package net.ddns.minersonline.HistorySurvival;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.nio.charset.StandardCharsets;

/**
 * Generates a broken Minecraft-style twos-complement signed
 * hex digest. Tested and confirmed to match vanilla.
 * @author https://gist.github.com/unascribed/70e830d471d6a3272e3f
 */
public class BrokenHash {
	public static String hash(String str) {
		try {
			byte[] digest = digest(str, "SHA-1");
			return new BigInteger(digest).toString(16);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] digest(String str, String algorithm) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
		return md.digest(strBytes);
	}
}