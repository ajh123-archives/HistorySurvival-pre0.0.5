package net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator;

public class FontGroup {
	FontType NORMAL;

	FontType BOLD;
	FontType BOLD_ITALIC;
	FontType BOLD_UNDERLINE;
	FontType BOLD_ITALIC_UNDERLINE;

	FontType ITALIC;
	FontType ITALIC_UNDERLINE;

	FontType UNDERLINE;

	public FontGroup(FontType NORMAL, FontType BOLD, FontType BOLD_ITALIC, FontType BOLD_UNDERLINE, FontType BOLD_ITALIC_UNDERLINE, FontType ITALIC, FontType ITALIC_UNDERLINE, FontType UNDERLINE) {
		this.NORMAL = NORMAL;
		this.BOLD = BOLD;
		this.BOLD_ITALIC = BOLD_ITALIC;
		this.BOLD_UNDERLINE = BOLD_UNDERLINE;
		this.BOLD_ITALIC_UNDERLINE = BOLD_ITALIC_UNDERLINE;
		this.ITALIC = ITALIC;
		this.ITALIC_UNDERLINE = ITALIC_UNDERLINE;
		this.UNDERLINE = UNDERLINE;
	}

	public FontType getNORMAL() {
		return NORMAL;
	}

	public FontType getBOLD() {
		return BOLD;
	}

	public FontType getBOLD_ITALIC() {
		return BOLD_ITALIC;
	}

	public FontType getBOLD_ITALIC_UNDERLINE() {
		return BOLD_ITALIC_UNDERLINE;
	}

	public FontType getBOLD_UNDERLINE() {
		return BOLD_UNDERLINE;
	}

	public FontType getITALIC() {
		return ITALIC;
	}

	public FontType getITALIC_UNDERLINE() {
		return ITALIC_UNDERLINE;
	}

	public FontType getUNDERLINE() {
		return UNDERLINE;
	}
}
