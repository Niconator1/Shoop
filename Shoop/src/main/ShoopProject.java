package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_10_R1.EnumParticle;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutCustomSoundEffect;
import net.minecraft.server.v1_10_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_10_R1.PlayerConnection;
import net.minecraft.server.v1_10_R1.SoundCategory;

public class ShoopProject extends JavaPlugin {
	public static ArrayList<Lightningbolt> bolt = new ArrayList<Lightningbolt>();
	public static ArrayList<Cooldown> cooldown = new ArrayList<Cooldown>();
	public static final boolean debughitbox = false;

	public void onEnable() {
		this.getLogger().info("Shoop");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		loopsShoop();
	}

	public void onDisable() {
		for (int i = 0; i < bolt.size(); i++) {
			bolt.get(i).getStand().remove();
			bolt.remove(i);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("remove")) {
			for (int i = 0; i < bolt.size(); i++) {
				bolt.get(i).getStand().remove();
				bolt.remove(i);
			}
		} else if (cmd.getName().equalsIgnoreCase("shoop")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				p.getInventory().setHelmet(new ItemStack(Material.CARPET, 1, (short) 1));
				p.getInventory()
						.setChestplate(addColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.fromRGB(0x009E10)));
				p.getInventory()
						.setLeggings(addColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.fromRGB(0x201C1D)));
				p.getInventory().setBoots(addColor(new ItemStack(Material.LEATHER_BOOTS), Color.fromRGB(0x009E10)));
				p.getInventory().setItem(0, getBoltItem());
			}
		}
		return false;
	}

	private ItemStack getBoltItem() {
		ItemStack is = new ItemStack(Material.RAW_BEEF);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Lightning Bolt " + ChatColor.GRAY + "-" + ChatColor.AQUA + " Right Click");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.AQUA + "Right Click " + ChatColor.GRAY + "to throw a");
		lore.add(ChatColor.GRAY + "bolt of lightning towards the");
		lore.add(ChatColor.GRAY + "targeted location, dealing");
		lore.add(ChatColor.GRAY + "damage to anyone it passes");
		lore.add(ChatColor.GRAY + "through. Grants " + ChatColor.GREEN + "1" + ChatColor.GRAY + " charge of");
		lore.add(ChatColor.GREEN + "Charged Lazor " + ChatColor.GRAY + "per enemy");
		lore.add(ChatColor.GRAY + "hit. A maximum of " + ChatColor.GREEN + "5" + ChatColor.GRAY + " charges");
		lore.add(ChatColor.GRAY + "can be stored.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	private void loopsShoop() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@SuppressWarnings("unused")
			public void run() {
				// Projectile Gravitation disabling
				for (int i = 0; i < bolt.size(); i++) {
					bolt.get(i).getStand().setVelocity(bolt.get(i).getVector());
				}
				// Cooldown decrease
				for (int i = 0; i < cooldown.size(); i++) {
					Cooldown c = cooldown.get(i);
					c.setTicks(c.getTicks() - 1);
					sendCooldownMessage(c);
					if (c.getTicks() < 0) {
						cooldown.remove(i);
					}
				}
				// Block collision check
				for (int i = 0; i < bolt.size(); i++) {
					Lightningbolt b = bolt.get(i);
					ArmorStand stand = b.getStand();
					Location mid = stand.getEyeLocation();
					double bonuslengthf = 0.75;
					double bonuslengthb = -1.5;
					for (double j = bonuslengthf; j >= bonuslengthb; j -= 0.05) {
						Vector dir = b.getVector().clone().normalize().multiply(j);
						Location midm = mid.clone().add(dir);
						Block c = midm.getBlock();
						if (c.getType().isSolid()) {
							if (b.isPassive()) {
								b.getStand().setPassenger(null);
							}
							stand.remove();
							bolt.remove(i);
							break;
						}
					}
				}
				// Hit registration
				for (int i = 0; i < bolt.size(); i++) {
					Lightningbolt b = bolt.get(i);
					ArmorStand stand = b.getStand();
					Location mid = stand.getEyeLocation();
					// Hitbox is about 2.5(h)x0.75(w)x0.75(l)
					double bonusxz = 0.4; // 0.35 player model width/length
					double bonusy = 0.55; // 1.8 player model height
					double bonuslengthf = 0.75;
					double bonuslengthb = -1.5;
					for (LivingEntity le : stand.getWorld().getLivingEntities()) {
						if (!(le instanceof ArmorStand)) {
							if (le.getUniqueId().compareTo(b.getShoop().getUniqueId()) != 0) {
								if (b.canHit(le)) {
									Location enemy = le.getLocation();
									for (double j = bonuslengthf; j >= bonuslengthb; j -= 0.05) {
										Vector dir = b.getVector().clone().normalize().multiply(j);
										Location midm = mid.clone().add(dir);
										if (Math.abs(enemy.getX() - midm.getX()) <= 0.35 + bonusxz) {
											if (Math.abs(enemy.getZ() - midm.getZ()) <= 0.35 + bonusxz) {
												double dy = enemy.getY() - midm.getY();
												if (dy >= -1.8 - bonusy && dy <= 0 + bonusy) {
													sendSoundPacket(b.getShoop(), "entity.arrow.hit_player",
															b.getShoop().getLocation());
													b.addHitted(le);
												}
											}
										}
									}
									if (enemy.distance(b.getShoop().getLocation()) < 5 && debughitbox == true) {
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME, enemy.clone()
												.add(new Vector(0.35 + bonusxz, 1.8 + bonusy, -0.35 - bonusxz)), 1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME, enemy.clone()
												.add(new Vector(-0.35 - bonusxz, 1.8 + bonusy, -0.35 - bonusxz)), 1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME, enemy.clone()
												.add(new Vector(0.35 + bonusxz, 1.8 + bonusy, 0.35 + bonusxz)), 1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME, enemy.clone()
												.add(new Vector(-0.35 - bonusxz, 1.8 + bonusy, 0.35 + bonusxz)), 1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME,
												enemy.clone().add(new Vector(0.35 + bonusxz, -bonusy, -0.35 - bonusxz)),
												1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME, enemy.clone()
												.add(new Vector(-0.35 - bonusxz, -bonusy, -0.35 - bonusxz)), 1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME,
												enemy.clone().add(new Vector(0.35 + bonusxz, -bonusy, 0.35 + bonusxz)),
												1);
										sendParticlePacket(b.getShoop(), EnumParticle.FLAME,
												enemy.clone().add(new Vector(-0.35 - bonusxz, -bonusy, 0.35 + bonusxz)),
												1);
									}
								}
							}
						}
					}
					if (b.isPassive()) {
						if (mid.distance(b.start()) >= 55) {
							b.getStand().setPassenger(null);
							b.getStand().remove();
							bolt.remove(i);
						}
					} else {
						if (mid.distance(b.start()) > 70) {
							b.getStand().remove();
							bolt.remove(i);
						}
					}
				}
			}
		}, 0, 1);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getLevel() < 100) {
						p.setLevel(p.getLevel() + 1);
						float pro = p.getLevel() / (100 + 0.000001f);
						p.setExp(pro);
					}
				}
			}
		}, 0, 2);
	}

	public ItemStack addColor(ItemStack item, Color c) {
		LeatherArmorMeta lch = (LeatherArmorMeta) item.getItemMeta();
		lch.setColor(c);
		item.setItemMeta(lch);
		return item;
	}

	public static boolean isBoltReady(Player p) {
		for (int i = 0; i < cooldown.size(); i++) {
			Cooldown c = cooldown.get(i);
			if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				if (c.getSkill() == 0) {
					return false;
				}
			}
		}
		return true;
	}

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
			c.getPlayer().sendMessage("Cooldown (ms): " + (System.currentTimeMillis() - c.getStart()));
			PlayerConnection con = ((CraftPlayer) c.getPlayer()).getHandle().playerConnection;
			IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + ChatColor.GREEN + ChatColor.BOLD + "READY "
					+ ChatColor.AQUA + ChatColor.BOLD + "(Right Click!)" + "\"}");
			PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
			con.sendPacket(packet);
		}
	}

	public static void sendSoundPacket(Player p, String sound, Location l) {
		PacketPlayOutCustomSoundEffect packet = new PacketPlayOutCustomSoundEffect(sound, SoundCategory.MASTER,
				l.getX(), l.getY(), l.getZ(), 1f, 1f);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	public static void sendParticlePacket(Player p, EnumParticle type, Location l, int count) {
		sendParticlePacket(p, type, l.getX(), l.getY(), l.getZ(), 0, 0, 0, 0, count);

	}

	public static void sendParticlePacket(Player p, EnumParticle type, double x, double y, double z, float vx, float vy,
			float vz, float v, int count) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(type, true, (float) x, (float) y,
				(float) z, vx, vy, vz, v, count, null);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
}
