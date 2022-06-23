package net.ddns.minersonline.HistorySurvival.api.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

public class GsonHelper {
	public static String convertToString(JsonElement jsonElement, String text) {
		if (jsonElement.isJsonPrimitive()) {
			return jsonElement.getAsString();
		} else {
			throw new JsonSyntaxException("Expected " + text + " to be a string, was " + getType(jsonElement));
		}
	}

	public static String getType(@Nullable JsonElement jsonElement) {
		String s = StringUtils.abbreviateMiddle(String.valueOf((Object)jsonElement), "...", 10);
		if (jsonElement == null) {
			return "null (missing)";
		} else if (jsonElement.isJsonNull()) {
			return "null (json)";
		} else if (jsonElement.isJsonArray()) {
			return "an array (" + s + ")";
		} else if (jsonElement.isJsonObject()) {
			return "an object (" + s + ")";
		} else {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonprimitive = jsonElement.getAsJsonPrimitive();
				if (jsonprimitive.isNumber()) {
					return "a number (" + s + ")";
				}

				if (jsonprimitive.isBoolean()) {
					return "a boolean (" + s + ")";
				}
			}

			return s;
		}
	}

}
