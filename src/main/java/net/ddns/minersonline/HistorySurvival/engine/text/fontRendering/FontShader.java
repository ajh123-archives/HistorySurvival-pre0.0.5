package net.ddns.minersonline.HistorySurvival.engine.text.fontRendering;

import net.ddns.minersonline.HistorySurvival.engine.shaders.ShaderProgramBase;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class FontShader extends ShaderProgramBase {

	private static final String VERTEX_FILE = "shaders/fontVertex.glsl";
	private static final String FRAGMENT_FILE = "shaders/fontFragment.glsl";

	private static int location_colour;
	private static int location_translation;
	private int location_width;
	private int location_edge;
	private int location_borderWidth;
	private int location_borderEdge;
	private int location_offset;
	private int location_outlineColor;

	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colour = super.getUniformLocation("colour");
		location_translation = super.getUniformLocation("translation");
		location_width = super.getUniformLocation("width");
		location_edge = super.getUniformLocation("edge");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_borderEdge = super.getUniformLocation("borderEdge");
		location_offset = super.getUniformLocation("offset");
		location_outlineColor = super.getUniformLocation("outlineColor");
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

	protected void loadWidth(float width) {
		super.loadFloat(location_width, width);
	}

	protected void loadEdge(float edge) {
		super.loadFloat(location_edge, edge);
	}

	protected void loadBorderWidth(float borderWidth) {
		super.loadFloat(location_borderWidth, borderWidth);
	}

	protected void loadBorderEdge(float borderEdge) {
		super.loadFloat(location_borderEdge, borderEdge);
	}

	protected void loadOffset(Vector2f offset) {
		super.loadVector(location_offset, offset);
	}

	protected void loadOutlineColor(Vector3f outlineColor) {
		super.loadVector(location_outlineColor, outlineColor);
	}
}
