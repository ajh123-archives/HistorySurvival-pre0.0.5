package net.ddns.minersonline.HistorySurvival.network;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Utils {
	public static String GAME_ID = "history_survival";
	public static String GAME = "History Survival";
	public static String VERSION = "0.0.2";

	public static String JOIN_URL = "https://minersonline.ddns.net/api/session/minecraft/join";
	public static String HAS_JOINED_URL = "https://minersonline.ddns.net/api/session/minecraft/hasJoined";

	/**
	 * Encryption mode is used to identify what side of communications we are
	 *
	 * @see Utils.EncryptionMode#NONE
	 * @see Utils.EncryptionMode#CLIENT
	 * @see Utils.EncryptionMode#SERVER
	 **/
	public enum EncryptionMode {
		/** Encryption mode to identify no encryption **/
		NONE,
		/** Encryption mode to identify us as a client **/
		CLIENT,
		/** Encryption mode to identify us as a server **/
		SERVER
	}

	/**
	 * The name of the encryption algorithm, e.g.,
	 * <i>AES/CBC/PKCS5Padding</i>.
	 * See the Cipher section in the <a href=
	 *   "{@docRoot}/../specs/security/standard-names.html#cipher-algorithm-names">
	 * Java Security Standard Algorithm Names Specification</a>
	 * for information about standard algorithm names.
	 **/
	public static String ENC_ALGO = "RSA";
	public static String KEY_ENC_ALGO = "RSA";
	public static EncryptionMode ENCRYPTION_MODE;

	/** Only used in {@link Utils.EncryptionMode#SERVER} **/
	public static PrivateKey ENC_PRIVATE;

	public static PublicKey ENC_PUBLIC;
}
