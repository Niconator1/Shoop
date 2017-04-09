package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import util.TextUtil;

public class Respawn {
	private Player p;
	private Location pos;

	public Respawn(Player player, Location respawn) {
		this.p = player;
		this.pos = respawn;
	}

	int x = 0;
	int y = 8;

	public void startCountdown() {
		y = 8;
		x = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class),
				new Runnable() {
					public void run() {
						Location neu = pos.clone();
						neu.setYaw(p.getLocation().getYaw());
						neu.setPitch(p.getLocation().getPitch());
						p.teleport(neu);
						p.setVelocity(new Vector(0, 0, 0));
						if (y > 0) {
							p.setWalkSpeed(0.0f);
						} else {
							p.setWalkSpeed(0.4f);
						}
						if (y > 0) {
							TextUtil.sendTitle(p, ChatColor.RED + "You died!");
							ChatColor time;
							if (y > 5) {
								time = ChatColor.GREEN;
							} else if (y > 2) {
								time = ChatColor.YELLOW;
							} else {
								time = ChatColor.RED;
							}
							TextUtil.sendSubTitle(p, ChatColor.BLUE + "Respawning in... " + time + y);
							TextUtil.sendTitleTime(p, 0, 25, 5);
							y--;
						} else {
							TextUtil.sendTitle(p, "");
							TextUtil.sendSubTitle(p, "");
							TextUtil.sendTitleTime(p, 0, 25, 5);
							SmashPlayer sp = Smashplex.getSmashPlayer(p);
							if (sp != null) {
								sp.preparePlayer(2);
							}
							Bukkit.getServer().getScheduler().cancelTask(x);
						}
					}
				}, 0L, 20L);
	}

}
