package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import util.SoundUtil;
import util.TextUtil;

public class Game {
	private Map map;
	private ArrayList<Player> list = new ArrayList<Player>();
	private ArrayList<Respawn> rlist = new ArrayList<Respawn>();
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
		if (isFull() && !isRunning()) {
			start();
			p.setVelocity(new Vector(0, 0, 0));
			Location neu = map.getSpawnPositions()[list.size() - 1].clone();
			p.teleport(neu);
			SoundUtil.sendSoundPacket(p, "announcer.joingame", neu);
		} else {
			p.teleport(map.getLobbyLocation());
			SoundUtil.sendSoundPacket(p, "announcer.joingame", p.getLocation());
		}
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
						sp.resetHero(false, true);
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
		for (int i = 0; i < list.size(); i++) {
			Player p = list.get(i);
			SmashPlayer sp = Smashplex.getSmashPlayer(p);
			if (sp != null) {
				sp.preparePlayer(2);
			}
		}
		y = 15;
		x = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class),
				new Runnable() {
					public void run() {
						if (list.size() < map.getMaxPlayerCount()) {
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								p.teleport(map.getLobbyLocation());
								SmashPlayer sp = Smashplex.getSmashPlayer(p);
								if (sp != null) {
									sp.preparePlayer(1);
								}
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
							if (y == 5) {
								SoundUtil.sendPublicSoundPacket("announcer.startgame", 1.0f);
							}
							y--;
						} else {
							sendGameMessage(ChatColor.YELLOW + "The game has started");
							countdowndone = true;
							for (int i = 0; i < list.size(); i++) {
								Player p = list.get(i);
								SmashPlayer sp = Smashplex.getSmashPlayer(p);
								if (sp != null) {
									sp.preparePlayer(4);
								}
								SoundUtil.sendSoundPacket(p, "note.pling", p.getLocation(), 4.047619f);
								SoundUtil.sendSoundPacket(p, "note.pling", p.getLocation(), 4.047619f);
								SoundUtil.sendSoundPacket(p, "note.pling", p.getLocation(), 4.047619f);
								SoundUtil.sendSoundPacket(p, "note.pling", p.getLocation(), 4.047619f);
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

	public void hasDied(SmashPlayer sp) {
		Player p = sp.getPlayer();
		sp.setLives(sp.getLives() - 1);
		if (sp.getLives() > 0) {
			if (sp != null) {
				sp.preparePlayer(1);
			}
			p.setVelocity(new Vector(0, 0, 0));
			Location respawn = map.getRandomSpawnPosition().clone();
			p.teleport(respawn.clone().add(0, 1, 0));
			SoundUtil.sendSoundPacket(p, "mob.endermen.portal", respawn, 0f);
			Respawn r = new Respawn(this, p, respawn);
			r.startCountdown();
			rlist.add(r);
		} else {
			// TODO: Add infinite spectation until game ends
			sp.resetHero(false, false);
			p.teleport(map.getLobbyLocation());
			SoundUtil.sendSoundPacket(p, "mob.endermen.portal", map.getLobbyLocation(), 0f);
		}
	}

	public Respawn getRespawn(Player p) {
		for (int i = 0; i < rlist.size(); i++) {
			if (rlist.get(i).getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				return rlist.get(i);
			}
		}
		return null;
	}

	public Map getMap() {
		return map;
	}

	public void removeRespawn(Player p) {
		for (int i = 0; i < rlist.size(); i++) {
			if (rlist.get(i).getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				rlist.remove(i);
			}
		}
	}
}
