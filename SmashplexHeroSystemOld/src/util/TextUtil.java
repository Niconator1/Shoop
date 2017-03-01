package util;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import abilities.Cooldown;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

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

	public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
		CraftPlayer craftplayer = (CraftPlayer) player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;
		IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay,
				fadeOut);
		connection.sendPacket(titlePacket);
	}

	public static void sendSubTitle(Player player, String subtitle, int fadeIn, int stay, int fadeOut) {
		CraftPlayer craftplayer = (CraftPlayer) player;
		PlayerConnection connection = craftplayer.getHandle().playerConnection;
		IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON, fadeIn, stay,
				fadeOut);
		connection.sendPacket(subtitlePacket);
	}
}
