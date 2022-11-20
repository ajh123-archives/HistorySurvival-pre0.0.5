package net.ddns.minersonline.HistorySurvival.engine;

import imgui.ImGui;
import net.ddns.minersonline.HistorySurvival.api.data.text.ChatColor;
import net.ddns.minersonline.HistorySurvival.api.data.text.JSONTextComponent;

public class TextBuilder {
	public static void ImGuiJsonText(JSONTextComponent textComponent) {
		ChatColor color = textComponent.getColor();
		if (color == null) {
			textComponent.setColor(ChatColor.WHITE);
		}
		ImGui.textColored(
				textComponent.getColor().color.getRed(),
				textComponent.getColor().color.getGreen(),
				textComponent.getColor().color.getBlue(),
				textComponent.getColor().color.getAlpha(),
				textComponent.getText()
		);

		if (ImGui.isItemHovered()) {
			if (textComponent.getHoverEvent() != null) {
				if (textComponent.getHoverEvent().getAction().equals("show_text")) {
					JSONTextComponent tooltip = JSONTextComponent.fromJSON(textComponent.getHoverEvent().getValue());
					ImGui.beginTooltip();
					ImGuiJsonText(tooltip);
					ImGui.endTooltip();
				}
			}
		}

		if (textComponent.getExtra() != null) {
			for (JSONTextComponent other : textComponent.getExtra()) {
				if (!other.getText().startsWith("\n")) {
					ImGui.sameLine();
				} else {
					other.setText(other.getText().replaceFirst("\n", ""));
				}
				ImGuiJsonText(other);
			}
		}
	}
}
