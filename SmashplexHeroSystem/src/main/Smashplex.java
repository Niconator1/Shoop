package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import abilities.Cooldown;
import abilities.Lightningbolt;
import util.SoundUtil;
import util.TextUtil;

public class Smashplex extends JavaPlugin {
	public static ArrayList<SmashPlayer> players = new ArrayList<SmashPlayer>();
	public static ArrayList<Lightningbolt> bolt = new ArrayList<Lightningbolt>();
	public static ArrayList<Cooldown> cooldown = new ArrayList<Cooldown>();
	public static boolean smash = false;

	public void onEnable() {
		this.getLogger().info("Smashplex enabled");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		loopsSmash();
		for (Player p : Bukkit.getOnlinePlayers()) {
			SmashPlayer sp = new SmashPlayer(p);
			Smashplex.players.add(sp);
		}
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
			bolt.get(i).getStand().remove();
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
						}
						sender.sendMessage("You selected shoop");
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
				// Projectile Gravitation disabling
				for (int i = 0; i < bolt.size(); i++) {
					bolt.get(i).getStand().setVelocity(bolt.get(i).getVector());
				}
				// Block collision check
				for (int i = 0; i < bolt.size(); i++) {
					Lightningbolt b = bolt.get(i);
					ArmorStand stand = b.getStand();
					Location mid = stand.getEyeLocation();
					double bonuslengthf = 0.75;
					double bonuslengthb = -0.5;// -1.5
					for (double j = bonuslengthf; j >= bonuslengthb; j -= 0.05) {
						Vector dir = b.getVector().clone().normalize().multiply(j);
						Location midm = mid.clone().add(dir);
						Block c = midm.getBlock();
						if (c.getType().isSolid()) {
							if (c.getType() == Material.STEP) {
								if (midm.getY() % midm.getBlockY() < 0.5) {
									if (b.isPassive()) {
										b.getStand().setPassenger(null);
									}
									stand.remove();
									bolt.remove(i);
									break;
								}
							} else {
								if (b.isPassive()) {
									b.getStand().setPassenger(null);
								}
								stand.remove();
								bolt.remove(i);
								break;
							}
						}
					}
					if (b.isPassive() && stand.getPassenger() == null) {
						stand.remove();
						bolt.remove(i);
					}
				}
				// Hit registration
				for (int i = 0; i < bolt.size(); i++) {
					Lightningbolt b = bolt.get(i);
					ArmorStand stand = b.getStand();
					Location mid = stand.getEyeLocation();
					// Hitbox is about 2.5(h)x0.75(w)x0.75(l)
					double bonusxz = 0.4; // 0.35 player model width/length
					double bonusy = 0.55; // 1.8 player model height
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
							b.getStand().setPassenger(null);
							b.getStand().remove();
							bolt.remove(i);
						}
					} else {
						if (mid.distance(b.start()) > 70) {
							b.getStand().remove();
							bolt.remove(i);
						}
					}
				}
				// Cooldown decrease
				for (int i = 0; i < cooldown.size(); i++) {
					Cooldown c = cooldown.get(i);
					c.setTicks(c.getTicks() - 1);
					Player p = c.getPlayer();
					if (c.getSkill() == 0) {
						if (p.isOnline()) {
							TextUtil.sendCooldownMessage(c);
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
						if (c.getSkill() == 2) {
							if (p.isOnline()) {
								SmashPlayer sp = getSmashPlayer(p);
								if (sp != null) {
									if (sp.getSelectedHero() != -1) {
										p.getInventory().setItem(2, sp.getHero().getSmash(1.0));
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
							if (p.getLevel() < 100) {
								p.setLevel(p.getLevel() + 1);
								float pro = p.getLevel() / (100 + 0.000001f);
								p.setExp(pro);
							}
						}
					}
				}
			}
		}, 0, 2);
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
