package net.ddns.minersonline.HistorySurvival.engine.fontRendering;

import net.ddns.minersonline.HistorySurvival.engine.shaders.ShaderProgramBase;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FontShader extends ShaderProgramBase {

	private static final String VERTEX_FILE = "shaders/fontVertex.glsl";
	private static final String FRAGMENT_FILE = "shaders/fontFragment.glsl";

	private static int location_colour;
	private static int location_fontAtlas;
	private static int location_translation;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_fontAtlas = super.getUniformLocation("fontAtlas");
		location_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoOrds");
	}

	protected void loadColour(Vector3f colour){
		super.loadVector(location_colour, colour);
	}

	protected void loadTranslation(Vector2f translation){
		super.loadVector(location_translation, translation);
	}
}
