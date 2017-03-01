package main;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class NPC {

	private int entityID;
	private int nameID;
	private ItemStack[] inventory = new ItemStack[5];
	private Location location;
	private GameProfile gameprofile;
	private float headrot;
	private String value;
	private String signature;

	public NPC(String name, Location location, float headrot, String value, String signature) {
		entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
		nameID = entityID + 1;
		gameprofile = new GameProfile(UUID.randomUUID(), name);
		this.headrot = headrot;
		this.location = location.clone();
		this.signature = signature;
		this.value = value;
	}

	public void changeSkin(String value, String signature) {
		gameprofile.getProperties().removeAll("textures");
		gameprofile.getProperties().put("textures", new Property("textures", value, signature));
	}

	public void setItemStack(int slot, org.bukkit.inventory.ItemStack is) {
		if (slot < inventory.length && slot >= 0) {
			inventory[slot] = CraftItemStack.asNMSCopy(is);
		}
	}

	public void spawnGlobal() {
		changeSkin(value, signature);
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
		setValue(packet, "a", entityID);
		setValue(packet, "b", gameprofile.getId());
		setValue(packet, "c", getFixLocation(location.getX()));
		setValue(packet, "d", getFixLocation(location.getY()));
		setValue(packet, "e", getFixLocation(location.getZ()));
		setValue(packet, "f", getFixRotation(location.getYaw()));
		setValue(packet, "g", getFixRotation(location.getPitch()));
		setValue(packet, "h", 0);
		DataWatcher w = new DataWatcher(null);
		w.a(3, (byte) 0);
		w.a(6, (float) 20);
		w.a(10, (byte) 127);
		setValue(packet, "i", w);
		addToTablist();
		sendPacket(packet);
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				PacketPlayOutEntityEquipment packet2 = new PacketPlayOutEntityEquipment(entityID, i, inventory[i]);
				sendPacket(packet2);
			}
		}
		PacketPlayOutEntityHeadRotation packet3 = new PacketPlayOutEntityHeadRotation();
		setValue(packet3, "a", entityID);
		setValue(packet3, "b", getFixRotation(headrot));
		sendPacket(packet3);
		PacketPlayOutSpawnEntityLiving packet4 = new PacketPlayOutSpawnEntityLiving();
		setValue(packet4, "a", nameID);
		setValue(packet4, "b", (byte) 30);
		setValue(packet4, "c", getFixLocation(location.getX()));
		setValue(packet4, "d", getFixLocation(location.getY() + 0.40625));
		setValue(packet4, "e", getFixLocation(location.getZ()));
		setValue(packet4, "i", getFixRotation(location.getYaw()));
		setValue(packet4, "j", getFixRotation(location.getPitch()));
		setValue(packet4, "k", getFixRotation(headrot));
		DataWatcher w2 = new DataWatcher(null);
		w2.a(0, (byte) 0x20);
		w2.a(2, ChatColor.GREEN + "Shoop" + ChatColor.RESET);
		w2.a(3, (byte) 1);
		w2.a(6, (float) 20);
		setValue(packet4, "l", w2);
		sendPacket(packet4);
	}

	public void spawn(Player p) {
		changeSkin(value, signature);
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
		setValue(packet, "a", entityID);
		setValue(packet, "b", gameprofile.getId());
		setValue(packet, "c", getFixLocation(location.getX()));
		setValue(packet, "d", getFixLocation(location.getY()));
		setValue(packet, "e", getFixLocation(location.getZ()));
		setValue(packet, "f", getFixRotation(location.getYaw()));
		setValue(packet, "g", getFixRotation(location.getPitch()));
		setValue(packet, "h", 0);
		DataWatcher w = new DataWatcher(null);
		w.a(3, (byte) 0);
		w.a(6, (float) 20);
		w.a(10, (byte) 127);
		setValue(packet, "i", w);
		addToTablist(p);
		sendPacket(packet, p);
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				PacketPlayOutEntityEquipment packet2 = new PacketPlayOutEntityEquipment(entityID, i, inventory[i]);
				sendPacket(packet2, p);
			}
		}
		PacketPlayOutEntityHeadRotation packet3 = new PacketPlayOutEntityHeadRotation();
		setValue(packet3, "a", entityID);
		setValue(packet3, "b", getFixRotation(headrot));
		sendPacket(packet3, p);
		PacketPlayOutSpawnEntityLiving packet4 = new PacketPlayOutSpawnEntityLiving();
		setValue(packet4, "a", nameID);
		setValue(packet4, "b", (byte) 30);
		setValue(packet4, "c", getFixLocation(location.getX()));
		setValue(packet4, "d", getFixLocation(location.getY() + 0.40625));
		setValue(packet4, "e", getFixLocation(location.getZ()));
		setValue(packet4, "i", getFixRotation(location.getYaw()));
		setValue(packet4, "j", getFixRotation(location.getPitch()));
		setValue(packet4, "k", getFixRotation(headrot));
		DataWatcher w2 = new DataWatcher(null);
		w2.a(0, (byte) 0x20);
		w2.a(2, ChatColor.GREEN + "Shoop" + ChatColor.RESET);
		w2.a(3, (byte) 1);
		w2.a(6, (float) 20);
		setValue(packet4, "l", w2);
		sendPacket(packet4, p);
	}

	public void destroyGlobal() {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { entityID, nameID });
		rmvFromTablist();
		sendPacket(packet);
	}

	public void destroy(Player p) {
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { entityID, nameID });
		rmvFromTablist(p);
		sendPacket(packet, p);
	}

	public void addToTablist() {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET,
				CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
				packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
	}

	public void addToTablist(Player p) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET,
				CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
				packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet, p);
	}

	public void rmvFromTablist() {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET,
				CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
				packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet);
	}

	public void rmvFromTablist(Player p) {
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
		PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET,
				CraftChatMessage.fromString(gameprofile.getName())[0]);
		@SuppressWarnings("unchecked")
		List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(
				packet, "b");
		players.add(data);

		setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
		setValue(packet, "b", players);

		sendPacket(packet, p);
	}

	public int getFixLocation(double pos) {
		return (int) MathHelper.floor(pos * 32.0D);
	}

	public byte getFixRotation(float yawpitch) {
		return (byte) ((int) (yawpitch * 256.0F / 360.0F));
	}

	public void setValue(Object obj, String name, Object value) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
		}
	}

	public Object getValue(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
		}
		return null;
	}

	public void sendPacket(Packet<?> packet, Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void sendPacket(Packet<?> packet) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			sendPacket(packet, player);
		}
	}
}