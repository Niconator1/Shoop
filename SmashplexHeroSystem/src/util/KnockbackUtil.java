package util;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import main.SmashPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;

public class KnockbackUtil {
	public static Vector getKnockback(SmashPlayer sp) {
		double sshp = sp.getMasxHP() - sp.getHP();
		double ey = 0.0125 * sshp;
		return new Vector(0, ey, 0);
	}

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
			return new Vector(ixz * x, iy / 0.98 + 0.08, ixz * z);
		} else if (tick == 1) {
			double firstxz = 0.018 - Math.abs(yp) * 0.018;
			double firsty = 0.052 + yp * 0.018;
			double remainingy = iy - firsty;
			double remainingxz = ixz - firstxz;
			return new Vector(remainingxz * x, remainingy / 0.98 + 0.08, remainingxz * z);
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
			return new Vector(remainingxz * x, remainingy / 0.98 + 0.08, remainingxz * z);
		}
	}

}
