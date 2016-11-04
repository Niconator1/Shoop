package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
				p.getInventory().setHelmet(ItemStackManipulation.getShoopHelmet());
				p.getInventory().setChestplate(ItemStackManipulation.getShoopChestplate());
				p.getInventory().setLeggings(ItemStackManipulation.getShoopLeggings());
				p.getInventory().setBoots(ItemStackManipulation.getShoopBoots());
				p.getInventory().setItem(0, ItemStackManipulation.getBoltItem());
				p.getInventory().setItem(1, ItemStackManipulation.getChargedItem(0));
			}
		}
		return false;
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
													b.addHitted(le.getUniqueId());
													addCharge(b.getShoop(), 1);
													break;
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

	public static void addCharge(Player shoop, int i) {
		ItemStack is = shoop.getInventory().getItem(1);
		if (is.getType() == Material.IRON_PICKAXE) {
			if (i > 5) {
				i = 5;
			}
			shoop.getInventory().setItem(1, ItemStackManipulation.getChargedItem(i));
		} else {
			if (i + is.getAmount() > 5) {
				i = 5;
			} else {
				i += is.getAmount();
			}
			shoop.getInventory().setItem(1, ItemStackManipulation.getChargedItem(i));
		}
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
