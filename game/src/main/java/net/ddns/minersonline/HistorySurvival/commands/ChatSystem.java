package net.ddns.minersonline.HistorySurvival.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.GameSettings;
import net.ddns.minersonline.HistorySurvival.api.GameHook;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.engine.TextBuilder;
import net.ddns.minersonline.HistorySurvival.engine.entities.ClientCommandExecutor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;
import net.ddns.minersonline.HistorySurvival.network.ClientHandler;
import net.ddns.minersonline.HistorySurvival.network.Utils;
import net.ddns.minersonline.HistorySurvival.network.packets.client.MessageServerPacket;

import java.util.ArrayList;
import java.util.List;

public class ChatSystem {
	private static final List<JSONTextComponent> chat = new ArrayList<>();
	private static final ImString message = new ImString();
	public static ClientHandler network = null;


	public ChatSystem() {}

	public void update(ImBoolean enableChat, ClientCommandExecutor executor){
		ImGui.setNextWindowSize(520, 220);
		ImGui.setNextWindowBgAlpha(0.5f);
		if (ImGui.begin(
			"Chat Window",
			enableChat,
			ImGuiWindowFlags.NoDecoration |
				ImGuiWindowFlags.AlwaysAutoResize |
				ImGuiWindowFlags.NoSavedSettings |
				ImGuiWindowFlags.NoFocusOnAppearing |
				ImGuiWindowFlags.NoNav |
				ImGuiWindowFlags.NoDocking
		)){
			ImGui.text("Chat");
			ImGui.separator();
			ImGui.beginChild("Chat Messages", 520, 150);
			for (JSONTextComponent component : chat) {
				TextBuilder.ImGuiJsonText(component);
			}
			ImGui.endChild();
			ImGui.separator();

			if (ImGui.inputText("Input", message, ImGuiInputTextFlags.EnterReturnsTrue)) {
				if (network == null) {
					if (executor != null) {
						if (message.get().charAt(0) == '/') {
							String command = message.get().replaceFirst("/", "");
							ParseResults<CommandSender> parse = GameHook.getInstance().getDispatcher().parse(command, executor);
							try {
								GameHook.getInstance().getDispatcher().execute(parse);
							} catch (CommandSyntaxException e) {
								JSONTextComponent msg = new JSONTextComponent();
								msg.setColor(ChatColor.RED);
								msg.setText(e.getMessage() + "\n");
								chat.add(msg);
								JSONTextComponent msg2 = new JSONTextComponent();
								msg2.setColor(ChatColor.RED);
								msg2.setText(message + "\n");
								chat.add(msg2);
							}
						} else {
							JSONTextComponent component = new JSONTextComponent();
							component.setText("<" + GameSettings.username + ">" + message.get());
							addChatMessage(component);
						}
					}
				} else {
					network.ctx.writeAndFlush(new MessageServerPacket(message.get(), Utils.ENCRYPTION_MODE));
				}
				message.clear();
			}
		}
		ImGui.end();
	}

	public void cleanUp(){

	}

	public static void addChatMessage(JSONTextComponent text){
		chat.add(text);
		Game.logger.info("[Chat] "+text.getText());
	}

	public static void clear() {
		chat.clear();
	}
}
