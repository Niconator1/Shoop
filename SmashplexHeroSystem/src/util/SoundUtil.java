package util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;

public class SoundUtil {
	public static void sendPublicSoundPacket(String sound, float pitch) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Location l = p.getLocation();
			PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, l.getX(), l.getY(),
					l.getZ(), 1f, pitch);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void sendPublicSoundPacket(String sound, Location l) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, l.getX(), l.getY(),
					l.getZ(), 1f, 1f);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void sendSoundPacket(Player p, String sound, Location l) {
		PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, l.getX(), l.getY(), l.getZ(),
				1f, 1f);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
