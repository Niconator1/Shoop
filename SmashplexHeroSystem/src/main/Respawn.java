package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import util.SoundUtil;
import util.TextUtil;

public class Respawn {
	private Player p;
	private Location pos;
	private Game game;
	private boolean smash = false;

	public Respawn(Game g, Player player, Location respawn) {
		this.game = g;
		this.p = player;
		this.pos = respawn;
	}

	public Player getPlayer() {
		return p;
	}

	int x = 0;
	int y = 8;

	public void startCountdown() {
		y = 8;
		x = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class),
				new Runnable() {
					public void run() {
						Location neu = pos;
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
								sp.preparePlayer(3);
							}
							if (smash) {
								p.getInventory().setItem(2, sp.getSelectedHero().getSmash(1.0));
								TextUtil.sendSubTitle(p,
										ChatColor.GREEN + "SMASH READY! " + ChatColor.AQUA + "Press [3]");
								TextUtil.sendTitleTime(p, 0, 25, 5);
								SoundUtil.sendSoundPacket(p, "mob.wither.spawn", p.getLocation(), 2f);
							}
							game.removeRespawn(p);
							Bukkit.getServer().getScheduler().cancelTask(x);
						}
					}
				}, 0L, 20L);
	}

	public void setSmashEffect(boolean b) {
		this.smash = b;
	}

}
