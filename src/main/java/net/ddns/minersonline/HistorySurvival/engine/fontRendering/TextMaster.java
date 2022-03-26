package net.ddns.minersonline.HistorySurvival.engine.fontRendering;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.fontMeshCreator.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TextMaster {
    private static ModelLoader loader;
    private static Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(ModelLoader loader){
        renderer = new FontRenderer();
        TextMaster.loader = loader;
    }

    public static void render(){
        renderer.render(texts);
    }

    public static void loadText(GUIText text){
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVao(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public static void removeText(GUIText text){
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            loader.destroy(text.getMesh());
            texts.remove(text.getFont());
        }
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }
}
