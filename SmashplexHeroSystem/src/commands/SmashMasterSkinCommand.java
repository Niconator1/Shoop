package commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.NPC;
import main.Smashplex;

public class SmashMasterSkinCommand extends SCommand {

	public SmashMasterSkinCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public boolean handle(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (Smashplex.smash) {
				Player p = (Player) sender;
				if (sender.hasPermission(getPermission())) {
					if (args.length > 0) {
						boolean ms = Boolean.parseBoolean(args[0]);
						for (int i = 0; i < Smashplex.npcs.size(); i++) {
							NPC n = Smashplex.npcs.get(i);
							n.sendEquipmentPacket(p, ms);
						}
					} else {
						sender.sendMessage("Usage: /smash <true/false>");
					}
				} else {
					sender.sendMessage("You don't have the permission to do this command");
				}
			} else {
				sender.sendMessage("The plugin is disabled right now");
			}
		} else {
			sender.sendMessage("This command can only be used as a player");
		}
		return false;
	}

}
