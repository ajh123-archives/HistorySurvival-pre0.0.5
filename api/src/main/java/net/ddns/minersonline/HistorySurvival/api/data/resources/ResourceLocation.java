package net.ddns.minersonline.HistorySurvival.api.data.resources;

import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.ddns.minersonline.HistorySurvival.api.exceptions.ResourceLocationException;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.api.util.Defaults;
import net.ddns.minersonline.HistorySurvival.api.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class ResourceLocation implements Comparable<ResourceLocation> {
	private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new JSONTextComponent("Invalid argument"));
	public static final char NAMESPACE_SEPARATOR = ':';
	public static final String DEFAULT_NAMESPACE = Defaults.DEFAULT_NAMESPACE;
	protected final String namespace;
	protected final String path;

	protected ResourceLocation(String[] location) {
		this.namespace = StringUtils.isEmpty(location[0]) ? DEFAULT_NAMESPACE : location[0];
		this.path = location[1];
		if (!isValidNamespace(this.namespace)) {
			throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ":" + this.path);
		} else if (!isValidPath(this.path)) {
			throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ":" + this.path);
		}
	}

	public ResourceLocation(String location) {
		this(decompose(location, NAMESPACE_SEPARATOR));
	}

	public ResourceLocation(String namespace, String id) {
		this(new String[]{namespace, id});
	}

	public static ResourceLocation of(String location, char separator) {
		return new ResourceLocation(decompose(location, separator));
	}

	@Nullable
	public static ResourceLocation tryParse(String location) {
		try {
			return new ResourceLocation(location);
		} catch (ResourceLocationException resourcelocationexception) {
			return null;
		}
	}

	protected static String[] decompose(String location, char separator) {
		String[] astring = new String[]{DEFAULT_NAMESPACE, location};
		int i = location.indexOf(separator);
		if (i >= 0) {
			astring[1] = location.substring(i + 1, location.length());
			if (i >= 1) {
				astring[0] = location.substring(0, i);
			}
		}

		return astring;
	}

	public static ResourceLocation read(String location) {
		try {
			return new ResourceLocation(location);
		} catch (ResourceLocationException resourcelocationexception) {
			throw new RuntimeException("Not a valid resource location: " + location + " " + resourcelocationexception.getMessage());
		}
	}

	public String getPath() {
		return this.path;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String toString() {
		return this.namespace + NAMESPACE_SEPARATOR + this.path;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ResourceLocation)) {
			return false;
		} else {
			ResourceLocation resourcelocation = (ResourceLocation)obj;
			return this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
		}
	}

	public int hashCode() {
		return 31 * this.namespace.hashCode() + this.path.hashCode();
	}

	public int compareTo(ResourceLocation location) {
		int i = this.path.compareTo(location.path);
		if (i == 0) {
			i = this.namespace.compareTo(location.namespace);
		}

		return i;
	}

	// Normal compare sorts by path first, this compares namespace first.
	public int compareNamespaced(ResourceLocation location) {
		int ret = this.namespace.compareTo(location.namespace);
		return ret != 0 ? ret : this.path.compareTo(location.path);
	}

	public String toDebugFileName() {
		return this.toString().replace('/', '_').replace(NAMESPACE_SEPARATOR, '_');
	}

	public static ResourceLocation read(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();

		while(reader.canRead() && isAllowedInResourceLocation(reader.peek())) {
			reader.skip();
		}

		String s = reader.getString().substring(i, reader.getCursor());

		try {
			return new ResourceLocation(s);
		} catch (ResourceLocationException resourcelocationexception) {
			reader.setCursor(i);
			throw ERROR_INVALID.createWithContext(reader);
		}
	}

	public static boolean isAllowedInResourceLocation(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == NAMESPACE_SEPARATOR || c == '/' || c == '.' || c == '-';
	}

	private static boolean isValidPath(String path) {
		for(int i = 0; i < path.length(); ++i) {
			if (!validPathChar(path.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private static boolean isValidNamespace(String namespace) {
		for(int i = 0; i < namespace.length(); ++i) {
			if (!validNamespaceChar(namespace.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public static boolean validPathChar(char c) {
		return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
	}

	private static boolean validNamespaceChar(char c) {
		return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
	}

	public static boolean isValidResourceLocation(String location) {
		String[] astring = decompose(location, ':');
		return isValidNamespace(StringUtils.isEmpty(astring[0]) ? DEFAULT_NAMESPACE : astring[0]) && isValidPath(astring[1]);
	}

	public static class Serializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
		public ResourceLocation deserialize(JsonElement element, Type type, JsonDeserializationContext p_135853_) throws JsonParseException {
			return new ResourceLocation(GsonHelper.convertToString(element, "location"));
		}

		public JsonElement serialize(ResourceLocation location, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(location.toString());
		}
	}
}
