package main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.EnumParticle;

public class EventManager implements Listener {
	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (e.getItem().getType() == Material.RAW_BEEF) {
				Player p = e.getPlayer();
				if (ShoopProject.isBoltReady(p)) {
					double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
					double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
					double x = Math.sin(pitch) * Math.cos(yaw);
					double y = Math.cos(pitch);
					double z = Math.sin(pitch) * Math.sin(yaw);
					Vector v = new Vector(x, y, z).multiply(1.4);
					Location l = p.getLocation();
					double xo = Math.cos(yaw);
					double zo = -Math.sin(yaw);
					Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
					l.add(vo.getX(), -0.45, vo.getZ());
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
					Cooldown c = new Cooldown(p, 0, 5);
					ShoopProject.sendCooldownMessage(c);
					ShoopProject.cooldown.add(c);
					Lightningbolt bolt = new Lightningbolt(f, v, e.getPlayer(), l, false);
					ShoopProject.bolt.add(bolt);
				}
			}
		}
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		p.sendMessage(e.isSneaking() + " " + p.isSneaking() + " " + p.isInsideVehicle());
		if (e.isSneaking()) {
			if (p.getLevel() >= 98) {
				p.setSneaking(false);
				double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
				double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
				double x = Math.sin(pitch) * Math.cos(yaw);
				double y = Math.cos(pitch);
				double z = Math.sin(pitch) * Math.sin(yaw);
				Vector v = new Vector(x, y, z).multiply(1.4);
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
	public void registerSlotSwitch(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		if (event.getNewSlot() == 1) {
			double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
			double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
			double x = Math.sin(pitch) * Math.cos(yaw);
			double y = Math.cos(pitch);
			double z = Math.sin(pitch) * Math.sin(yaw);
			Vector v = new Vector(x, y, z).normalize().multiply(0.5);
			Location b = p.getEyeLocation();
			for (int i = 0; i < 120; i++) {
				b.add(v);
				ShoopProject.sendParticlePacket(p, EnumParticle.FLAME, b, 1);
			}
		}
		p.getInventory().setHeldItemSlot(0);
	}
}
