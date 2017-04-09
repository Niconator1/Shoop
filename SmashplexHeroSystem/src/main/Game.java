package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

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
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			sp.preparePlayer(1);
		}
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
				SmashPlayer sp = Smashplex.getSmashPlayer(p);
				if (sp != null) {
					if (sp.getSelectedHero() != null) {
						sp.resetHero(false);
					}
				}
			}
		}
	}

	public String getMapName() {
		return map.getName();
	}

	int x = 0;
	int y = 15;

	public void start() {
		this.hasStarted = true;
		y = 15;
		x = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class),
				new Runnable() {
					public void run() {
						if (list.size() < map.getMaxPlayerCount()) {
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								p.teleport(map.getLobbyLocation());
							}
							sendGameMessage(ChatColor.RED + "Cancelling starting countdown, not enough players!");
							hasStarted = false;
							Bukkit.getServer().getScheduler().cancelTask(x);
							return;
						}
						for (int i = 0; i < list.size(); i++) {
							Player p = list.get(i);
							Location neu = map.getSpawnPositions()[i].clone();
							neu.setYaw(p.getLocation().getYaw());
							neu.setPitch(p.getLocation().getPitch());
							p.teleport(neu);
							p.setVelocity(new Vector(0, 0, 0));
							if (y > 0) {
								p.setWalkSpeed(0.0f);
							} else {
								p.setWalkSpeed(0.4f);
							}
						}
						if (y > 0) {
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								TextUtil.sendTitle(p, ChatColor.YELLOW + "Game starting in...");
								ChatColor time;
								if (y > 5) {
									time = ChatColor.GREEN;
								} else if (y > 2) {
									time = ChatColor.YELLOW;
								} else {
									time = ChatColor.RED;
								}
								TextUtil.sendSubTitle(p, time + "" + y);
								TextUtil.sendTitleTime(p, 0, 25, 5);
							}
							y--;
						} else {
							sendGameMessage(ChatColor.YELLOW + "The game has started");
							countdowndone = true;
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								SmashPlayer sp = Smashplex.getSmashPlayer(p);
								if (sp != null) {
									sp.preparePlayer(2);
								}
								TextUtil.sendTitle(p, ChatColor.GREEN + "GO!");
								TextUtil.sendSubTitle(p, ChatColor.YELLOW + "Knock off or kill other players to win!");
								TextUtil.sendTitleTime(p, 0, 25, 5);
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

	public void disband() {
		for (int i = 0; i < list.size(); i++) {
			Player p = list.get(i);
			leave(p);
		}
	}
}
