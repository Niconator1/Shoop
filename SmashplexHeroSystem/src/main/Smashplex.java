package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import heroes.Shoop;
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
	public static boolean smash = true;
	public static Objective obj;
	public static Team team;

	public void onEnable() {
		this.getLogger().info("Smashplex enabled");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		loopsSmash();
		HeroLoops.loopsBotmon();
		HeroLoops.loopsSkullfire();
		HeroLoops.loopsShoop();
		SmashCommands.registerCommands();
		registerLobbyNPCs();
		for (Player p : Bukkit.getOnlinePlayers()) {
			SmashPlayer sp = new SmashPlayer(p, 100);
			Smashplex.players.add(sp);
			for (int i = 0; i < npcs.size(); i++) {
				NPC n = npcs.get(i);
				n.spawnGlobal();
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Smashplex.class),
						new Runnable() {
							public void run() {
								n.rmvFromTablist();
							}
						}, 100);
			}
		}
	}

	public void onDisable() {
		this.getLogger().info("Smashplex disabled");
		for (int i = 0; i < players.size(); i++) {
			SmashPlayer sp = players.get(i);
			if (sp.getSelectedHero() != null) {
				sp.resetHero();
				for (int j = 0; j < cooldown.size(); j++) {
					Cooldown c = cooldown.get(j);
					if (c.getPlayer().getUniqueId().compareTo(sp.getPlayer().getUniqueId()) == 0) {
						cooldown.remove(j);
					}
				}
			}
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
		NPC shoop = new NPC("i13u1i3u12i3u1",
				new Location(Bukkit.getWorld("lobby"), 839.5, 39.5, 62.5, 82.96875f, 7.03125f), 82.96875f,
				"eyJ0aW1lc3RhbXAiOjE0NDMyMzE4ODgzNDksInByb2ZpbGVJZCI6ImFhZDIzYTUwZWVkODQ3MTA5OWNmNjRiZThmZjM0ZWY0IiwicHJvZmlsZU5hbWUiOiIxUm9ndWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UwMjE2MTJlMjEwNDZkNDY4OGIyMjhjYjM3MDJiYjZhZWM3YTA4Yjc4YjBmNTYwMTljMDRmYWUyYWQyOTQyZCJ9fX0=",
				"yRVtcYcMOKmDtUkY0c1ruzkJQVswrCExRrG8He5qKUltUT1RI+CMlhjovdEfxfwXRXWQz9BTmfrVlhEwBu7NIfWfimj8twotmGgcJ4ZLSGlkyc/VMwrIMUKzZowvIObQqEWenBQYW3uSfYKFStyi6jCzNOmY4fkCc3VVPBGKKdXOVWo9vGVg63tUjLJZlBSL9r0Cr6IUn7lAQg7cGwGxyikh6/B6tCrpr8/ssJJWJnyDw/rcwnbALJfZVvxAEipA3qA47u3FUntN0CNQIzdVi/y+nNRH+jmyDmV5CSDGELCODrXF1ll/R5gYKn3ZJuSBuY6PjdvpDBr9EeKhD7EFNjTrMqIUoWu+KX6hzwA8DVK2EPn4ZXk31siHQz1L6vd37Y8s8V5fqYj0293frhJ3aiOoUhH5tmfcpV0k7Vdpp2RQVdJk+cHO3t0I6vvroNKoD+pbk8FqA1BxLH4oM8fX4J6Dfk0jFTGSDFdoIp2As8q34ZZrZoTSOf36Hm8tAmXNi0cqxANwQ5zyA9467MDLncTcCWn1NsU1W7KPBBY7mQf4VOhi2zFYKdB2rYMbxbbQ2dGWdUcZFqPmj2DLx/k0HkNiEVOZyxnvuKWydA+L+T+36zHFbs3mo5P414/yI/Zwwxx0xRhE1FIorqj2OphSkmMS4wBzLt96tJjAsaV8af8=");
		Shoop npc = new Shoop(null, false);
		team.addEntry("i13u1i3u12i3u1");
		shoop.setItemStack(0, npc.getPrimary(0));
		shoop.setItemStack(1, npc.getBoots());
		shoop.setItemStack(2, npc.getLeggings());
		shoop.setItemStack(3, npc.getChestplate());
		shoop.setItemStack(4, npc.getHelmet());
		npcs.add(shoop);
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
									p.getInventory().setItem(1, sp.getSelectedHero()
											.getSecondary(1.0 - (double) c.getTicks() / (double) c.getMaxTicks()));
								}
							}
						}
					} else if (c.getSkill() == 2) {
						if (p.isOnline()) {
							SmashPlayer sp = getSmashPlayer(p);
							if (sp != null) {
								if (sp.getSelectedHero() != null) {
									p.getInventory().setItem(2, sp.getSelectedHero()
											.getSmash(1.0 - (double) c.getTicks() / (double) c.getMaxTicks()));
								}
							}
						}
					}
					if (c.getTicks() < 0) {
						if (c.getSkill() == 0) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero().getNumber() == 0) {
										Shoop s = (Shoop) sp.getSelectedHero();
										s.setCharges(99);
										p.getInventory().setItem(0, s.getPrimary(s.getCharges()));
									}
								}
							}
						} else if (c.getSkill() == 1) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != null) {
										p.getInventory().setItem(1, sp.getSelectedHero().getSecondary(1.0));
									}
								}
							}
						} else if (c.getSkill() == 2) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != null) {
										p.getInventory().setItem(2, sp.getSelectedHero().getSmash(1.0));
										TextUtil.sendSubTitle(p,
												ChatColor.GREEN + "SMASH READY! " + ChatColor.AQUA + "Press [3]", 0, 30,
												0);
										SoundUtil.sendSoundPacket(p, "mob.wither.spawn", p.getLocation(), 2f);
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

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (SmashCommands.handleCommand(sender, cmd, args)) {
			return true;
		}
		return false;
	}

}
