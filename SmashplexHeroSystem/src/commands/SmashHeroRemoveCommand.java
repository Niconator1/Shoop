package commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.SmashPlayer;
import main.Smashplex;

public class SmashHeroRemoveCommand extends SCommand {

	public SmashHeroRemoveCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public boolean handle(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			SmashPlayer sp = Smashplex.getSmashPlayer(p);
			if (sp != null) {
				if (sp.getSelectedHero() != null) {
					sp.resetHero(true, true);
				}
				sender.sendMessage("Your selected hero was reseted");
				return true;
			} else {
				sender.sendMessage("Error #0 please contact NiconatorTM");
			}
		} else {
			sender.sendMessage("This command can only be used as a player");
		}
		return false;
	}

}
