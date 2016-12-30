package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class ShoopProject extends JavaPlugin {
	public static ArrayList<Shoop> players = new ArrayList<Shoop>();
	public static ArrayList<Lightningbolt> bolt = new ArrayList<Lightningbolt>();
	public static ArrayList<ShoopLazor> lazor = new ArrayList<ShoopLazor>();
	public static ArrayList<Cooldown> cooldown = new ArrayList<Cooldown>();
	public static ArrayList<DJump> jumps = new ArrayList<DJump>();
	public static final boolean debughitbox = false;

	public void onEnable() {
		this.getLogger().info("Shoop");
		getServer().getPluginManager().registerEvents(new EventManager(), this);
		loopsShoop();
	}

	public void onDisable() {
		for (int i = 0; i < bolt.size(); i++) {
			bolt.get(i).getStand().remove();
		}
		for (int i = 0; i < lazor.size(); i++) {
			lazor.get(i).finish();
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
				if (p.hasPermission("smash.shoop")) {
					if (!ShoopProject.isShoop(p)) {
						p.getInventory().setHelmet(ItemStackManipulation.getShoopHelmet());
						p.getInventory().setChestplate(ItemStackManipulation.getShoopChestplate());
						p.getInventory().setLeggings(ItemStackManipulation.getShoopLeggings());
						p.getInventory().setBoots(ItemStackManipulation.getShoopBoots());
						p.getInventory().setItem(0, ItemStackManipulation.getBoltItem());
						p.getInventory().setItem(1, ItemStackManipulation.getChargedItem(0));
						p.getInventory().setItem(2, ItemStackManipulation.getSmashItem(0));
						p.setAllowFlight(true);
						Cooldown c = new Cooldown(p, 2, 1799);
						cooldown.add(c);
						DJump dj = new DJump(p, 2);
						jumps.add(dj);
						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1, false, false));
						p.setWalkSpeed(0.4f);
						Shoop s = new Shoop(p);
						players.add(s);
					}
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("removeshoop")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (ShoopProject.isShoop(p)) {
					p.getInventory().setHelmet(null);
					p.getInventory().setChestplate(null);
					p.getInventory().setLeggings(null);
					p.getInventory().setBoots(null);
					p.getInventory().setItem(0, null);
					p.getInventory().setItem(1, null);
					p.getInventory().setItem(2, null);
					p.setAllowFlight(false);
					p.removePotionEffect(PotionEffectType.JUMP);
					p.setWalkSpeed(0.2f);
					p.setLevel(0);
					p.setExp(0);
					for (int i = 0; i < players.size(); i++) {
						Shoop s = players.get(i);
						if (s.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
							players.remove(i);
						}
					}
					for (int i = 0; i < jumps.size(); i++) {
						DJump dj = jumps.get(i);
						if (dj.getShoop().getUniqueId().compareTo(p.getUniqueId()) == 0) {
							jumps.remove(i);
						}
					}
					for (int i = 0; i < cooldown.size(); i++) {
						Cooldown c = cooldown.get(i);
						if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
							cooldown.remove(i);
						}
					}
				}
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
					if (c.getSkill() == 0) {
						sendCooldownMessage(c);
					} else if (c.getSkill() == 2) {
						c.getPlayer().getInventory().setItem(2, ItemStackManipulation.getSmashItem(
								(int) ((1.0 - (double) c.getTicks() / (double) c.getMaxTicks()) * 250.0)));
					}
					if (c.getTicks() < 0) {
						if (c.getSkill() == 2) {
							c.getPlayer().getInventory().setItem(2, ItemStackManipulation.getSmashItem(250));
						}
						cooldown.remove(i);
					}
				}
				// Smash
				for (int i = 0; i < lazor.size(); i++) {
					ShoopLazor sl = lazor.get(i);
					sl.setTicks(sl.getTicks() - 1);
					if (sl.getTicks() < 0) {
						sl.finish();
						lazor.remove(i);
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
					if (b.isPassive() && stand.getPassenger() == null) {
						stand.remove();
						bolt.remove(i);
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
													sendSoundPacket(b.getShoop(), "random.successful_hit",
															b.getShoop().getLocation());
													b.addHitted(le.getUniqueId());
													if (b.isPassive()) {
														addCharges(b.getShoop(), 5);
													} else {
														addCharges(b.getShoop(), 1);
													}
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
					if (ShoopProject.isShoop(p)) {
						if (p.getLevel() < 100) {
							p.setLevel(p.getLevel() + 1);
							float pro = p.getLevel() / (100 + 0.000001f);
							p.setExp(pro);
						}
					}
				}
			}
		}, 0, 2);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				// Smash
				for (int i = 0; i < lazor.size(); i++) {
					ShoopLazor sl = lazor.get(i);
					if (sl.getTicks() > 0) {
						sl.handle();
					}
				}
			}
		}, 0, 3);
	}

	public static void addCharges(Player shoop, int i) {
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

	public static void setCharges(Player shoop, int i) {
		shoop.getInventory().setItem(1, ItemStackManipulation.getChargedItem(0));
	}

	public static int getCharges(Player shoop) {
		ItemStack is = shoop.getInventory().getItem(1);
		if (is.getType() == Material.IRON_PICKAXE) {
			return 0;
		} else {
			return is.getAmount();
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
			// c.getPlayer().sendMessage("Cooldown (ms): " +
			// (System.currentTimeMillis() - c.getStart()));
			PlayerConnection con = ((CraftPlayer) c.getPlayer()).getHandle().playerConnection;
			IChatBaseComponent chat = ChatSerializer.a("{\"text\": \"" + ChatColor.GREEN + ChatColor.BOLD + "READY "
					+ ChatColor.AQUA + ChatColor.BOLD + "(Right Click!)" + "\"}");
			PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
			con.sendPacket(packet);
		}
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

	public static void sendSoundPacket(Player p, String sound, Location l) {
		PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, l.getX(), l.getY(), l.getZ(),
				1f, 1f);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	public static void sendPublicSoundPacket(String sound, Location l) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, l.getX(), l.getY(),
					l.getZ(), 1f, 1f);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static void sendPublicParticlePacket(EnumParticle type, double x, double y, double z, float vx, float vy,
			float vz, float v, int count) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendParticlePacket(p, type, x, y, z, vx, vy, vz, v, count);
		}
	}

	public static final Vector rotateAroundAxisX(Vector v, double angle) {
		double y, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		y = v.getY() * cos - v.getZ() * sin;
		z = v.getY() * sin + v.getZ() * cos;
		return v.setY(y).setZ(z);
	}

	public static final Vector rotateAroundAxisY(Vector v, double angle) {
		double x, z, cos, sin;
		cos = Math.cos(angle);
		sin = Math.sin(angle);
		x = v.getX() * cos + v.getZ() * sin;
		z = v.getX() * -sin + v.getZ() * cos;
		return v.setX(x).setZ(z);
	}

	public static void sendPublicSoundPacket(String sound, float pitch) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Location l = p.getLocation();
			PacketPlayOutNamedSoundEffect packet = new PacketPlayOutNamedSoundEffect(sound, l.getX(), l.getY(),
					l.getZ(), 1f, pitch);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}

	public static boolean isShoop(Player p) {
		for (int i = 0; i < players.size(); i++) {
			Shoop s = players.get(i);
			if (s.getPlayer().getUniqueId() == p.getUniqueId()) {
				return true;
			}
		}
		return false;
	}
}
