package util;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;

public class KnockbackUtil {

	public static void sendPublicVelocityPacket(Entity f, Vector v) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PacketPlayOutEntityVelocity packet = new PacketPlayOutEntityVelocity(f.getEntityId(), v.getX(), v.getY(),
					v.getZ());
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}

	}

	public static Vector getNadeVelocity(double angley, double anglexz, double tick) {
		double pitch = ((angley + 90.0) * Math.PI) / 180.0;
		double yaw = ((anglexz + 90.0) * Math.PI) / 180.0;
		double x = Math.cos(yaw);
		double z = Math.sin(yaw);
		double xzp = Math.sin(pitch);
		double ixz = xzp * 1.8;
		double yp = Math.cos(pitch);
		double iy = 0.2 + yp * 1.8;
		if (tick == 0) {
			return new Vector(ixz * x, iy, ixz * z);
		} else if (tick == 1) {
			double firstxz = 0.018 - Math.abs(yp) * 0.018;
			double firsty = 0.052 + yp * 0.018;
			double remainingy = iy - firsty;
			double remainingxz = ixz - firstxz;
			return new Vector(remainingxz * x, remainingy, remainingxz * z);
		} else {
			double firstxz = 0.018 - Math.abs(yp) * 0.018;
			double numberxz = 0.05455 - Math.abs(yp) * 0.05455;
			double firsty = 0.052 + yp * 0.018;
			double numbery = 0.15758 + yp * 0.05455;
			double remainingy = iy - firsty;
			double remainingxz = ixz - firstxz;
			for (int i = 1; i < tick; i++) {
				remainingy -= numbery * Math.pow(0.9703, i);
				remainingxz -= numberxz * Math.pow(0.9703, i);
			}
			return new Vector(remainingxz * x, remainingy, remainingxz * z);
		}
	}

	public static Vector getShoopPrimaryVelocity(double angley, double anglexz, double hp) {
		double pitch = ((angley + 90.0) * Math.PI) / 180.0;
		// double yaw = ((anglexz + 90.0) * Math.PI) / 180.0;
		// double x = Math.cos(yaw);
		// double z = Math.sin(yaw);
		// double xzp = Math.sin(pitch);
		// double ixz = xzp * 1.8;
		double yp = Math.cos(pitch);
		double iy = 0.0;
		if (yp >= 0.0) {
			iy = yp * 0.1325 + 0.10125;
		} else {
			iy = yp * 0.335 + 0.10125;
		}
		// double numberxz = 0.05455 - Math.abs(yp) * 0.05455;
		// double remainingxz = ixz - firstxz;
		double numbery = hp * yp * (0.0075 + 0.005);
		double remainingy = iy + numbery;
		return new Vector(0, remainingy + 0.08 * 0.98, 0);
	}

	public static Vector getFinalVelocity(Vector base, Vector kb) {
		if (base.getY() >= 0) {
			if (kb.getY() > 0) {
				if (kb.getY() > base.getY()) {
					return new Vector(base.getX(), kb.getY(), base.getZ());
				} else {
					return base;
				}
			} else {
				return new Vector(base.getX(), base.getY() + kb.getY(), base.getZ());
			}
		} else {
			if (kb.getY() < 0) {
				if (kb.getY() < base.getY()) {
					return new Vector(base.getX(), kb.getY(), base.getZ());
				} else {
					return base;
				}
			} else {
				return new Vector(base.getX(), base.getY() + kb.getY(), base.getZ());
			}
		}
	}

}
