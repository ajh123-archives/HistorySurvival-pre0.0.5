package net.ddns.minersonline.HistorySurvival.api.events;

import com.mojang.brigadier.CommandDispatcher;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import org.pf4j.ExtensionPoint;

public interface CommandRegisterEvent extends ExtensionPoint {
	void register(CommandDispatcher<CommandSender> dispatcher);
}
