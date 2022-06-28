package net.ddns.minersonline.HistorySurvival.engine.text;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.utils.StringUtils;
import org.joml.Vector2f;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class JSONTextBuilder {
	public static Collection<JSONTextComponent> build(String JSON){
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		Type collectionType = new TypeToken<Collection<JSONTextComponent>>(){}.getType();
		return gson.fromJson(JSON, collectionType);
	}

	public static GUIText build_string(String JSON, GUIText parent, @Nullable GUIText oldText){
		Collection<JSONTextComponent> texts = JSONTextBuilder.build(JSON);
		GUIText lastText = parent;
		GUIText firstText = null;
		if(oldText != null){
			oldText.remove();
		}
		for(JSONTextComponent text : texts){
			GUIText text1 = asText(text, parent.getFont(), lastText, oldText);
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

	public static GUIText build_string_array(List<JSONTextComponent> JSON_List, GUIText parent, @Nullable GUIText oldText){
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
		return build_string(JSON, parent, oldText);
	}

	public static GUIText asText(JSONTextComponent JSON, FontGroup font, GUIText parent, @Nullable GUIText oldText){
		if(oldText != null){
			oldText.remove();
		}

		Vector2f pos = new Vector2f(parent.getEndPos());
		GUIText text = new GUIText(JSON.getText(), parent.getFontSize(), font, pos, -1, parent.isCenterText());
		text.setParent(parent);
		text.setPosition(parent.getEndPos());

		String color_char = " ";
		color_char.toCharArray()[0] = ChatColor.COLOR_CHAR;
		if(JSON.getColor()!=null) {
			char chat_color = JSON.getColor().replaceAll(color_char, "").toCharArray()[1];
			ChatColor text_color = ChatColor.getByChar(chat_color);
			text.setColour(text_color.color.getRed() / 255f, text_color.color.getGreen() / 255f, text_color.color.getBlue() / 255f);
			text.setOutlineColor((text_color.color.getRed() / 255f) / 2, (text_color.color.getGreen() / 255f) / 2, (text_color.color.getBlue() / 255f) / 2);
		}
		if(JSON.isBold()){
			text.setSelectedFont(font.getBOLD());
			if(JSON.isItalic()){
				text.setSelectedFont(font.getBOLD_ITALIC());
				if(JSON.isUnderline()){
					text.setSelectedFont(font.getBOLD_ITALIC_UNDERLINE());
				}
			} else {
				if(JSON.isUnderline()){
					text.setSelectedFont(font.getBOLD_UNDERLINE());
				}
			}
		}else if(JSON.isUnderline()){
			text.setSelectedFont(font.getUNDERLINE());
		} else if(JSON.isItalic()){
			text.setSelectedFont(font.getITALIC());
			if(JSON.isUnderline()){
				text.setSelectedFont(font.getITALIC_UNDERLINE());
			}
		}
		text.setVisible(false);
		text.setReady(true);
		return text;
	}
}
