package net.ddns.minersonline.HistorySurvival.engine.text.fontRendering;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
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
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVao(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
        if(text.hasForked()){
            GUIText rootText = text.getRoot();
            FontType root_font = rootText.getFont();
            TextMeshData root_data = root_font.loadText(rootText);
            int root_vao = loader.loadToVao(root_data.getVertexPositions(), root_data.getTextureCoords());
            text.setMeshInfo(root_vao, data.getVertexCount());
            List<GUIText> root_textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
            root_textBatch.add(rootText);

            for(GUIText text2: text.getChildTexts()){
                FontType font2 = text.getFont();
                TextMeshData data2 = font.loadText(text2);
                int vao2 = loader.loadToVao(data2.getVertexPositions(), data2.getTextureCoords());
                text2.setMeshInfo(vao2, data2.getVertexCount());
                List<GUIText> textBatch2 = texts.computeIfAbsent(font2, k -> new ArrayList<>());
                textBatch2.add(text2);
            }
        }
    }

    public static void removeText(GUIText text){
        List<GUIText> textBatch = texts.get(text.getFont());
        if(textBatch != null) {
            textBatch.remove(text);
            if (textBatch.isEmpty()) {
                loader.destroy(text.getMesh());
                texts.remove(text.getFont());
            }
        }
        if(text.hasForked()){
            GUIText root = text.getRoot();
            if(root != null) {
                removeText(root);
            }
            for(GUIText text2: text.getChildTexts()){
                removeText(text2);
            }
        }
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }
}
