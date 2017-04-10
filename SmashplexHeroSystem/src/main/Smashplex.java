package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import abilities.Botmobile;
import abilities.Cooldown;
import abilities.Grenade;
import abilities.Lightningbolt;
import abilities.ShoopLazor;
import heroes.HeroLoops;
import heroes.Skullfire;
import util.SoundUtil;
import util.TextUtil;

public class Smashplex extends JavaPlugin {
	public static ArrayList<SmashPlayer> players = new ArrayList<SmashPlayer>();
	public static ArrayList<Lightningbolt> bolt = new ArrayList<Lightningbolt>();
	public static ArrayList<ShoopLazor> lazor = new ArrayList<ShoopLazor>();
	public static ArrayList<Grenade> nade = new ArrayList<Grenade>();
	public static ArrayList<Cooldown> cooldown = new ArrayList<Cooldown>();
	public static ArrayList<NPC> npcs = new ArrayList<NPC>();
	public static ArrayList<Botmobile> bm = new ArrayList<Botmobile>();
	public static ArrayList<Game> gamelist = new ArrayList<Game>();
	public static ArrayList<Map> maplist = new ArrayList<Map>();
	public static boolean smash = true;
	public static Objective obj;
	public static Team team;
	public static NetworkManager nmanager;

	public void onEnable() {
		this.getLogger().info("Smashplex enabled");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		loopsSmash();
		HeroLoops.loopsBotmon();
		HeroLoops.loopsSkullfire();
		HeroLoops.loopsShoop();
		SmashCommands.registerCommands();
		MapRegistry.registerMaps();
		registerLobbyNPCs();
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
			nmanager = new NetworkManager();
			nmanager.registerNetworkHandling();
		} else {
			this.getLogger().warning("Some features will not work without ProtocolLib");
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			SmashPlayer sp = new SmashPlayer(p, 100);
			if (!sp.firstJoin()) {
				sp.readData();
			}
			Smashplex.players.add(sp);
			for (int i = 0; i < npcs.size(); i++) {
				NPC n = npcs.get(i);
				if (p.getLocation().getWorld().getUID().compareTo(n.getLocation().getWorld().getUID()) == 0
						&& p.getLocation().distance(n.getLocation()) < 100) {
					n.spawn(p, false);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Smashplex.class),
							new Runnable() {
								public void run() {
									n.rmvFromTablist(p);
								}
							}, 100);
				}
			}
		}
	}

	public void onDisable() {
		this.getLogger().info("Smashplex disabled");
		for (int i = 0; i < players.size(); i++) {
			SmashPlayer sp = players.get(i);
			sp.saveData();
		}
		for (int i = 0; i < bolt.size(); i++) {
			bolt.get(i).getStand().die();
		}
		for (int i = 0; i < bm.size(); i++) {
			bm.get(i).getStand().die();
		}
		for (int i = 0; i < nade.size(); i++) {
			nade.get(i).getStand().die();
		}
		for (int i = 0; i < lazor.size(); i++) {
			lazor.get(i).finish();
		}
		for (int i = 0; i < npcs.size(); i++) {
			NPC n = npcs.get(i);
			n.destroyGlobal();
		}
		for (int i = 0; i < gamelist.size(); i++) {
			Game g = gamelist.get(i);
			g.disband();
		}
	}

	private void registerLobbyNPCs() {
		if (team == null) {
			if (Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("-1993870687") != null) {
				team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("-1993870687");
			} else {
				team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("-1993870687");
			}
			team.setNameTagVisibility(NameTagVisibility.NEVER);
		}
		NPCRegistry.registerShoop();
		NPCRegistry.registerSkullfire();
	}

	public static SmashPlayer getSmashPlayer(Player p) {
		for (int i = 0; i < players.size(); i++) {
			SmashPlayer sp = players.get(i);
			if (sp.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				return sp;
			}
		}
		return null;
	}

	private void loopsSmash() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				// Cooldown decrease
				for (int i = 0; i < cooldown.size(); i++) {
					Cooldown c = cooldown.get(i);
					c.setTicks(c.getTicks() - 1);
					Player p = c.getPlayer();
					if (c.getSkill() == 0) {
						if (p.isOnline()) {
							TextUtil.sendCooldownMessage(c);
						}
					} else if (c.getSkill() == 1) {
						if (p.isOnline()) {
							SmashPlayer sp = getSmashPlayer(p);
							if (sp != null) {
								if (sp.getSelectedHero() != null) {
									Game g = getGame(p);
									if (g != null && g.hasBegan()) {
										Respawn r = g.getRespawn(p);
										if (r == null) {
											p.getInventory().setItem(1, sp.getSelectedHero().getSecondary(
													1.0 - (double) c.getTicks() / (double) c.getMaxTicks()));
										}
									}
								}
							}
						}
					} else if (c.getSkill() == 2) {
						if (p.isOnline()) {
							SmashPlayer sp = getSmashPlayer(p);
							if (sp != null) {
								if (sp.getSelectedHero() != null) {
									Game g = getGame(p);
									if (g != null && g.hasBegan()) {
										Respawn r = g.getRespawn(p);
										if (r == null) {
											p.getInventory().setItem(2, sp.getSelectedHero()
													.getSmash(1.0 - (double) c.getTicks() / (double) c.getMaxTicks()));
										}
									}
								}
							}
						}
					}
					if (c.getTicks() < 0) {
						if (c.getSkill() == 0) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero().getNumber() == 1) {
										Skullfire s = (Skullfire) sp.getSelectedHero();
										s.setBullets(7);
										Game g = getGame(p);
										if (g != null && g.hasBegan()) {
											Respawn r = g.getRespawn(p);
											if (r == null) {
												p.getInventory().setItem(0, s.getPrimary(s.getBullets()));
											}
										}
									}
								}
							}
						} else if (c.getSkill() == 1) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != null) {
										Game g = getGame(p);
										if (g != null && g.hasBegan()) {
											Respawn r = g.getRespawn(p);
											if (r == null) {
												p.getInventory().setItem(1, sp.getSelectedHero().getSecondary(1.0));
											}
										}
									}
								}
							}
						} else if (c.getSkill() == 2) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != null) {
										Game g = getGame(p);
										if (g != null && g.hasBegan()) {
											Respawn r = g.getRespawn(p);
											if (r != null) {
												r.setSmashEffect(true);
											} else {
												p.getInventory().setItem(2, sp.getSelectedHero().getSmash(1.0));
												TextUtil.sendSubTitle(p, ChatColor.GREEN + "SMASH READY! "
														+ ChatColor.AQUA + "Press [3]");
												TextUtil.sendTitleTime(p, 0, 25, 5);
												SoundUtil.sendSoundPacket(p, "mob.wither.spawn", p.getLocation(), 2f);
											}
										}
									}
								}
							}
						}
						cooldown.remove(i);
					}
				}
			}
		}, 0, 1);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					SmashPlayer sp = getSmashPlayer(p);
					if (sp != null) {
						Game g = getGame(p);
						if (g != null && g.hasBegan() && sp.getLives() > 0) {
							if (sp.getSelectedHero() != null) {
								if (sp.getSelectedHero().getNumber() != 1) {
									if (p.getLevel() < 100) {
										p.setLevel(p.getLevel() + 1);
										float pro = p.getLevel() / (100 + 0.000001f);
										p.setExp(pro);
									}
								}
							}
						}
					}
				}
			}
		}, 0, 2);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				// Player Health
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (obj == null) {
						if (Bukkit.getServer().getScoreboardManager().getMainScoreboard()
								.getObjective("health") != null) {
							obj = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("health");
						} else {
							obj = Bukkit.getServer().getScoreboardManager().getMainScoreboard()
									.registerNewObjective("health", "dummy");
						}
						obj.setDisplayName(ChatColor.RED + "❤");
						obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
					}
					SmashPlayer sp = getSmashPlayer(p);
					if (sp != null) {
						if (sp.getSelectedHero() != null) {
							obj.getScore(p.getName()).setScore((int) Math.round(sp.getHP()));
						} else {
							obj.getScore(p.getName()).setScore(100);
						}
					}
				}
			}
		}, 0, 1);
	}

	public static boolean isPrimaryReady(Player p) {
		for (int i = 0; i < cooldown.size(); i++) {
			Cooldown c = cooldown.get(i);
			if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				if (c.getSkill() == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isSecondaryReady(Player p) {
		for (int i = 0; i < cooldown.size(); i++) {
			Cooldown c = cooldown.get(i);
			if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				if (c.getSkill() == 1) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isSmashReady(Player p) {
		for (int i = 0; i < cooldown.size(); i++) {
			Cooldown c = cooldown.get(i);
			if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				if (c.getSkill() == 2) {
					return false;
				}
			}
		}
		return true;
	}

	public static Game getGame(Player p) {
		for (int i = 0; i < gamelist.size(); i++) {
			Game g = gamelist.get(i);
			if (g.hasPlayer(p)) {
				return g;
			}
		}
		return null;
	}

	public static Game getFreeGame() {
		for (int i = 0; i < gamelist.size(); i++) {
			Game g = gamelist.get(i);
			if (!g.isFull() && !g.isRunning()) {
				return g;
			}
		}
		return null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (SmashCommands.handleCommand(sender, cmd, args)) {
			return true;
		}
		return false;
	}

}
