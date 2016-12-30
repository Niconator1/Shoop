package util;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import abilities.Cooldown;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;

public class TextUtil {
	public static void sendCooldownMessage(Cooldown c) {
		if (c.getTicks() > -1) {
			int per = (int) (c.getTicks() * 1.0 / c.getMaxTicks() * 12.0 + 0.5);
			String part = ChatColor.GREEN + "";
			for (int j = 0; j < 12 - per; j++) {
				part += "█";
			}
			part += ChatColor.RED;
			for (int j = 0; j < per; j++) {
				part += "█";
			}
			double time = (Math.round(c.getTicks() / 2.0)) / 10.0;
			PlayerConnection con = ((CraftPlayer) c.getPlayer()).getHandle().playerConnection;
			IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + ChatColor.DARK_GRAY + "[" + part
					+ ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + time + "s" + "\"}");
			PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
			con.sendPacket(packet);
		} else {
			PlayerConnection con = ((CraftPlayer) c.getPlayer()).getHandle().playerConnection;
			IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + ChatColor.GREEN + ChatColor.BOLD + "READY "
					+ ChatColor.AQUA + ChatColor.BOLD + "(Right Click!)" + "\"}");
			PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
			con.sendPacket(packet);
		}
	}
}
