package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import util.TextUtil;

public class Game {
	private Map map;
	private ArrayList<Player> list = new ArrayList<Player>();
	private boolean countdowndone = false;
	private boolean hasStarted = false;

	public Game(Map map) {
		this.map = map;
	}

	public void join(Player p) {
		list.add(p);
		sendGameMessage(p.getName() + ChatColor.YELLOW + " has joined (" + ChatColor.AQUA + list.size()
				+ ChatColor.YELLOW + "/" + ChatColor.AQUA + map.getMaxPlayerCount() + ChatColor.YELLOW + ")");
		p.teleport(map.getLobbyLocation());
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

	public String getMapName() {
		return map.getName();
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
							if (list.size() < map.getMaxPlayerCount()) {
								sendGameMessage(ChatColor.RED + "Cancelling starting countdown, not enough players!");
								hasStarted = false;
								Bukkit.getServer().getScheduler().cancelTask(x);
							}
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								TextUtil.sendTitle(p, ChatColor.YELLOW + "Game starting in...");
								TextUtil.sendSubTitle(p, ChatColor.YELLOW + "" + y);
								TextUtil.sendTitleTime(p, 0, 25, 5);
							}
							y--;
						} else {
							if (list.size() < map.getMaxPlayerCount()) {
								sendGameMessage(ChatColor.RED + "Cancelling starting countdown, not enough players!");
								hasStarted = false;
							} else {
								sendGameMessage(ChatColor.YELLOW + "The game has started");
								countdowndone = true;
								for (int i = 0; i < list.size(); i++) {
									Player p = list.get(i);
									p.teleport(map.getSpawnPositions()[i]);
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
		if (list.size() >= map.getMaxPlayerCount()) {
			return true;
		}
		return false;
	}
}
