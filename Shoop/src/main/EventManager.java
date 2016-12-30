package main;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.EnumParticle;

public class EventManager implements Listener {
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (e.getItem()==ItemStackManipulation.getBoltItem()) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Player p = e.getPlayer();
					if(!p.getName().equals("NiconatorTM")){
						return;
					}
					if (ShoopProject.isBoltReady(p)) {
						double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
						double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
						double x = Math.sin(pitch) * Math.cos(yaw);
						double y = Math.cos(pitch);
						double z = Math.sin(pitch) * Math.sin(yaw);
						Vector v = new Vector(x, y, z).multiply(2.5);
						Location l = p.getLocation();
						double xo = Math.cos(yaw);
						double zo = -Math.sin(yaw);
						Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
						l.add(vo.getX(), -0.45, vo.getZ());
						ArmorStand f = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
						CraftArmorStand a = (CraftArmorStand) f;
						a.getHandle().noclip = true;
						f.setVelocity(v);
						System.out.println(f.getVelocity().length());
						f.setSilent(true);
						f.setVisible(false);
						f.setInvulnerable(true);
						f.setHelmet(new ItemStack(Material.WOOL, 1, (short) 10));
						f.setHeadPose(f.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
						p.getWorld().playSound(p.getLocation(), "ShoopDaWhoop.active", 1, 1);
						Cooldown c = new Cooldown(p, 0, 5);
						ShoopProject.sendCooldownMessage(c);
						ShoopProject.cooldown.add(c);
						Lightningbolt bolt = new Lightningbolt(f, v, e.getPlayer(), l, false);
						ShoopProject.bolt.add(bolt);
					}
				}
			}
		}
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(!p.getName().equals("NiconatorTM")){
			return;
		}
		// p.sendMessage(e.isSneaking() + " " + p.isSneaking() + " " +
		// p.isInsideVehicle());
		if (e.isSneaking()) {
			if (p.getLevel() >= 98) {
				p.setSneaking(false);
				double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
				double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
				double x = Math.sin(pitch) * Math.cos(yaw);
				double y = Math.cos(pitch);
				double z = Math.sin(pitch) * Math.sin(yaw);
				Vector v = new Vector(x, y, z).multiply(2.5);
				Location l = p.getLocation();
				double xo = Math.cos(yaw);
				double zo = -Math.sin(yaw);
				Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
				l.add(vo.getX(), -0.3, vo.getZ());
				ArmorStand f = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
				CraftArmorStand a = (CraftArmorStand) f;
				a.getHandle().noclip = true;
				f.setVelocity(v);
				f.setSilent(true);
				f.setVisible(false);
				f.setInvulnerable(true);
				f.setHelmet(new ItemStack(Material.WOOL, 1, (short) 10));
				f.setHeadPose(f.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
				p.getWorld().playSound(p.getLocation(), "ShoopDaWhoop.active", 1, 1);
				f.setPassenger(p);
				Lightningbolt bolt = new Lightningbolt(f, v, e.getPlayer(), l, true);
				ShoopProject.bolt.add(bolt);
				p.setLevel(p.getLevel() - 98);
				float pro = p.getLevel() / (100 + 0.000001f);
				p.setExp(pro);
				e.setCancelled(true);
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ShoopProject.getPlugin(ShoopProject.class),
						new Runnable() {
							@Override
							public void run() {
								f.setPassenger(p);
							}
						}, 2);
			}
		} else {
			for (int i = 0; i < ShoopProject.bolt.size(); i++) {
				Lightningbolt b = ShoopProject.bolt.get(i);
				if (p.getUniqueId().compareTo(b.getShoop().getUniqueId()) == 0) {
					if (b.isPassive()) {
						if (b.getStand().getLocation().distance(b.start()) > 3) {
							p.setSneaking(false);
							b.getStand().setPassenger(null);
							b.getStand().remove();
							ShoopProject.bolt.remove(i);
						} else {
							p.setSneaking(false);
							e.setCancelled(true);
						}
					}
				}
			}
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

	@EventHandler
	public void registerDoubleJump(PlayerToggleFlightEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("NiconatorTM")){
			return;
		}
		event.setCancelled(true);
		p.sendMessage("Jump");
		double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.cos(yaw);
		double z = Math.sin(yaw);
		Vector v = new Vector(x, 0, z).normalize().multiply(0.77);
		p.setVelocity(new Vector(v.getX(), 0.77, v.getZ()));
	}

	@EventHandler
	public void registerSlotSwitch(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		if(!p.getName().equals("NiconatorTM")){
			return;
		}
		if (event.getNewSlot() == 1) {
			int charges = ShoopProject.getCharges(p);
			if (charges > 0) {
				double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
				double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
				double x = Math.sin(pitch) * Math.cos(yaw);
				double y = Math.cos(pitch);
				double z = Math.sin(pitch) * Math.sin(yaw);
				Vector v = new Vector(x, y, z).normalize();
				Location b = p.getEyeLocation();
				ArrayList<UUID> hitted = new ArrayList<UUID>();
				int r = 60 * 2;
				if (charges > 4) {
					ShoopProject.sendPublicSoundPacket("ShoopDaWhoop.chargedbeam.big", p.getLocation());
					v.multiply(0.5);
				} else {
					ShoopProject.sendPublicSoundPacket("ShoopDaWhoop.chargedbeam.small", p.getLocation());
					v.multiply(0.25);
					r *= 2;
				}
				for (int i = 0; i < r; i++) {
					b.add(v);
					Block c = b.getBlock();
					if (c.getType().isSolid()) {
						break;
					}
					if (b.distance(p.getEyeLocation()) >= 1.0) {
						switch (charges) {
						case 2:
							ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
									-1.0f, 1f, 0.4f, 1f, 0);
							break;
						case 3:
							ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
									-1.0f, 0.4f, 1f, 1f, 0);
							break;
						case 4:
							ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
									-0.19999999f, 0f, 1f, 1f, 0);
							break;
						case 5:
							for (int j = 0; j < 5; j++) {
								double x1 = Math.sin(j / 5.0 * 2.0 * Math.PI) * 0.2;
								double y1 = Math.cos(j / 5.0 * 2.0 * Math.PI) * 0.2;
								Vector v1 = new Vector(y1, 0, x1);
								v1 = ShoopProject.rotateAroundAxisX(v1, ((b.getPitch() + 90) / 180.0 * Math.PI));
								v1 = ShoopProject.rotateAroundAxisY(v1, -(b.getYaw()) * Math.PI / 180);
								ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX() + v1.getX(),
										b.getY() + v1.getY(), b.getZ() + v1.getZ(), 1f, 1f, 1f, 1f, 0);
							}
							ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
									1f, 0f, 0f, 1f, 0);
							break;
						default:
							ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
									-0.19999999f, 1f, 0f, 1f, 0);
							break;
						}
					}
				}
				ShoopProject.setCharges(p, 0);
				b = p.getEyeLocation();
				v.normalize().multiply(0.1);
				// Hitbox is about 2.5(h)x0.75(w)x0.75(l)
				double bonusxz = 0.4; // 0.35 player model width/length
				double bonusy = 0.55; // 1.8 player model height
				for (double j = 0; j <= 60; j += 0.1) {
					Location midm = b.add(v);
					Block c = midm.getBlock();
					if (c.getType().isSolid()) {
						break;
					}
					for (LivingEntity le : b.getWorld().getLivingEntities()) {
						if (!(le instanceof ArmorStand)) {
							if (le.getUniqueId().compareTo(p.getUniqueId()) != 0) {
								boolean canhit = true;
								for (int i = 0; i < hitted.size(); i++) {
									if (hitted.get(i).compareTo(le.getUniqueId()) == 0) {
										canhit = false;
									}
								}
								if (canhit) {
									Location enemy = le.getLocation();
									if (Math.abs(enemy.getX() - midm.getX()) <= 0.35 + bonusxz) {
										if (Math.abs(enemy.getZ() - midm.getZ()) <= 0.35 + bonusxz) {
											double dy = enemy.getY() - midm.getY();
											if (dy >= -1.8 - bonusy && dy <= 0 + bonusy) {
												ShoopProject.sendSoundPacket(p, "entity.arrow.hit_player",
														p.getLocation());
												hitted.add(le.getUniqueId());
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else if (event.getNewSlot() == 2) {
			// ItemStack is = p.getInventory().getItem(1);
			// if (is.getType() == Material.INK_SACK && is.getDurability() ==
			// 14) {
			ShoopProject.sendPublicSoundPacket("ShoopDaWhoop.crystal", 1.0f);
			p.getInventory().setItem(2, ItemStackManipulation.getSmashItem(0));
			Cooldown c = new Cooldown(p, 2, 1799);
			ShoopProject.cooldown.add(c);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ShoopProject.getPlugin(ShoopProject.class),
					new Runnable() {
						@Override
						public void run() {
							ShoopLazor sl = new ShoopLazor(p);
							ShoopProject.lazor.add(sl);
							ShoopProject.sendPublicSoundPacket("entity.wither.death", 0.5f);
						}
					}, 45);
			// }
		}
		p.getInventory().setHeldItemSlot(0);
	}
}
