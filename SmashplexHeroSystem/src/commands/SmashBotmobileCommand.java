package commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Botmobile;
import main.SmashPlayer;
import main.Smashplex;
import net.minecraft.server.v1_8_R3.WorldServer;
import util.SoundUtil;

public class SmashBotmobileCommand extends SCommand {

	public SmashBotmobileCommand(String name, String permission) {
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
						double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
						double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
						double x = Math.sin(pitch) * Math.cos(yaw);
						double y = Math.cos(pitch);
						double z = Math.sin(pitch) * Math.sin(yaw);
						Vector v = new Vector(x, y, z).multiply(0.911625);
						Location l = p.getEyeLocation();
						SoundUtil.sendPublicSoundPacket("Botmun.crystal", p.getLocation());
						WorldServer s = ((CraftWorld) l.getWorld()).getHandle();
						ArmorStandM fn = new ArmorStandM(s, 0);
						fn.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
						CraftArmorStand an = (CraftArmorStand) fn.getBukkitEntity();
						fn.motX = v.getX();
						fn.motY = v.getY();
						fn.motZ = v.getZ();
						an.setVisible(false);
						an.setHelmet(new ItemStack(Material.MYCEL));
						s.addEntity(fn);
						an.setPassenger(p);
						p.getLocation().setPitch(0);
						Botmobile botmobile = new Botmobile(fn, p);
						Smashplex.bm.add(botmobile);
						return true;
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
