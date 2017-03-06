package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import util.TextUtil;

public class Game {
	private String map = "";
	private ArrayList<Player> list = new ArrayList<Player>();
	private boolean countdowndone = false;
	private boolean hasStarted = false;
	private int maxplayers;
	private Location[] pos = { new Location(Bukkit.getWorld("Kingdom"), 1, 62, -21),
			new Location(Bukkit.getWorld("Kingdom"), 25, 63, -2), new Location(Bukkit.getWorld("Kingdom"), -1, 63, -22),
			new Location(Bukkit.getWorld("Kingdom"), -24, 63, 4) };

	public Game(String map, int maxplayers) {
		this.map = map;
		this.maxplayers = maxplayers;
	}

	public void join(Player p) {
		list.add(p);
		sendGameMessage(p.getName() + ChatColor.YELLOW + " has joined (" + ChatColor.AQUA + list.size()
				+ ChatColor.YELLOW + "/" + ChatColor.AQUA + maxplayers + ChatColor.YELLOW + ")");
		p.teleport(new Location(Bukkit.getWorld("Kingdom"), 0, 84, 0));
	}

	private void sendGameMessage(String string) {
		for (int i = 0; i < list.size(); i++) {
			Player t = list.get(i);
			t.sendMessage(string);
		}
	}

	public void leave(Player p) {
		for (int i = 0; i < list.size(); i++) {
			Player t = list.get(i);
			if (t.getUniqueId().compareTo(p.getUniqueId()) == 0) {
				list.remove(i);
				sendGameMessage(p.getName() + ChatColor.YELLOW + " has quit");
				t.teleport(Bukkit.getWorld("lobby").getSpawnLocation());
			}
		}
	}

	public int getMaxPlayers() {
		return maxplayers;
	}

	public String getMapName() {
		return map;
	}

	int x = 0;
	int y = 10;

	public void start() {
		this.hasStarted = true;
		y = 10;
		x = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class),
				new Runnable() {
					public void run() {
						if (y > 0) {
							if (list.size() < maxplayers) {
								sendGameMessage(ChatColor.RED + "Cancelling starting countdown, not enough players!");
								hasStarted = false;
								Bukkit.getServer().getScheduler().cancelTask(x);
							}
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								TextUtil.sendTitle(p, ChatColor.YELLOW + "Game starting in...", 0, 20, 0);
								TextUtil.sendSubTitle(p, ChatColor.YELLOW + "" + y, 0, 20, 0);
							}
							y--;
						} else {
							if (list.size() < maxplayers) {
								sendGameMessage(ChatColor.RED + "Cancelling starting countdown, not enough players!");
								hasStarted = false;
							} else {
								sendGameMessage(ChatColor.YELLOW + "The game has started");
								countdowndone = true;
								for (int i = 0; i < list.size(); i++) {
									Player p = list.get(i);
									p.teleport(pos[i]);
								}
							}

							Bukkit.getServer().getScheduler().cancelTask(x);
						}
					}
				}, 0L, 20L);
	}

	public boolean isRunning() {
		return this.hasStarted;
	}

	public boolean hasBegan() {
		return this.countdowndone;
	}

	public boolean hasPlayer(Player p) {
		for (int i = 0; i < list.size(); i++) {
			Player t = list.get(i);
			if (t.getUniqueId().compareTo(p.getUniqueId()) == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isFull() {
		if (list.size() == maxplayers) {
			return true;
		}
		return false;
	}
}
