package net.ddns.minersonline.HistorySurvival.api.text;

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
	private List<JSONTextComponent> extra;

	public JSONTextComponent(String text, String translate, List<String> with, JSONScore score, String selector, String keybind, String color, boolean bold, boolean italic, boolean underline, boolean strikethrough, boolean obfuscated, String insertion, JSONClickEvent clickEvent, JSONHoverEvent hoverEvent, List<JSONTextComponent> extra) {
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

	public JSONTextComponent() {
		this.text = null;
		this.translate = null;
		this.with = null;
		this.score = null;
		this.selector = null;
		this.keybind = null;
		this.color = null;
		this.bold = false;
		this.italic = false;
		this.underline = false;
		this.strikethrough = false;
		this.obfuscated = false;
		this.insertion = null;
		this.clickEvent = null;
		this.hoverEvent = null;
		this.extra = null;
	}

	public JSONTextComponent(String text) {
		this.text = text;
		this.translate = null;
		this.with = null;
		this.score = null;
		this.selector = null;
		this.keybind = null;
		this.color = null;
		this.bold = false;
		this.italic = false;
		this.underline = false;
		this.strikethrough = false;
		this.obfuscated = false;
		this.insertion = null;
		this.clickEvent = null;
		this.hoverEvent = null;
		this.extra = null;
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

	public List<JSONTextComponent> getExtra() {
		return extra;
	}

	public void setExtra(List<JSONTextComponent> extra) {
		this.extra = extra;
	}
}
