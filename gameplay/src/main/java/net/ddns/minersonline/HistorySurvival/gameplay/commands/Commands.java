package net.ddns.minersonline.HistorySurvival.gameplay.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.ddns.minersonline.HistorySurvival.api.commands.CommandSender;
import net.ddns.minersonline.HistorySurvival.api.events.CommandRegisterEvent;
import org.pf4j.Extension;

@Extension
public class Commands implements CommandRegisterEvent {
	@Override
	public void register(CommandDispatcher<CommandSender> dispatcher) {
		GamePlayCommand.register(dispatcher);
	}
}
