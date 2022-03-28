package net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator;


import net.ddns.minersonline.HistorySurvival.engine.text.fontRendering.TextMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a piece of text in the game.
 * 
 * @author Karl
 *
 */
public class GUIText {

	private String textString;
	private String ogTextString;
	private final float fontSize;
	private List<GUIText> childTexts;
	private GUIText parent = null;
	private GUIText root = null;
	private boolean child = false;
	private boolean hasForked = false;

	private double endX = 0f;
	private double endY = 0f;

	private int textMeshVao;
	private int vertexCount;
	private final Vector3f colour = new Vector3f(0f, 0f, 0f);

	private final Vector2f position;
	private float width = 0.5f;
	private float edge = 0.1f;
	private float borderWidth = 0.7f;
	private float borderEdge = 0.1f;
	private Vector2f offset = new Vector2f(0.0f, 0.0f);
	private final Vector3f outlineColor = new Vector3f(1f, 1f, 1f);

	private final float lineMaxSize;
	private int numberOfLines;

	private final FontType font;
	private boolean visible = true;

	private boolean centerText;

	/**
	 * Creates a new text, loads the text's quads into a VAO, and adds the text
	 * to the screen.
	 * 
	 * @param text
	 *            - the text.
	 * @param fontSize
	 *            - the font size of the text, where a font size of 1 is the
	 *            default size.
	 * @param font
	 *            - the font that this text should use.
	 * @param position
	 *            - the position on the screen where the top left corner of the
	 *            text should be rendered. The top left corner of the screen is
	 *            (0, 0) and the bottom right is (1, 1).
	 * @param maxLineLength
	 *            - basically the width of the virtual page in terms of screen
	 *            width (1 is full screen width, 0.5 is half the width of the
	 *            screen, etc.) Text cannot go off the edge of the page, so if
	 *            the text is longer than this length it will go onto the next
	 *            line. When text is centered it is centered into the middle of
	 *            the line, based on this line length value.
	 * @param centered
	 *            - whether the text should be centered or not.
	 */
	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength,
			boolean centered) {
		this.textString = text;
		this.ogTextString = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		childTexts = new ArrayList<>();
		TextMaster.loadText(this);
	}

	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered, GUIText parent) {
		this(text, fontSize, font, position, maxLineLength, centered);
		this.child = true;
		this.parent = parent;
	}

	/**
	 * Remove the text from the screen.
	 */
	public void remove() {
		for(GUIText child : childTexts){
			TextMaster.removeText(child);
		}
		TextMaster.removeText(this);
	}

	/**
	 * @return The font used by this text.
	 */
	public FontType getFont() {
		return font;
	}

	/**
	 * Set the colour of the text.
	 * 
	 * @param r
	 *            - red value, between 0 and 1.
	 * @param g
	 *            - green value, between 0 and 1.
	 * @param b
	 *            - blue value, between 0 and 1.
	 */
	public void setColour(float r, float g, float b) {
		colour.set(r, g, b);
	}

	/**
	 * @return the colour of the text.
	 */
	public Vector3f getColour() {
		return colour;
	}

	/**
	 * @return The number of lines of text. This is determined when the text is
	 *         loaded, based on the length of the text and the max line length
	 *         that is set.
	 */
	public int getNumberOfLines() {
		return numberOfLines;
	}

	/**
	 * @return The position of the top-left corner of the text in screen-space.
	 *         (0, 0) is the top left corner of the screen, (1, 1) is the bottom
	 *         right.
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * @return the ID of the text's VAO, which contains all the vertex data for
	 *         the quads on which the text will be rendered.
	 */
	public int getMesh() {
		return textMeshVao;
	}

	/**
	 * Set the VAO and vertex count for this text.
	 * 
	 * @param vao
	 *            - the VAO containing all the vertex data for the quads on
	 *            which the text will be rendered.
	 * @param verticesCount
	 *            - the total number of vertices in all of the quads.
	 */
	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	/**
	 * @return The total number of vertices of all the text's quads.
	 */
	public int getVertexCount() {
		return this.vertexCount;
	}

	/**
	 * @return the font size of the text (a font size of 1 is normal).
	 */
	protected float getFontSize() {
		return fontSize;
	}

	/**
	 * Sets the number of lines that this text covers (method used only in
	 * loading).
	 * 
	 * @param number
	 */
	protected void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	/**
	 * @return {@code true} if the text should be centered.
	 */
	protected boolean isCentered() {
		return centerText;
	}

	/**
	 * @return The maximum length of a line of this text.
	 */
	protected float getMaxLineSize() {
		return lineMaxSize;
	}

	/**
	 * @return The string of text.
	 */
	public String getTextString() {
		return textString;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if(hasForked) {
			for (GUIText child : childTexts) {
				child.setVisible(visible);
			}
		}
		this.visible = visible;
	}

	public GUIText split(int pos) {
		hasForked = true;
		TextMaster.removeText(this);
		for (GUIText child : childTexts){
			TextMaster.removeText(child);
		}
		childTexts.clear();
		String sub = this.ogTextString.substring(0, pos);
		String remainder = this.ogTextString.substring(pos+2);
		textString = sub;
		GUIText child = new GUIText(remainder, fontSize, font, position, lineMaxSize, centerText, this);
		child.setVisible(visible);
		childTexts.add(child);
		root = new GUIText(textString, fontSize, font, position, lineMaxSize, centerText);
		root.setVisible(visible);
		return child;
	}

    public void setTextString(String a) {
		textString = a;
		ogTextString = a;
		TextMaster.removeText(this);
		TextMaster.loadText(this);
    }

	public boolean hasForked() {
		return hasForked;
	}

	public List<GUIText> getChildTexts() {
		return childTexts;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getEdge() {
		return edge;
	}

	public void setEdge(float edge) {
		this.edge = edge;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
	}

	public float getBorderEdge() {
		return borderEdge;
	}

	public void setBorderEdge(float borderEdge) {
		this.borderEdge = borderEdge;
	}

	public Vector2f getOffset() {
		return offset;
	}

	public void setOffset(Vector2f offset) {
		this.offset = offset;
	}

	public Vector3f getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(float r, float g, float b) {
		outlineColor.set(r, g, b);
	}

	public boolean isChild() {
		return child;
	}

	public GUIText getParent() {
		return parent;
	}

	public GUIText getRoot() {
		return root;
	}

	public double getEndX() {
		return endX;
	}

	public void setEndX(double endX) {
		this.endX = endX;
	}

	public double getEndY() {
		return endY;
	}

	public void setEndY(double endY) {
		this.endY = endY;
	}
}
