package net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

public class TextMeshCreator {

	public static final double LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private final MetaFile metaData;

	protected TextMeshCreator(String metaFile) {
		metaData = new MetaFile(metaFile);
	}

	protected TextMeshData createTextMesh(GUIText text) {
		List<Line> lines = createStructure(text);
		return createQuadVertices(text, lines);
	}

	private List<Line> createStructure(GUIText text) {
		String lineOfText = text.getTextString().replaceAll("\t", "    ");
		char[] chars = lineOfText.toCharArray();
		List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());
		for (char c : chars) {
			if ((int) c == SPACE_ASCII || c == '\n') {
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}
				currentWord = new Word(text.getFontSize());
				if(c == '\n') {
					text.setEndX(0);
					text.setOnNewLine(true);
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
				}
				continue;
			}

			Character character = metaData.getCharacter(c);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, GUIText text) {
		boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(GUIText text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double curserX = 0f;
		double curserY = 0f;
		if (text.isReady() || text.getParent() != null) {
			curserX = text.getEndX();
			if(text.isOnNewLine()){
				curserX = 0;
			}
			curserY = text.getEndY();
			if(text.getParent() != null){
				text.setReady(false);
			}
		}
		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoOrds = new ArrayList<>();
		double lineEndX = 0;

		for (Line line : lines) {
			if (text.isCentered()) {
				curserX = (line.getMaxLength() - line.getLineLength()) / 2;
			}
			for (Word word : line.getWords()) {
				for (Character letter : word.getCharacters()) {
					addVerticesForCharacter(curserX, curserY, letter, text.getFontSize(), vertices);
					addTexCoords(textureCoOrds, letter.getxTextureCoord(), letter.getyTextureCoord(),
							letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					curserX += letter.getxAdvance() * text.getFontSize();
				}
				lineEndX = curserX;
				curserX += metaData.getSpaceWidth() * text.getFontSize();
			}
			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSize();
		}
		if (!text.isReady() || text.getParent() != null) {
			double size = LINE_HEIGHT * text.getFontSize();
			text.setEndX(lineEndX);
			text.setEndY(curserY-size);
			if(text.getParent() != null){
				text.setReady(true);
			}
		}
		return new TextMeshData(listToArray(vertices), listToArray(textureCoOrds));
	}

	private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize,
										 List<Float> vertices) {
		double x = curserX + (character.getxOffset() * fontSize);
		double y = curserY + (character.getyOffset() * fontSize);
		double maxX = x + (character.getSizeX() * fontSize);
		double maxY = y + (character.getSizeY() * fontSize);
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}

	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}

	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}


	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}

}