package heroes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Botmobile;
import abilities.Cooldown;
import abilities.Grenade;
import abilities.Lightningbolt;
import abilities.ShoopLazor;
import configuration.heroes;
import main.SmashPlayer;
import main.Smashplex;
import net.minecraft.server.v1_8_R3.EnumParticle;
import util.KnockbackUtil;
import util.ParticleUtil;
import util.SoundUtil;

public class HeroLoops {
	public static void loopsShoop() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Smashplex.getPlugin(Smashplex.class),
				new Runnable() {
					@Override
					public void run() {
						// Smash
						for (int i = 0; i < Smashplex.lazor.size(); i++) {
							ShoopLazor sl = Smashplex.lazor.get(i);
							if (sl.getTicks() > 0) {
								sl.handle();
							}
						}
					}
				}, 0, 2);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Smashplex.getPlugin(Smashplex.class),
				new Runnable() {
					@Override
					public void run() {
						// Block collision check
						for (int i = 0; i < Smashplex.bolt.size(); i++) {
							Lightningbolt b = Smashplex.bolt.get(i);
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
											Smashplex.bolt.remove(i);
											break;
										}
									} else {
										if (b.isPassive()) {
											stand.setPassenger(null);
										}
										stand.remove();
										Smashplex.bolt.remove(i);
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
						for (int i = 0; i < Smashplex.bolt.size(); i++) {
							Lightningbolt b = Smashplex.bolt.get(i);
							ArmorStandM standn = b.getStand();
							CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
							Location mid = stand.getEyeLocation();
							// Hitbox is about 2.5(h)x0.75(w)x0.75(l)
							double bonusxz = 1.3; // 0.35 player model
													// width/length +0.4
							double bonusy = 0.8; // 1.8 player model height
													// +0.55
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
																SoundUtil.sendSoundPacket(b.getShoop(),
																		"random.successful_hit",
																		b.getShoop().getLocation());
															}
															b.addHitted(le.getUniqueId());
															if (le instanceof Player) {
																Player target = (Player) le;
																SmashPlayer spt = Smashplex.getSmashPlayer(target);
																if (spt != null) {
																	if (spt.getSelectedHero() != null) {
																		spt.damage(heroes.SHOOPBOLTDAMAGE);
																		Vector kb = KnockbackUtil
																				.getShoopPrimaryVelocity(
																						b.getShoop().getLocation()
																								.getPitch(),
																						b.getShoop().getLocation()
																								.getYaw(),
																						spt.getMasxHP() - spt.getHP());
																		Vector ret = KnockbackUtil.getFinalVelocity(
																				target.getVelocity(), kb);
																		target.setVelocity(ret);
																	}
																}
															}
															SmashPlayer sp = Smashplex.getSmashPlayer(b.getShoop());
															if (sp != null) {
																if (sp.getSelectedHero().getNumber() == 0) {
																	Shoop s = (Shoop) sp.getSelectedHero();
																	if (s.getCharges() < heroes.SHOOPMAXCHARGES) {
																		s.setCharges(s.getCharges() + 1);
																		b.getShoop().getInventory().setItem(1,
																				s.getSecondary(((double) s.getCharges())
																						/ ((double) (heroes.SHOOPMAXCHARGES))));
																	}
																}
															}
															if (!Smashplex.isSmashReady(b.getShoop())) {
																for (int k = 0; k < Smashplex.cooldown.size(); k++) {
																	Cooldown c = Smashplex.cooldown.get(k);
																	if (c.getPlayer().getUniqueId().compareTo(
																			b.getShoop().getUniqueId()) == 0) {
																		if (c.getSkill() == 2) {
																			c.setTicks(c.getTicks()
																					- heroes.SHOOPBOLTSMASHREDUCTION);
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
								if (mid.distance(b.start()) >= heroes.SHOOPPASSIVERANGE) {
									stand.setPassenger(null);
									stand.remove();
									Smashplex.bolt.remove(i);
								}
							} else {
								if (mid.distance(b.start()) > 120) {
									stand.remove();
									Smashplex.bolt.remove(i);
								}
							}
						}
						// Smash
						for (int i = 0; i < Smashplex.lazor.size(); i++) {
							ShoopLazor sl = Smashplex.lazor.get(i);
							sl.setTicks(sl.getTicks() - 1);
							if (sl.getTicks() < 0) {
								sl.finish();
								Smashplex.lazor.remove(i);
							}
						}
					}
				}, 0, 1);
	}

	public static void loopsSkullfire() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Smashplex.getPlugin(Smashplex.class),
				new Runnable() {
					@Override
					public void run() {
						// Block collision check
						for (int i = 0; i < Smashplex.nade.size(); i++) {
							Grenade b = Smashplex.nade.get(i);
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
										Smashplex.nade.remove(i);
										break;
									}
								} else {
									ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_HUGE, mid, 1);
									SoundUtil.sendPublicSoundPacket("Skullfire.explodegrenade", mid);
									stand.remove();
									Smashplex.nade.remove(i);
									break;
								}
							}
						}
						for (int i = 0; i < Smashplex.nade.size(); i++) {
							Grenade b = Smashplex.nade.get(i);
							ArmorStandM standn = b.getStand();
							CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
							if (standn.ticksLived % 3 == 1) {
								if (standn.ticksLived / 3.0 > 21.0) {
									Location mid = stand.getEyeLocation();
									ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_HUGE, mid, 1);
									SoundUtil.sendPublicSoundPacket("Skullfire.explodegrenade", mid);
									stand.remove();
									Smashplex.nade.remove(i);
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

	public static void loopsBotmon() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Smashplex.getPlugin(Smashplex.class),
				new Runnable() {
					@Override
					public void run() {
						// Velocity change
						for (int i = 0; i < Smashplex.bm.size(); i++) {
							Botmobile b = Smashplex.bm.get(i);
							ArmorStandM standn = b.getStand();
							CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
							Player p = b.getPlayer();
							double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
							double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
							double x = Math.sin(pitch) * Math.cos(yaw);
							double y = Math.cos(pitch);
							double z = Math.sin(pitch) * Math.sin(yaw);
							Vector v = new Vector(x, y, z).multiply(0.911625);
							standn.yaw = p.getLocation().getYaw();
							standn.motX = v.getX();
							standn.motY = v.getY();
							standn.motZ = v.getZ();
							stand.setHeadPose(
									stand.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
							Location smoke = stand.getEyeLocation().add(0, 0.2, 0).add(v.normalize().multiply(-2.0));
							ParticleUtil.sendPublicParticlePacket(EnumParticle.SMOKE_LARGE, smoke, 1);
							// SoundUtil.sendPublicSoundPacket("Skullfire.explodegrenade",
							// mid);
						}
						// Leave detection
						for (int i = 0; i < Smashplex.bm.size(); i++) {
							Botmobile b = Smashplex.bm.get(i);
							ArmorStandM standn = b.getStand();
							CraftArmorStand stand = (CraftArmorStand) standn.getBukkitEntity();
							if (stand.getPassenger() == null) {
								if (stand.getTicksLived() < 5) {
									if (b.getPlayer().isOnline()) {
										stand.setPassenger(b.getPlayer());
									}
								} else {
									standn.die();
									Smashplex.bm.remove(i);
								}
							}
						}
					}
				}, 0, 1);

	}
}
