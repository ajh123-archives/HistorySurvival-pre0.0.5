package net.ddns.minersonline.HistorySurvival.engine.text;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.utils.StringUtils;

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
			GUIText text1 = text.asText(parent.getFont(), lastText);
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

	public static GUIText build_string_array(List<JSONSafeText> JSON_List, GUIText parent){
		String JSON = "[]";
		for (JSONSafeText message : JSON_List) {
			JSON = StringUtils.removeLastChar(JSON);
			if (!JSON.equals("[")) {
				JSON += "," + message.getText() + "\n" + "]";
			} else {
				JSON += message.getText() + "\n" + "]";
			}
		}
		return build_string(JSON, parent);
	}
}
