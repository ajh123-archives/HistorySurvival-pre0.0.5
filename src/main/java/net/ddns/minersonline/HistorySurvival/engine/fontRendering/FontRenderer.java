package net.ddns.minersonline.HistorySurvival.engine.fontRendering;

import net.ddns.minersonline.HistorySurvival.engine.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.fontMeshCreator.GUIText;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}

	public void cleanUp(){
		shader.destroy();
	}

	public void render(Map<FontType, List<GUIText>> texts){
		prepare();
		for(FontType font : texts.keySet()){
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for(GUIText text : texts.get(font)){
				if(text.isVisible()) {
					renderText(text);
				}
			}
		}
		endRendering();
	}

	private void prepare(){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		shader.bind();
	}
	
	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColour(text.getColour());
		shader.loadTranslation(text.getPosition());
		shader.loadWidth(text.getWidth()); // 0.5f
		shader.loadEdge(text.getEdge()); // 0.1f
		shader.loadBorderWidth(text.getBorderWidth()); // 0.7f
		shader.loadBorderEdge(text.getBorderEdge()); // 0.1f
		shader.loadOffset(text.getOffset()); //new Vector2f(0.0f, 0.0f));
		shader.loadOutlineColor(text.getOutlineColor()); //new Vector3f(1.0f, 1.0f, 1.0f));
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.unbind();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
