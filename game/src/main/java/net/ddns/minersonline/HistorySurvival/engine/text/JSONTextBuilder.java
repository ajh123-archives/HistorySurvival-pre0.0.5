package net.ddns.minersonline.HistorySurvival.engine.text;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.minersonline.HistorySurvival.api.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.utils.StringUtils;
import org.joml.Vector2f;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class JSONTextBuilder {
	public static Collection<JSONTextComponent> build(String JSON){
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		Type collectionType = new TypeToken<Collection<JSONTextComponent>>(){}.getType();
		return gson.fromJson(JSON, collectionType);
	}

	public static GUIText build_string(String JSON, GUIText parent){
		Collection<JSONTextComponent> texts = JSONTextBuilder.build(JSON);
		GUIText lastText = parent;
		GUIText firstText = null;
		for(JSONTextComponent text : texts){
			GUIText text1 = asText(text, parent.getFont(), lastText);
			text1.setParent(lastText);
			text1.load();
			lastText = text1;
			if(firstText == null){
				firstText = text1;
				continue;
			}
			firstText.getChildren().add(text1);
		}
		if(firstText==null) return parent;
		return firstText;
	}

	public static GUIText build_string_array(List<JSONTextComponent> JSON_List, GUIText parent){
		String JSON = "[]";
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		for (JSONTextComponent message : JSON_List) {
			JSON = StringUtils.removeLastChar(JSON);
			if (!JSON.equals("[")) {
				JSON += "," + gson.toJson(message) + "\n" + "]";
			} else {
				JSON += gson.toJson(message) + "\n" + "]";
			}
		}
		return build_string(JSON, parent);
	}

	public static GUIText asText(JSONTextComponent JSON, FontType font, GUIText parent){
		GUIText text = new GUIText(JSON.getText(), parent.getFontSize(), font, new Vector2f(0, parent.getPosition().y), -1, false);
		String color_char = " ";
		color_char.toCharArray()[0] = ChatColor.COLOR_CHAR;
		if(JSON.getColor()!=null) {
			char chat_color = JSON.getColor().replaceAll(color_char, "").toCharArray()[1];
			ChatColor text_color = ChatColor.getByChar(chat_color);
			text.setColour(text_color.color.getRed() / 255f, text_color.color.getGreen() / 255f, text_color.color.getBlue() / 255f);
			text.setOutlineColor((text_color.color.getRed() / 255f) / 2, (text_color.color.getGreen() / 255f) / 2, (text_color.color.getBlue() / 255f) / 2);
		}
		text.setEndX(parent.getEndX());
		text.setEndY(parent.getEndY());
		text.setReady(true);
		return text;
	}
}
