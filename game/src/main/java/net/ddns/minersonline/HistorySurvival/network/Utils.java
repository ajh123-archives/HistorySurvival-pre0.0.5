package net.ddns.minersonline.HistorySurvival.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.util.Defaults;
import net.ddns.minersonline.HistorySurvival.api.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.engine.worldOld.types.World;

import java.security.PrivateKey;
import java.security.PublicKey;


public class Utils {
	public static String GAME_ID = Defaults.DEFAULT_NAMESPACE;
	public static String GAME = "History Survival";
	public static String VERSION = "0.0.4";
	public static Gson gson;

	public static String URL = "https://minersonline.tk/api/";

	static {
		String auth = System.getenv("AUTH_URL");
		if(auth != null){
			URL = auth+"/";
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Component.class, new Component.JSON());
		gsonBuilder.registerTypeAdapter(GameObject.class, new GameObject.JSON());
		gsonBuilder.registerTypeAdapter(TexturedModel.class, new TexturedModel.JSON());
		gsonBuilder.registerTypeAdapter(Voxel.class, new Voxel.JSON());
		gsonBuilder.registerTypeAdapter(World.class, new World.JSON());
		gsonBuilder.registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer());
		gsonBuilder.registerTypeAdapter(ChatColor.class, new ChatColor.JSON());
		gson = gsonBuilder.create();
	}
	public static String JOIN_URL = URL+"session/hs/join";
	public static String HAS_JOINED_URL = URL+"session/hs/hasJoined";

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
