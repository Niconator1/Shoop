package main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
					Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.4);
					l.add(vo.getX(), -0.5, vo.getZ());
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
					Lightningbolt bolt = new Lightningbolt(f, v, e.getPlayer(), l);
					ShoopProject.bolt.add(bolt);
				}
			}
		}
	}
}
