package commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.SmashPlayer;
import main.Smashplex;

public class SmashSkullfireCommand extends SCommand {

	public SmashSkullfireCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public boolean handle(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (Smashplex.smash) {
				Player p = (Player) sender;
				if (sender.hasPermission(getPermission())) {
					SmashPlayer sp = Smashplex.getSmashPlayer(p);
					if (sp != null) {
						if (sp.getSelectedHero() == null) {
							sp.selectHero(1);
							sender.sendMessage("You selected skullfire");
							return true;
						} else {
							sender.sendMessage("You already have a hero selected");
						}
					} else {
						sender.sendMessage("Error #0 please contact NiconatorTM");
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
