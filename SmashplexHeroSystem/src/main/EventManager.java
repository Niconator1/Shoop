package main;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Cooldown;
import abilities.FlameJump;
import abilities.Lightningbolt;
import configuration.heroes;
import heroes.Skullfire;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.WorldServer;
import util.ParticleUtil;
import util.SoundUtil;

public class EventManager implements Listener {
	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = new SmashPlayer(p, 100);
		if (!sp.firstJoin()) {
			sp.readData();
		}
		Smashplex.players.add(sp);
		for (int i = 0; i < Smashplex.npcs.size(); i++) {
			NPC n = Smashplex.npcs.get(i);
			UUID npc = n.getLocation().getWorld().getUID();
			if (npc.compareTo(p.getLocation().getWorld().getUID()) == 0
					&& p.getLocation().distance(n.getLocation()) < 100.0) {
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

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if (e.isSneaking()) {
			SmashPlayer sp = Smashplex.getSmashPlayer(p);
			if (sp != null) {
				if (sp.getSelectedHero() != null) {
					Game g = Smashplex.getGame(p);
					if (g != null && g.hasBegan()) {
						if (sp.getSelectedHero().getNumber() == 0) {
							if (p.getLevel() >= heroes.SHOOPPASSIVEENERGY) {
								p.setSneaking(false);
								double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
								double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
								double x = Math.sin(pitch) * Math.cos(yaw);
								double y = Math.cos(pitch);
								double z = Math.sin(pitch) * Math.sin(yaw);
								Vector v = new Vector(x, y, z).multiply(heroes.SHOOPPASSIVESPEED);
								Location l = p.getLocation();
								double xo = Math.cos(yaw);
								double zo = -Math.sin(yaw);
								Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
								l.add(vo.getX(), -0.3, vo.getZ());
								SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.active", p.getLocation());
								WorldServer s = ((CraftWorld) l.getWorld()).getHandle();
								ArmorStandM fn = new ArmorStandM(s, 0);
								fn.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
								fn.noclip = true;
								CraftArmorStand an = (CraftArmorStand) fn.getBukkitEntity();
								fn.motX = v.getX();
								fn.motY = v.getY();
								fn.motZ = v.getZ();
								an.setVisible(false);
								an.setHelmet(new ItemStack(Material.WOOL, 1, (short) 10));
								an.setHeadPose(
										an.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
								s.addEntity(fn);
								an.setPassenger(p);
								Lightningbolt bolt = new Lightningbolt(fn, v, e.getPlayer(), l, true);
								Smashplex.bolt.add(bolt);
								p.setLevel(p.getLevel() - heroes.SHOOPPASSIVEENERGY);
								float pro = p.getLevel() / (100 + 0.000001f);
								p.setExp(pro);
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			sp.saveData();
			Smashplex.players.remove(sp);
		}
		for (int i = 0; i < Smashplex.cooldown.size(); i++) {
			Cooldown c = Smashplex.cooldown.get(i);
			if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				Smashplex.cooldown.remove(i);
			}
		}
		for (int i = 0; i < Smashplex.npcs.size(); i++) {
			NPC n = Smashplex.npcs.get(i);
			n.destroy(p);
		}
		if (Smashplex.getGame(p) != null) {
			Smashplex.getGame(p).leave(p);
		}
	}

	@EventHandler
	public void registerDoubleJump(PlayerToggleFlightEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != null) {
				if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
					if (sp.getRemainingJumps() > 0 || sp.getSelectedHero().getNumber() == 1
							&& ((Skullfire) sp.getSelectedHero()).getFlameJumps() > 0) {
						double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
						double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
						double x = Math.sin(pitch) * Math.cos(yaw);
						double y = Math.cos(pitch);
						double z = Math.sin(pitch) * Math.sin(yaw);
						Vector v = new Vector(x, y, z).normalize().multiply(0.787);
						p.setVelocity(new Vector(v.getX(), 0.865, v.getZ()));
						event.setCancelled(true);
						sp.setJumps(sp.getRemainingJumps() - 1);
						SoundUtil.sendPublicSoundPacket("mob.bat.takeoff", p.getLocation());
						if (sp.getSelectedHero().getNumber() == 1
								&& ((Skullfire) sp.getSelectedHero()).getFlameJumps() > 0) {
							Skullfire s = (Skullfire) sp.getSelectedHero();
							new FlameJump(p.getLocation()).doJump();
							s.setFlameJumps(s.getFlameJumps() - 1);
						} else {
							for (int i = 0; i < 20; i++) {
								double radius = 1.0;
								double cx = Math.sin(((double) i) / 20.0 * Math.PI * 2.0) * radius;
								double cz = Math.cos(((double) i) / 20.0 * Math.PI * 2.0) * radius;
								if (sp.getSelectedHero().getNumber() == 1) {
									ParticleUtil.sendPublicParticlePacket(EnumParticle.SMOKE_LARGE,
											p.getLocation().add(cx, 0, cz), 2);
								} else {
									ParticleUtil.sendPublicParticlePacket(EnumParticle.CLOUD,
											p.getLocation().add(cx, 0, cz), 2);
								}

							}
						}
						if (sp.getRemainingJumps() <= 0 && sp.getSelectedHero().getNumber() != 1
								|| sp.getRemainingJumps() <= 0 && sp.getSelectedHero().getNumber() == 1
										&& ((Skullfire) sp.getSelectedHero()).getFlameJumps() <= 0) {
							p.setAllowFlight(false);
						}
					}
				}
			}

		}
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		Player p = event.getPlayer();
		if (p.getInventory().getItemInHand() != null) {
			SmashPlayer sp = Smashplex.getSmashPlayer(p);
			if (sp != null) {
				if (sp.getSelectedHero() != null) {
					if (Smashplex.isPrimaryReady(p)) {
						if (Smashplex.smash) {
							Game g = Smashplex.getGame(p);
							if (g != null && g.hasBegan()) {
								sp.doPrimary();
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		for (Player p2 : Bukkit.getOnlinePlayers()) {
			if (p2.getUniqueId().compareTo(p.getUniqueId()) != 0) {
				if (event.getMessage().contains(p2.getName())) {
					SoundUtil.sendSoundPacket(p2, "random.successful_hit", p2.getLocation());
				}
			}
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Player p = e.getPlayer();
				SmashPlayer sp = Smashplex.getSmashPlayer(p);
				if (sp != null) {
					if (sp.getSelectedHero() != null) {
						if (Smashplex.isPrimaryReady(p)) {
							if (Smashplex.smash) {
								Game g = Smashplex.getGame(p);
								if (g != null && g.hasBegan()) {
									sp.doPrimary();
								}
							}
						} else {
							if (sp.getSelectedHero().getNumber() == 1) {
								p.sendMessage(ChatColor.YELLOW + "Reloading... ");
								SoundUtil.sendSoundPacket(p, "SmashCrystal.notready", p.getLocation());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != null) {
				Entity e = (Entity) p;
				if (e.isOnGround()) {
					p.setAllowFlight(true);
					sp.setJumps(2);
					if (sp.getSelectedHero().getNumber() == 1) {
						Skullfire s = (Skullfire) sp.getSelectedHero();
						s.setFlameJumps(0);
					}
				}
			}
		}
		UUID to = event.getTo().getWorld().getUID();
		UUID from = event.getFrom().getWorld().getUID();
		for (int i = 0; i < Smashplex.npcs.size(); i++) {
			NPC n = Smashplex.npcs.get(i);
			UUID npc = n.getLocation().getWorld().getUID();
			if (to.compareTo(npc) == 0) {
				if (from.compareTo(npc) == 0) {
					if (event.getTo().distance(n.getLocation()) < 100.0) {
						if (event.getFrom().distance(n.getLocation()) > 100.0) {
							// Walked in right dimension and entered range
							n.spawn(p, false);
							Bukkit.getServer().getScheduler()
									.scheduleSyncDelayedTask(JavaPlugin.getPlugin(Smashplex.class), new Runnable() {
										public void run() {
											n.rmvFromTablist(p);
										}
									}, 100);
						} else {
							// Nothing, walked in right dimension while in range
						}
					} else {
						if (event.getFrom().distance(n.getLocation()) < 100.0) {
							// Walked in right dimension and left range
							n.destroy(p);
						} else {
							// Nothing, walked in right dimension outside of
							// range
						}
					}
				} else {
					if (event.getTo().distance(n.getLocation()) < 100.0) {
						// Entered right dimension and in reach
						n.spawn(p, false);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Smashplex.class),
								new Runnable() {
									public void run() {
										n.rmvFromTablist(p);
									}
								}, 100);
					} else {
						// Nothing, entered right dimension but not in reach
					}
				}
			} else {
				if (from.compareTo(npc) == 0) {
					if (event.getFrom().distance(n.getLocation()) < 100.0) {
						// Left right dimension and in reach
						n.destroy(p);
					} else {
						// Nothing, Left right dimension but not in reach
					}

				} else {
					// Changed dimensions but none of them was an npc dimension
				}
			}
		}
	}

	@EventHandler
	public void registerSlotSwitch(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != null) {
				Game g = Smashplex.getGame(p);
				if (g != null && g.hasBegan()) {
					if (event.getNewSlot() == 1) {
						if (Smashplex.isSecondaryReady(p)) {
							if (Smashplex.smash) {
								sp.doSecondary();
							}
						} else {
							for (int i = 0; i < Smashplex.cooldown.size(); i++) {
								Cooldown c = Smashplex.cooldown.get(i);
								if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
									if (c.getSkill() == 1) {
										long time = Math.round(c.getTicks() / 20.0);
										p.sendMessage(ChatColor.YELLOW + "You can't use that yet! " + time
												+ " seconds remaining");
									}
								}
							}
						}

					} else if (event.getNewSlot() == 2) {
						if (Smashplex.isSmashReady(p)) {
							if (Smashplex.smash) {
								sp.doSmash();
							}
						} else {
							for (int i = 0; i < Smashplex.cooldown.size(); i++) {
								Cooldown c = Smashplex.cooldown.get(i);
								if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
									if (c.getSkill() == 2) {
										long time = Math.round(c.getTicks() / 20.0);
										p.sendMessage(ChatColor.YELLOW + "You can't use that yet! " + time
												+ " seconds remaining");
										SoundUtil.sendSoundPacket(p, "SmashCrystal.notready", p.getLocation());
									}
								}
							}
						}
					}
					p.getInventory().setHeldItemSlot(0);
				}
			}
		}
	}

	@EventHandler
	public void blockDamageEvent(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player p = (Player) entity;
			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
				Entity damager = e.getDamager();
				if (damager instanceof Player) {
					Player p2 = (Player) damager;
					SmashPlayer sp = Smashplex.getSmashPlayer(p);
					SmashPlayer enemy = Smashplex.getSmashPlayer(p2);
					if (sp != null && enemy != null) {
						if (sp.getSelectedHero() != null && enemy.getSelectedHero() != null) {
							Game g = Smashplex.getGame(p);
							if (g != null && g.hasBegan() && g.hasPlayer(p2)) {
								double dmg = enemy.getSelectedHero().getMeleeDamage();
								sp.damage(dmg);
							}
						}
					}
				}
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void blockFoodEvent(FoodLevelChangeEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player p = (Player) entity;
			p.setFoodLevel(20);
			event.setCancelled(true);
		}
	}

}
