package commands;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import heroes.Hero;
import main.Smashplex;

public class SmashArmorCommand extends SCommand {

	public SmashArmorCommand(String name, String permission) {
		super(name, permission);
	}

	@Override
	public boolean handle(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			if (Smashplex.smash) {
				Player p = (Player) sender;
				if (sender.hasPermission(getPermission())) {
					if (args.length > 2) {
						try {
							int red = Integer.parseInt(args[0]);
							int green = Integer.parseInt(args[1]);
							int blue = Integer.parseInt(args[2]);
							if (red >= 0 && red < 256 && blue >= 0 && blue < 256 && green >= 0 && green < 256) {
								ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
								Color c = Color.fromBGR(blue, green, red);
								is = Hero.addColor(is, c);
								ItemMeta im = is.getItemMeta();
								im.setDisplayName(ChatColor.AQUA + "Color: " + c.asRGB());
								is.setItemMeta(im);
								p.getInventory().addItem(is);
								return true;
							} else {
								sender.sendMessage("You can only use numbers between 0 and 255");
							}
						} catch (NumberFormatException e) {
							sender.sendMessage("Not a valid number");
						}
					} else {
						sender.sendMessage("Usage: /armor <red> <green> <blue>");
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
