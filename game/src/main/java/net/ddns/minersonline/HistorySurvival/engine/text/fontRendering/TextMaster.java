package net.ddns.minersonline.HistorySurvival.engine.text.fontRendering;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.text.MeshData;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.TextMeshData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TextMaster {
    private static ModelLoader loader;
    private static final Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(ModelLoader loader){
        renderer = new FontRenderer();
        TextMaster.loader = loader;
    }

    public static void render(){
        renderer.render(texts);
    }

    public static void loadText(GUIText text){
        FontType font = text.getSelectedFont();
        TextMeshData data = font.loadText(text);
        MeshData ids = loader.loadToVao(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(ids, data.getVertexCount());
        List<GUIText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public static void removeText(GUIText text){
        List<GUIText> textBatch = texts.get(text.getSelectedFont());
        if(textBatch != null) {
            textBatch.remove(text);
            if (textBatch.isEmpty()) {
                loader.destroy(text.getMesh());
                texts.remove(text.getSelectedFont());
            }
        }
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }
}
