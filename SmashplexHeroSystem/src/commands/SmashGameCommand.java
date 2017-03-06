package commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.Game;
import main.Smashplex;

public class SmashGameCommand extends SCommand {
	public SmashGameCommand(String name, String perm) {
		super(name, perm);
	}

	@Override
	public boolean handle(CommandSender sender, String[] args) {
		if (sender.hasPermission(getPermission())) {
			if (sender instanceof Player) {
				if (Smashplex.smash) {
					Player p = (Player) sender;
					if (args.length > 0) {
						if (args[0].equals("join")) {
							if (Smashplex.getGame(p) != null) {
								p.sendMessage(ChatColor.YELLOW + "You are already in a game");
							} else {
								Game g = Smashplex.getFreeGame();
								if (g != null) {
									g.join(p);
								} else {
									Game ng = new Game("Kingdom", 2);
									ng.join(p);
									Smashplex.gamelist.add(ng);
								}
								return true;
							}
						} else if (args[0].equals("leave")) {
							if (Smashplex.getGame(p) != null) {
								Smashplex.getGame(p).leave(p);
								p.sendMessage(ChatColor.YELLOW + "You quit the game");
								return true;
							} else {
								p.sendMessage(ChatColor.YELLOW + "You are not in a game");
							}
						} else {
							sender.sendMessage("Usage: /game <join/leave>");
						}
					} else {
						sender.sendMessage("Usage: /game <join/leave>");
					}
				} else {
					sender.sendMessage("The plugin is disabled right now");
				}
			} else {
				sender.sendMessage("This command can only be used as a player");
			}
		} else {
			sender.sendMessage("You don't have the permission to do this command");
		}
		return false;
	}

}
