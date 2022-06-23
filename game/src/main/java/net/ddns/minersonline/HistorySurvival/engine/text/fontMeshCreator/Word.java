package net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator;

import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * During the loading of a text this represents one word in the text.
 * @author Karl
 *
 */
public class Word {
	
	private List<Character> characters = new ArrayList<>();
	private double width = 0;
	private double fontSize;
	private ChatColor color;

	public ChatColor getColor() {
		return color;
	}

	public void setColor(ChatColor color) {
		this.color = color;
	}

	/**
	 * Create a new empty word.
	 * @param fontSize - the font size of the text which this word is in.
	 */
	protected Word(double fontSize){
		this.fontSize = fontSize;
	}
	
	/**
	 * Adds a character to the end of the current word and increases the screen-space width of the word.
	 * @param character - the character to be added.
	 */
	protected void addCharacter(Character character){
		characters.add(character);
		width += character.getxAdvance() * fontSize;
	}
	
	/**
	 * @return The list of characters in the word.
	 */
	protected List<Character> getCharacters(){
		return characters;
	}
	
	/**
	 * @return The width of the word in terms of screen size.
	 */
	protected double getWordWidth(){
		return width;
	}

	public String toString() {
		StringBuilder me = new StringBuilder();
		for(Character character	: characters){
			me.append((char)character.getId());
		}
		return me.toString();
	}

}
