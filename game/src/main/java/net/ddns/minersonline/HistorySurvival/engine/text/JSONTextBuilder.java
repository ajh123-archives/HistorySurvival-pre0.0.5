package net.ddns.minersonline.HistorySurvival.engine.text;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;

import java.lang.reflect.Type;
import java.util.Collection;

public class JSONTextBuilder {
	public static Collection<JSONTextComponent> build(String JSON){
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<JSONTextComponent>>(){}.getType();
		return gson.fromJson(JSON, collectionType);
	}

	public static GUIText build_string(FontType font, String JSON){
		Collection<JSONTextComponent> texts = JSONTextBuilder.build(JSON);
		GUIText lastText = null;
		GUIText firstText = null;
		for(JSONTextComponent text : texts){
			GUIText text1 = text.asText(font, lastText);
			text1.setParent(lastText);
			text1.load();
			lastText = text1;
			if(firstText == null){
				firstText = text1;
				continue;
			}
			firstText.getChildren().add(text1);
		}
		return firstText;
	}
}
