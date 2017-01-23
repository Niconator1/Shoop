package main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
import org.bukkit.util.Vector;

import abilities.Cooldown;
import abilities.FlameJump;
import abilities.Lightningbolt;
import net.minecraft.server.v1_8_R3.EnumParticle;
import util.ParticleUtil;
import util.SoundUtil;

public class EventManager implements Listener {
	@EventHandler
	public void onConnect(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = new SmashPlayer(p, 100);
		Smashplex.players.add(sp);
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if (e.isSneaking()) {
			SmashPlayer sp = Smashplex.getSmashPlayer(p);
			if (sp != null) {
				if (sp.getSelectedHero() == 0) {
					if (p.getLevel() >= 98) {
						p.setSneaking(false);
						double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
						double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
						double x = Math.sin(pitch) * Math.cos(yaw);
						double y = Math.cos(pitch);
						double z = Math.sin(pitch) * Math.sin(yaw);
						Vector v = new Vector(x, y, z).multiply(2.0);
						Location l = p.getLocation();
						double xo = Math.cos(yaw);
						double zo = -Math.sin(yaw);
						Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
						l.add(vo.getX(), -0.3, vo.getZ());
						ArmorStand f = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
						CraftArmorStand a = (CraftArmorStand) f;
						a.getHandle().noclip = true;
						f.setVelocity(v);
						f.setVisible(false);
						f.setHelmet(new ItemStack(Material.WOOL, 1, (short) 10));
						f.setHeadPose(f.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
						SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.active", p.getLocation());
						f.setPassenger(p);
						Lightningbolt bolt = new Lightningbolt(f, v, e.getPlayer(), l, true);
						Smashplex.bolt.add(bolt);
						p.setLevel(p.getLevel() - 98);
						float pro = p.getLevel() / (100 + 0.000001f);
						p.setExp(pro);
						e.setCancelled(true);
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
			if (sp.getSelectedHero() != -1) {
				sp.removeHeroItems();
				for (int i = 0; i < Smashplex.cooldown.size(); i++) {
					Cooldown c = Smashplex.cooldown.get(i);
					if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
						Smashplex.cooldown.remove(i);
					}
				}
			}
			Smashplex.players.remove(sp);
		}
	}

	@EventHandler
	public void registerDoubleJump(PlayerToggleFlightEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != -1) {
				if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
					if (sp.getRemainingJumps() > 0 || sp.getFlameJump()) {
						double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
						double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
						double x = Math.sin(pitch) * Math.cos(yaw);
						double y = Math.cos(pitch);
						double z = Math.sin(pitch) * Math.sin(yaw);
						Vector v = new Vector(x, y, z).normalize().multiply(0.77);
						p.setVelocity(new Vector(v.getX(), 0.77, v.getZ()));
						event.setCancelled(true);
						sp.setJumps(sp.getRemainingJumps() - 1);
						SoundUtil.sendPublicSoundPacket("mob.bat.takeoff", p.getLocation());
						if (sp.getFlameJump()) {
							new FlameJump(p.getLocation()).doJump();
						} else {
							for (int i = 0; i < 20; i++) {
								double radius = 1.37;
								double cx = Math.sin(((double) i) / 20.0 * Math.PI * 2.0) * radius;
								double cz = Math.cos(((double) i) / 20.0 * Math.PI * 2.0) * radius;
								if (sp.getSelectedHero() == 1) {
									ParticleUtil.sendPublicParticlePacket(EnumParticle.SMOKE_LARGE,
											p.getLocation().add(cx, 0, cz), 2);
								} else {
									ParticleUtil.sendPublicParticlePacket(EnumParticle.CLOUD,
											p.getLocation().add(cx, 0, cz), 2);
								}

							}
						}
						sp.setFlameJump(false);
						if (sp.getRemainingJumps() <= 0) {
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
				if (sp.getSelectedHero() != -1) {
					if (Smashplex.isPrimaryReady(p)) {
						if (Smashplex.smash) {
							sp.doPrimary();
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
					if (sp.getSelectedHero() != -1) {
						if (Smashplex.isPrimaryReady(p)) {
							if (Smashplex.smash) {
								sp.doPrimary();
							}
						} else {
							if (sp.getSelectedHero() == 1) {
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
			if (sp.getSelectedHero() != -1) {
				Entity e = (Entity) p;
				if (e.isOnGround()) {
					p.setAllowFlight(true);
					sp.setJumps(2);
					sp.setFlameJump(false);
				}
			}
		}
	}

	@EventHandler
	public void registerSlotSwitch(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		SmashPlayer sp = Smashplex.getSmashPlayer(p);
		if (sp != null) {
			if (sp.getSelectedHero() != -1) {
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

	@EventHandler
	public void blockDamageEvent(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
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
