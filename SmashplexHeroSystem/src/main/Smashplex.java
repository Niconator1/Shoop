package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Cooldown;
import abilities.Grenade;
import abilities.Lightningbolt;
import abilities.ShoopLazor;
import heroes.Shoop;
import net.minecraft.server.v1_8_R3.EnumParticle;
import util.KnockbackUtil;
import util.ParticleUtil;
import util.SoundUtil;
import util.TextUtil;

public class Smashplex extends JavaPlugin {
	public static ArrayList<SmashPlayer> players = new ArrayList<SmashPlayer>();
	public static ArrayList<Lightningbolt> bolt = new ArrayList<Lightningbolt>();
	public static ArrayList<ShoopLazor> lazor = new ArrayList<ShoopLazor>();
	public static ArrayList<Grenade> nade = new ArrayList<Grenade>();
	public static ArrayList<Cooldown> cooldown = new ArrayList<Cooldown>();
	public static ArrayList<NPC> npcs = new ArrayList<NPC>();
	public static boolean smash = true;
	public static double knockback = 0.125;
	public static Objective obj;
	public static Team team;

	public void onEnable() {
		this.getLogger().info("Smashplex enabled");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		loopsSmash();
		loopsSkullfire();
		loopsShoop();
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
		Shoop npc = new Shoop();
		team.addEntry("i13u1i3u12i3u1");
		shoop.setItemStack(0, npc.getPrimary(0));
		shoop.setItemStack(1, npc.getBoots());
		shoop.setItemStack(2, npc.getLeggings());
		shoop.setItemStack(3, npc.getChestplate());
		shoop.setItemStack(4, npc.getHelmet());
		npcs.add(shoop);
	}

	public void onDisable() {
		this.getLogger().info("Smashplex disabled");
		for (int i = 0; i < players.size(); i++) {
			SmashPlayer sp = players.get(i);
			if (sp.getSelectedHero() != -1) {
				sp.removeHeroItems();
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

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("smash")) {
			if (sender.hasPermission("smash.admin")) {
				if (args.length > 0) {
					if (args[0].equals("enable")) {
						smash = true;
						sender.sendMessage("Smash enabled");
						return true;
					} else if (args[0].equals("disable")) {
						smash = false;
						sender.sendMessage("Smash disabled");
						return true;
					} else {
						sender.sendMessage("Usage: /smash <enable/disable>");
					}
				} else {
					sender.sendMessage("Usage: /smash <enable/disable>");
				}
			} else {
				sender.sendMessage("You don't have the permission to do this command");
			}

		} else if (cmd.getName().equalsIgnoreCase("setkb")) {
			if (sender.hasPermission("smash.admin")) {
				if (args.length > 0) {
					try {
						double value = Double.parseDouble(args[0]);
						knockback = value;
						sender.sendMessage("Upward knockback set to " + value);
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage("Not a valid number");
					}
				} else {
					sender.sendMessage("Usage: /setkb <value>");
				}
			} else {
				sender.sendMessage("You don't have the permission to do this command");
			}
		} else if (cmd.getName().equalsIgnoreCase("armor")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (sender.hasPermission("smash.util")) {
					if (args.length > 2) {
						try {
							int red = Integer.parseInt(args[0]);
							int green = Integer.parseInt(args[1]);
							int blue = Integer.parseInt(args[2]);
							ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
							Color c = Color.fromBGR(blue, green, red);
							is = Hero.addColor(is, c);
							ItemMeta im = is.getItemMeta();
							im.setDisplayName(ChatColor.AQUA + "Color: " + c.asRGB());
							is.setItemMeta(im);
							p.getInventory().addItem(is);
							return true;
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
				sender.sendMessage("This command can only be used as a player");
			}
		} else if (cmd.getName().equalsIgnoreCase("removehero")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				SmashPlayer sp = getSmashPlayer(p);
				if (sp != null) {
					if (sp.getSelectedHero() != -1) {
						sp.removeHeroItems();
						for (int i = 0; i < cooldown.size(); i++) {
							Cooldown c = cooldown.get(i);
							if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
								cooldown.remove(i);
							}
						}
					}
					sp.setSelectedHero(-1);
					sender.sendMessage("Your selected hero was reseted");
				} else {
					sender.sendMessage("Error #0 please contact NiconatorTM");
				}
			} else {
				sender.sendMessage("This command can only be used as a player");
			}
		} else if (cmd.getName().equalsIgnoreCase("shoop")) {
			if (sender instanceof Player) {
				if (smash) {
					Player p = (Player) sender;
					SmashPlayer sp = getSmashPlayer(p);
					if (sp != null) {
						if (sp.getSelectedHero() == -1) {
							sp.setSelectedHero(0);
							sp.giveHeroItems();
							SoundUtil.sendSoundPacket(p, "ShoopDaWhoop.select", p.getLocation());
							sender.sendMessage("You selected shoop");
						} else {
							sender.sendMessage("You already have a hero selected");
						}
					} else {
						sender.sendMessage("Error #0 please contact NiconatorTM");
					}
				} else {
					sender.sendMessage("The plugin is disabled right now");
				}
			} else {
				sender.sendMessage("This command can only be used as a player");
			}
		} else if (cmd.getName().equalsIgnoreCase("skullfire")) {
			if (sender instanceof Player) {
				if (smash) {
					Player p = (Player) sender;
					SmashPlayer sp = getSmashPlayer(p);
					if (sp != null) {
						if (sp.getSelectedHero() == -1) {
							sp.setSelectedHero(1);
							sp.setCharges(99);
							sp.giveHeroItems();
							SoundUtil.sendSoundPacket(p, "Skullfire.select", p.getLocation());
							Cooldown c = new Cooldown(p, 0, -1);
							Cooldown c2 = new Cooldown(p, 1, -1);
							cooldown.add(c2);
							TextUtil.sendCooldownMessage(c);
							sender.sendMessage("You selected skullfire");
						} else {
							sender.sendMessage("You already have a hero selected");
						}
					} else {
						sender.sendMessage("Error #0 please contact NiconatorTM");
					}
				} else {
					sender.sendMessage("The plugin is disabled right now");
				}
			} else {
				sender.sendMessage("This command can only be used as a player");
			}
		}
		return false;
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
								if (sp.getSelectedHero() != -1) {
									p.getInventory().setItem(1, sp.getHero()
											.getSecondary(1.0 - (double) c.getTicks() / (double) c.getMaxTicks()));
								}
							}
						}
					} else if (c.getSkill() == 2) {
						if (p.isOnline()) {
							SmashPlayer sp = getSmashPlayer(p);
							if (sp != null) {
								if (sp.getSelectedHero() != -1) {
									p.getInventory().setItem(2, sp.getHero()
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
									if (sp.getSelectedHero() == 1) {
										sp.setCharges(99);
										p.getInventory().setItem(0, sp.getHero().getPrimary(sp.getCharges()));
									}
								}
							}
						} else if (c.getSkill() == 1) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != -1) {
										p.getInventory().setItem(1, sp.getHero().getSecondary(1.0));
									}
								}
							}
						} else if (c.getSkill() == 2) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != -1) {
										p.getInventory().setItem(2, sp.getHero().getSmash(1.0));
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
						if (sp.getSelectedHero() != -1) {
							if (sp.getSelectedHero() != 1) {
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
						if (sp.getSelectedHero() != -1) {
							obj.getScore(p.getName()).setScore((int) Math.round(sp.getHP()));
						} else {
							obj.getScore(p.getName()).setScore(100);
						}
					}
				}
			}
		}, 0, 1);

	}

	private void loopsShoop() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				// Smash
				for (int i = 0; i < lazor.size(); i++) {
					ShoopLazor sl = lazor.get(i);
					if (sl.getTicks() > 0) {
						sl.handle();
					}
				}
			}
		}, 0, 2);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				// Block collision check
				for (int i = 0; i < bolt.size(); i++) {
					Lightningbolt b = bolt.get(i);
					ArmorStandM standn = b.getStand();
					CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
					Location mid = stand.getEyeLocation();
					double bonuslengthf = 0.75;
					if (b.isPassive()) {
						bonuslengthf += 0.5;
					}
					double bonuslengthb = -0.5;// -1.5
					for (double j = bonuslengthf; j >= bonuslengthb; j -= 0.05) {
						Vector dir = b.getVector().clone().normalize().multiply(j);
						Location midm = mid.clone().add(dir);
						Block c = midm.getBlock();
						if (c.getType().isSolid()) {
							if (c.getType() == Material.STEP) {
								if (midm.getY() % midm.getBlockY() < 0.5) {
									if (b.isPassive()) {
										stand.setPassenger(null);
									}
									stand.remove();
									bolt.remove(i);
									break;
								}
							} else {
								if (b.isPassive()) {
									stand.setPassenger(null);
								}
								stand.remove();
								bolt.remove(i);
								break;
							}
						}
					}
					if (b.isPassive() && stand.getPassenger() == null) {
						if (stand.getTicksLived() < 5) {
							if (b.getShoop().isOnline()) {
								stand.setPassenger(b.getShoop());
							}
						}
					}
				}
				// Hit registration
				for (int i = 0; i < bolt.size(); i++) {
					Lightningbolt b = bolt.get(i);
					ArmorStandM standn = b.getStand();
					CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
					Location mid = stand.getEyeLocation();
					// Hitbox is about 2.5(h)x0.75(w)x0.75(l)
					double bonusxz = 1.3; // 0.35 player model width/length +0.4
					double bonusy = 0.8; // 1.8 player model height +0.55
					double bonuslengthf = 0.75;
					double bonuslengthb = -1.5;
					for (LivingEntity le : stand.getWorld().getLivingEntities()) {
						if (!(le instanceof ArmorStand)) {
							if (le.getUniqueId().compareTo(b.getShoop().getUniqueId()) != 0) {
								if (b.canHit(le)) {
									Location enemy = le.getLocation();
									for (double j = bonuslengthf; j >= bonuslengthb; j -= 0.05) {
										Vector dir = b.getVector().clone().normalize().multiply(j);
										Location midm = mid.clone().add(dir);
										if (Math.abs(enemy.getX() - midm.getX()) <= 0.35 + bonusxz) {
											if (Math.abs(enemy.getZ() - midm.getZ()) <= 0.35 + bonusxz) {
												double dy = enemy.getY() - midm.getY();
												if (dy >= -1.8 - bonusy && dy <= 0 + bonusy) {
													if (b.getShoop().isOnline()) {
														SoundUtil.sendSoundPacket(b.getShoop(), "random.successful_hit",
																b.getShoop().getLocation());
													}
													b.addHitted(le.getUniqueId());
													if (le instanceof Player) {
														Player target = (Player) le;
														SmashPlayer spt = Smashplex.getSmashPlayer(target);
														if (spt != null) {
															if (spt.getSelectedHero() != -1) {
																spt.damage(1.5);
																Vector kb = KnockbackUtil.getShoopPrimaryVelocity(
																		b.getShoop().getLocation().getPitch(),
																		b.getShoop().getLocation().getYaw(),
																		spt.getMasxHP() - spt.getHP());
																Vector ret = KnockbackUtil
																		.getFinalVelocity(target.getVelocity(), kb);
																b.getShoop().sendMessage(ret.getY() + " " + kb.getY());
																target.setVelocity(ret);
															}
														}
													}
													SmashPlayer sp = getSmashPlayer(b.getShoop());
													if (sp != null) {
														if (sp.getSelectedHero() == 0) {
															if (sp.getCharges() < 5) {
																sp.setCharges(sp.getCharges() + 1);
																b.getShoop().getInventory().setItem(1,
																		sp.getHero().getSecondary(
																				((double) sp.getCharges()) / 5.0));
															}
														}
													}
													if (!isSmashReady(b.getShoop())) {
														for (int k = 0; k < Smashplex.cooldown.size(); k++) {
															Cooldown c = Smashplex.cooldown.get(k);
															if (c.getPlayer().getUniqueId()
																	.compareTo(b.getShoop().getUniqueId()) == 0) {
																if (c.getSkill() == 2) {
																	c.setTicks(c.getTicks() - 40);
																}
															}
														}
													}
													break;
												}
											}
										}
									}
								}
							}
						}
					}
					if (b.isPassive()) {
						if (mid.distance(b.start()) >= 55) {
							stand.setPassenger(null);
							stand.remove();
							bolt.remove(i);
						}
					} else {
						if (mid.distance(b.start()) > 120) {
							stand.remove();
							bolt.remove(i);
						}
					}
				}
				// Smash
				for (int i = 0; i < lazor.size(); i++) {
					ShoopLazor sl = lazor.get(i);
					sl.setTicks(sl.getTicks() - 1);
					if (sl.getTicks() < 0) {
						sl.finish();
						lazor.remove(i);
					}
				}
			}
		}, 0, 1);
	}

	private void loopsSkullfire() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				// Block collision check
				for (int i = 0; i < nade.size(); i++) {
					Grenade b = nade.get(i);
					ArmorStandM standn = b.getStand();
					CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
					Location mid = stand.getEyeLocation();
					Block c = mid.getBlock();
					if (c.getType().isSolid()) {
						if (c.getType() == Material.STEP) {
							if (mid.getY() % mid.getBlockY() < 0.5) {
								ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_HUGE, mid, 1);
								SoundUtil.sendPublicSoundPacket("Skullfire.explodegrenade", mid);
								stand.remove();
								nade.remove(i);
								break;
							}
						} else {
							ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_HUGE, mid, 1);
							SoundUtil.sendPublicSoundPacket("Skullfire.explodegrenade", mid);
							stand.remove();
							nade.remove(i);
							break;
						}
					}
				}
				for (int i = 0; i < nade.size(); i++) {
					Grenade b = nade.get(i);
					ArmorStandM standn = b.getStand();
					CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
					if (standn.ticksLived % 3 == 1) {
						if (standn.ticksLived / 3.0 > 21.0) {
							Location mid = stand.getEyeLocation();
							ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_HUGE, mid, 1);
							SoundUtil.sendPublicSoundPacket("Skullfire.explodegrenade", mid);
							stand.remove();
							nade.remove(i);
						} else {
							Vector v = KnockbackUtil.getNadeVelocity(b.getPitch(), b.getYaw(),
									(int) (standn.ticksLived / 3.0 + 1.0));
							standn.motX = v.getX();
							standn.motY = v.getY();
							standn.motZ = v.getZ();
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

}
