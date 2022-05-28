package net.ddns.minersonline.HistorySurvival.engine.guis;

import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

public class GuiTextBox {
	private FontGroup font;
	private Vector2f position;
	private GUIText guiText;
	private GUIText guiTextParent;
	private int charsCount = 0;
	private final StringBuilder message = new StringBuilder();
	private StringBuilder previewText = new StringBuilder();
	private boolean isVisible = true;
	private boolean isFocused = true;
	private Function<StringBuilder, Object> onExecute = null;
	private int MAX_LENGTH;

	public GuiTextBox(FontGroup font, Vector2f position, int MAX_LENGTH) {
		this.font = font;
		this.position = position;
		this.MAX_LENGTH = MAX_LENGTH;
		this.guiText = new GUIText("", 1.3f, font, new Vector2f(position), MAX_LENGTH, false);
	}

	public FontGroup getFont() {
		return font;
	}

	public void setFont(FontGroup font) {
		this.font = font;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public GUIText getGuiText() {
		return guiText;
	}

	public StringBuilder getMessage() {
		return message;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public boolean isFocused() {
		return isFocused;
	}

	public void setFocused(boolean focused) {
		isFocused = focused;
	}

	public void setOnExecute(Function<StringBuilder, Object> onExecute) {
		this.onExecute = onExecute;
	}

	public void render(){
		if(this.guiText != null) {
			guiText.remove();
			guiTextParent = new GUIText("", 1.3f, font, new Vector2f(position), MAX_LENGTH, false);
			guiText = JSONTextBuilder.asText(new JSONTextComponent(previewText.toString()), font, guiTextParent);
			guiText.setVisible(isVisible);
			guiText.load();
		}
	}

	public void update(KeyEvent keyEvent, boolean ignore){
		if(isFocused) {
			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
				message.setLength(Math.max(message.length() - 1, 0));
				previewText.setLength(Math.max(previewText.length() - 1, 0));
				charsCount -= 1;
			}

			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_ENTER)){
				onExecute.apply(message);
				isFocused = false;
				ignore = true;
				message.delete(0, message.length());
				charsCount = 0;
				previewText.delete(0, previewText.length());
				if(this.guiText != null) {
					guiText.setVisible(false);
					guiText.remove();
				}
			}
			if(isFocused) {
				if (guiText != null) {
					guiText.setVisible(true);
				}
				if (charsCount < MAX_LENGTH && keyEvent != null && keyEvent.type == 2) {
					String char_ = keyEvent.getChar();
					if(!ignore) {
						message.append(char_);
						charsCount += 1;
						previewText.append(char_);
					}
				}
				if (MAX_LENGTH == -1 && keyEvent != null && keyEvent.type == 2) {
					String char_ = keyEvent.getChar();
					if(!ignore) {
						message.append(char_);
						charsCount += 1;
						previewText.append(char_);
					}
				}
			}
		}
	}

	public void cleanUp(){
		guiText.setVisible(false);
		guiText.remove();
		guiText = null;
		if(guiTextParent != null) {
			guiTextParent.setVisible(false);
			guiTextParent.remove();
			guiTextParent = null;
		}
	}
}
