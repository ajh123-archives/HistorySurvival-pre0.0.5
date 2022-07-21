package net.ddns.minersonline.HistorySurvival.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.engine.entities.ClientPlayer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTextBox;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.text.JSONTextBuilder;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.FontGroup;
import net.ddns.minersonline.HistorySurvival.engine.text.fontMeshCreator.GUIText;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatSystem {
	private final int MAX_CHAT_LENGTH = 50;

	private boolean isInChat = false;
	private boolean ignoreChat = true;
	private static final List<JSONTextComponent> chat = new ArrayList<>();

	private final ClientPlayer player;
	List<String> completions = new ArrayList<>();

	public ChatSystem(FontGroup font, ClientPlayer player) {
		this.player = player;
	}

	public void update(KeyEvent keyEvent){
//		chatParent.remove();
//		chatParent = null;
//
//		chatParent = new GUIText("", 1.3f, font, new Vector2f(0, 0.13f), MAX_CHAT_LENGTH, false);
//		chatText = JSONTextBuilder.build_string_array(chat, chatParent, chatText);
//
//		chatPreview.setFocused(isInChat);
//		chatPreview.setVisible(isInChat);
//		if(isInChat){
//			chatParent.remove();
//			chatParent = null;
//			if (chatText != null) {
//				chatText.remove();
//			}
//
//			chatPreview.setPosition(new Vector2f(0, 1-0.13f));
//			chatPreview.render();
//
//			chatParent = new GUIText("", 1.3f, font, new Vector2f(0, 0.13f), 50, false);
//			chatText = JSONTextBuilder.build_string_array(chat, chatParent, chatText);
//
//			chatPreview.setOnExecute(message -> {
//				if(message.length() > 0) {
//					String command;
//					if(message.charAt(0) == '/') {
//						command = message.substring(1);
//						final ParseResults<Object> parse = GameHook.getInstance().getDispatcher().parse(command, player);
//						try {
//							GameHook.getInstance().getDispatcher().execute(parse);
//						} catch (CommandSyntaxException e) {
//							JSONTextComponent msg = new JSONTextComponent();
//							msg.setColor(ChatColor.RED.toString());
//							msg.setText(e.getMessage() + "\n");
//							chat.add(msg);
//							JSONTextComponent msg2 = new JSONTextComponent();
//							msg2.setText(message + "\n");
//							chat.add(msg2);
//						}
//					} else {
//						JSONTextComponent msg = new JSONTextComponent();
//						msg.setText(message + "\n");
//						chat.add(msg);
//					}
//				}
//				isInChat = false;
//				return null;
//			});
//
//
//			StringBuilder message = chatPreview.getMessage();
//			if(message.length() > 0) {
//				if(message.charAt(0) == '/') {
//					String command = message.substring(1);
//					ParseResults<Object> parse = GameHook.getInstance().getDispatcher().parse(command, player);
//					StringReader reader = new StringReader(parse.getReader().getString());
//					reader.setCursor(parse.getReader().getCursor());
//					completions.clear();
//					try {
//						String phrase = reader.readString();
//						Suggestions suggestions = GameHook.getInstance().getDispatcher().getCompletionSuggestions(parse).get();
//						for(Suggestion suggestion : suggestions.getList())
//							if(suggestion.getText().startsWith(phrase))
//								completions.add(suggestion.getText());
//					} catch (CommandSyntaxException | ExecutionException | InterruptedException ignored) {}
//				}
//			}
//
//
//			if(isInChat) {
//				if(chatText != null) {
//					chatText.setVisible(true);
//				}
//				chatPreview.update(keyEvent, ignoreChat);
//				ignoreChat  = false;
//			}
//		}
	}

	public boolean notIsInChat() {
		return !isInChat;
	}

	public void setInChat(boolean inChat) {
		isInChat = inChat;
		if(inChat){
			ignoreChat = true;
		}
	}

	public void cleanUp(){

	}

	public static void addChatMessage(JSONTextComponent text){
		chat.add(text);
	}
}
