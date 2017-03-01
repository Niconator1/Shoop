package commands;

import org.bukkit.command.CommandSender;

import main.Smashplex;

public class SmashEnableCommand extends SCommand {
	public SmashEnableCommand(String name, String perm) {
		super(name, perm);
	}

	@Override
	public boolean handle(CommandSender sender, String[] args) {
		if (sender.hasPermission(getPermission())) {
			if (args.length > 0) {
				if (args[0].equals("enable")) {
					Smashplex.smash = true;
					sender.sendMessage("Smash enabled");
					return true;
				} else if (args[0].equals("disable")) {
					Smashplex.smash = false;
					sender.sendMessage("Smash disabled");
					return true;
				} else {
					sender.sendMessage("Usage: /smash <enable/disable>");
				}
			} else {
				sender.sendMessage("Usage: /smash <enable/disable>");
			}
		} else {
			sender.sendMessage("You don't have the permission to do this command");
		}
		return false;
	}

}
