package main;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import commands.SCommand;
import commands.SmashArmorCommand;
import commands.SmashBotmobileCommand;
import commands.SmashEnableCommand;
import commands.SmashGameCommand;
import commands.SmashHeroRemoveCommand;
import commands.SmashMasterSkinCommand;
import commands.SmashShoopCommand;
import commands.SmashSkullfireCommand;

public class SmashCommands {
	private static ArrayList<SCommand> commands = new ArrayList<SCommand>();

	public static boolean handleCommand(CommandSender sender, Command cmd, String[] args) {
		for (int i = 0; i < commands.size(); i++) {
			SCommand c = commands.get(i);
			if (c.getName().equalsIgnoreCase(cmd.getName())) {
				return c.handle(sender, args);
			}
		}
		return false;
	}

	public static void registerCommands() {
		commands.add(new SmashEnableCommand("smash", "smash.admin"));
		commands.add(new SmashArmorCommand("armor", "smash.util"));
		commands.add(new SmashHeroRemoveCommand("removehero", ""));
		commands.add(new SmashBotmobileCommand("botmobile", "smash.util"));
		commands.add(new SmashShoopCommand("shoop", "smash.util"));
		commands.add(new SmashSkullfireCommand("skullfire", "smash.util"));
		commands.add(new SmashMasterSkinCommand("masterskin", "smash.util"));
		commands.add(new SmashGameCommand("game", "smash.game"));
	}

}
