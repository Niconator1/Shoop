package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.EnumParticle;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMove;

public class ShoopLazor {
	private Player shoop;
	private Vector dirold;
	private Location old;
	private ArrayList<ArmorStand> alist = new ArrayList<ArmorStand>();
	private int ticks = 80;

	public ShoopLazor(Player shoop) {
		this.shoop = shoop;
		start();
	}

	private void start() {
		Vector v = shoop.getLocation().getDirection().normalize();
		for (double i = 2.5; i < 50.0; i += 1.3) {
			Vector dif = v.normalize().multiply(i);
			Location l = shoop.getLocation().add(dif);
			ArmorStand a = (ArmorStand) shoop.getLocation().getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
			CraftArmorStand ca = (CraftArmorStand) a;
			ca.getHandle().noclip = true;
			old = l;
			dirold = v.normalize();
			a.setSilent(true);
			a.setGravity(false);
			a.setInvulnerable(true);
			a.setVisible(false);
			a.setRemoveWhenFarAway(false);
			a.setHelmet(new ItemStack(Material.DEAD_BUSH, 1));
			a.setHeadPose(a.getHeadPose().setX(l.getPitch() / 90.0 * 0.5 * Math.PI));
			alist.add(a);
		}

	}

	public Player getShoop() {
		return shoop;
	}

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public void finish() {
		for (int i = 0; i < alist.size(); i++) {
			ArmorStand a = alist.get(i);
			a.remove();
		}
	}

	public void handle() {
		Vector v = shoop.getLocation().getDirection().normalize();
		Vector dif = v.normalize().multiply(2.5);
		Location b = shoop.getEyeLocation().add(dif);
		for (int i = 0; i < 18; i++) {
			double x1 = Math.sin(i / 18.0 * 2.0 * Math.PI) * 1.0;
			double y1 = Math.cos(i / 18.0 * 2.0 * Math.PI) * 1.0;
			Vector v1 = new Vector(y1, 0, x1);
			v1 = ShoopProject.rotateAroundAxisX(v1, ((b.getPitch() + 90) / 180.0 * Math.PI));
			v1 = ShoopProject.rotateAroundAxisY(v1, -(b.getYaw()) * Math.PI / 180);
			ShoopProject.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX() + v1.getX(), b.getY() + v1.getY(),
					b.getZ() + v1.getZ(), 1f, 0f, 0f, 1f, 0);
		}
		for (int i = 0; i < alist.size(); i++) {
			double j = 2.5 + i * 1.3;
			ArmorStand a = alist.get(i);
			Vector difold = dirold.normalize().multiply(j);
			Location locold = old.clone().add(difold);
			a.teleport(locold);
			Location previous = a.getLocation();
			Vector difs = v.normalize().multiply(j);
			Location ls = shoop.getLocation().add(difs);
			old = shoop.getLocation();
			dirold = difs.normalize();
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				PacketPlayOutRelEntityMove packet = new PacketPlayOutRelEntityMove(a.getEntityId(),
						(long) ((ls.getX() * 32.0 - previous.getX() * 32.0) * 10.0),
						(long) ((ls.getY() * 32.0 - previous.getY() * 32.0) * 10.0),
						(long) ((ls.getZ() * 32.0 - previous.getZ() * 32.0) * 10.0), true);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
			}
			a.setHeadPose(a.getHeadPose().setX(ls.getPitch() / 90.0 * 0.5 * Math.PI));
		}
	}
}
