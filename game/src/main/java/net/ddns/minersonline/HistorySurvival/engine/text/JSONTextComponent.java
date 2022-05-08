package net.ddns.minersonline.HistorySurvival.engine.text;

import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import org.joml.Vector2f;

import java.util.List;

public class JSONTextComponent {
	private String text;
	private String translate;
	private List<String> with;
	private JSONScore score;
	private String selector;
	private String keybind;
	private String color;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private boolean strikethrough;
	private boolean obfuscated;
	private String insertion;
	private JSONClickEvent clickEvent;
	private JSONHoverEvent hoverEvent;
	private List<String> extra;

	public JSONTextComponent(String text, String translate, List<String> with, JSONScore score, String selector, String keybind, String color, boolean bold, boolean italic, boolean underline, boolean strikethrough, boolean obfuscated, String insertion, JSONClickEvent clickEvent, JSONHoverEvent hoverEvent, List<String> extra) {
		this.text = text;
		this.translate = translate;
		this.with = with;
		this.score = score;
		this.selector = selector;
		this.keybind = keybind;
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.insertion = insertion;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
		this.extra = extra;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTranslate() {
		return translate;
	}

	public void setTranslate(String translate) {
		this.translate = translate;
	}

	public List<String> getWith() {
		return with;
	}

	public void setWith(List<String> with) {
		this.with = with;
	}

	public JSONScore getScore() {
		return score;
	}

	public void setScore(JSONScore score) {
		this.score = score;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getKeybind() {
		return keybind;
	}

	public void setKeybind(String keybind) {
		this.keybind = keybind;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public boolean isStrikethrough() {
		return strikethrough;
	}

	public void setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
	}

	public boolean isObfuscated() {
		return obfuscated;
	}

	public void setObfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
	}

	public String getInsertion() {
		return insertion;
	}

	public void setInsertion(String insertion) {
		this.insertion = insertion;
	}

	public JSONClickEvent getClickEvent() {
		return clickEvent;
	}

	public void setClickEvent(JSONClickEvent clickEvent) {
		this.clickEvent = clickEvent;
	}

	public JSONHoverEvent getHoverEvent() {
		return hoverEvent;
	}

	public void setHoverEvent(JSONHoverEvent hoverEvent) {
		this.hoverEvent = hoverEvent;
	}

	public List<String> getExtra() {
		return extra;
	}

	public void setExtra(List<String> extra) {
		this.extra = extra;
	}

	public GUIText asText(FontType font, GUIText parent){
		GUIText text = new GUIText(this.text, 1.5f, font, new Vector2f(0, 0), -1, false);
		String color_char = " ";
		color_char.toCharArray()[0] = ChatColor.COLOR_CHAR;
		if(this.color!=null) {
			char chat_color = color.replaceAll(color_char, "").toCharArray()[1];
			ChatColor text_color = ChatColor.getByChar(chat_color);
			text.setColour(text_color.color.getRed() / 255f, text_color.color.getGreen() / 255f, text_color.color.getBlue() / 255f);
			text.setOutlineColor((text_color.color.getRed() / 255f) / 2, (text_color.color.getGreen() / 255f) / 2, (text_color.color.getBlue() / 255f) / 2);
		}
		if(parent != null){
			text.setEndX(parent.getEndX());
			text.setEndY(parent.getEndY());
			text.setReady(true);
		}
		return text;
	}
}
