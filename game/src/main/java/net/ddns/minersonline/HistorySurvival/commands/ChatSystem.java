package net.ddns.minersonline.HistorySurvival.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.api.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontType;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ChatSystem {
	private final int MAX_CHAT_LENGTH = 50;

	private GUIText chatParent;
	private GUIText chatPreview;
	private GUIText chatText = null;

	private boolean isInChat = false;
	private boolean ignoreChat = true;
	private int chatChars = 0;
	private StringBuilder previewText = new StringBuilder();
	private static final List<JSONTextComponent> chat = new ArrayList<>();
	private final StringBuilder message = new StringBuilder();
	private final FontType font;

	private Player player;

	public ChatSystem(FontType font, Player player) {
		this.chatParent = new GUIText("", 1.3f, font, new Vector2f(0, 0), MAX_CHAT_LENGTH, false);
		this.chatPreview = new GUIText("", 1.3f, font, new Vector2f(0, 0), MAX_CHAT_LENGTH, false);
		this.font = font;
		this.player = player;
	}

	public void update(KeyEvent keyEvent){
		float y = (1f/2f);
		chatParent.remove();
		if (chatText != null) {
			chatText.remove();
		}
		chatParent = new GUIText("", 1.3f, font, new Vector2f(0, y), 100, false);
		chatText = JSONTextBuilder.build_string_array(chat, chatParent);

		if(isInChat){
			chatParent.remove();
			if (chatText != null) {
				chatText.remove();
			}
			chatPreview.remove();
			chatPreview = new GUIText(previewText.toString(), 1.3f, font, new Vector2f(0, 1), 50, false);
			chatPreview.load();
			chatPreview.setVisible(true);
			chatParent = new GUIText("", 1.3f, font, new Vector2f(0, y-0.1f), 50, false);
			chatText = JSONTextBuilder.build_string_array(chat, chatParent);

			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
				message.setLength(Math.max(message.length() - 1, 0));
				previewText.setLength(Math.max(previewText.length() - 1, 0));
				chatChars -= 1;
			}

			if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_ENTER)){
				if(message.length() > 0) {
					String command;
					if(message.charAt(0) == '/') {
						command = message.substring(1);
						final ParseResults<Object> parse = GameHook.getInstance().getDispatcher().parse(command, player);
						try {
							GameHook.getInstance().getDispatcher().execute(parse);
						} catch (CommandSyntaxException e) {
							JSONTextComponent msg = new JSONTextComponent();
							msg.setColor(ChatColor.RED.toString());
							msg.setText(e.getMessage() + "\n");
							chat.add(msg);
						}
					} else {
						JSONTextComponent msg = new JSONTextComponent();
						msg.setText(message + "\n");
						chat.add(msg);
					}
				}
				isInChat = false;
				ignoreChat = true;
				message.delete(0, message.length());
				chatChars = 0;
				previewText.delete(0, previewText.length());
				chatPreview.remove();
			}
			if(isInChat) {
				if (chatText != null) {
					chatText.setVisible(true);
				}
				if (chatChars < MAX_CHAT_LENGTH && keyEvent != null && keyEvent.type == 2) {
					String char_ = keyEvent.getChar();
					if(!ignoreChat) {
						message.append(char_);
						chatChars += 1;
						previewText.append(char_);
					}
					ignoreChat  = false;
				}
			}
		}
	}

	public boolean notIsInChat() {
		return !isInChat;
	}

	public void setInChat(boolean inChat) {
		isInChat = inChat;
	}

	public static void addChatMessage(JSONTextComponent text){
		chat.add(text);
	}
}
