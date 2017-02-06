package heroes;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Cooldown;
import abilities.Grenade;
import main.Hero;
import main.SmashPlayer;
import main.Smashplex;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.WorldServer;
import util.ParticleUtil;
import util.SoundUtil;

public class Skullfire extends Hero {

	private static int smashticks = 2000;

	public Skullfire() {
		super("Skullfire", smashticks);
	}

	@Override
	public ItemStack getHelmet() {
		ItemStack is = new ItemStack(Material.CARPET, 1, (short) 9);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Skullfire's Mask");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getChestplate() {
		ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
		is = addColor(is, Color.fromRGB(0x1E1E1E));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Skullfire's Chestpiece");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getLeggings() {
		ItemStack is = new ItemStack(Material.LEATHER_LEGGINGS);
		is = addColor(is, Color.fromRGB(0x1E1E1E));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Skullfire's Pants");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getBoots() {
		ItemStack is = new ItemStack(Material.LEATHER_BOOTS);
		is = addColor(is, Color.fromRGB(0x1E1E1E));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Skullfire's Boots");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getPrimary(int amount) {
		ItemStack is = new ItemStack(Material.DIAMOND_PICKAXE, amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Deagle " + ChatColor.GRAY + "-" + ChatColor.AQUA + " Right Click");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.AQUA + "Right Click" + ChatColor.GRAY + " to fire the");
		lore.add(ChatColor.GRAY + "Desert Eagle and deal damage");
		lore.add(ChatColor.GRAY + "instantly to players from a.");
		lore.add(ChatColor.GRAY + "distance. Hold" + ChatColor.AQUA + " [Sneak] " + ChatColor.GRAY + "to");
		lore.add(ChatColor.GRAY + "fire a triple-shot. Damage is");
		lore.add(ChatColor.GRAY + "reduced at longer range.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getSecondary(double loaded) {
		ItemStack is = new ItemStack(Material.IRON_PICKAXE, 1, (short) (250 - (int) (loaded * 250.0 + 0.5)));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Grenade " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [2]");
		if (loaded >= 1.0) {
			is = new ItemStack(Material.INK_SACK, 1, (short) 2);
			im.setDisplayName(ChatColor.GREEN + "Grenade " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [2]");
		}
		ArrayList<String> lore = new ArrayList<String>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Press" + ChatColor.AQUA + " [2]" + ChatColor.GRAY + " to throw a");
        lore.add(ChatColor.GRAY + "grenade that will explode");
        lore.add(ChatColor.GRAY + "instantly upon impact with a");
        lore.add(ChatColor.GRAY + "player or a block.");
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

	@Override
	public ItemStack getSmash(double loaded) {
		ItemStack is = new ItemStack(Material.IRON_SPADE, 1, (short) (250 - (int) (loaded * 250.0 + 0.5)));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "FIRIN' MAH LAZOR " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [3]");
		if (loaded >= 1.0) {
			is = new ItemStack(Material.INK_SACK, 1, (short) 14);
			im.setDisplayName(ChatColor.GREEN + "FIRIN' MAH LAZOR " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [3]");
		}
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Smash Ability");
		lore.add("");
		lore.add(ChatColor.GRAY + "Press" + ChatColor.AQUA + " [3]" + ChatColor.GRAY + " to unleash");
		lore.add(ChatColor.GRAY + "a devastating lazor.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	int x = 0;
	int y = 1;

	@Override
	public void doPrimary(SmashPlayer sp) {
		Player p = sp.getPlayer();
		if (sp.getCharges() > 0 && System.currentTimeMillis() - sp.lastshottime > 200) {
			if (p.isSneaking()) {
				if (sp.getCharges() > 1) {
					doShot(sp);
					y = 1;
					x = Bukkit.getServer().getScheduler()
							.scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class), new Runnable() {
								public void run() {
									if (sp.getCharges() > 0 && y < 3) {
										doShot(sp);
										y++;
									} else {
										Bukkit.getServer().getScheduler().cancelTask(x);
									}
								}
							}, 4L, 4L);
				} else {
					doShot(sp);
				}
			} else {
				doShot(sp);
			}
		}
	}

	private void doShot(SmashPlayer sp) {
		Player p = sp.getPlayer();
		double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
		double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.cos(pitch);
		double z = Math.sin(pitch) * Math.sin(yaw);
		Vector v = new Vector(x, y, z).normalize();
		Location b = p.getEyeLocation();
		ArrayList<UUID> hitted = new ArrayList<UUID>();
		int r = (int) (100.0 * 1.0 / 0.75);
		SoundUtil.sendPublicSoundPacket("Skullfire.magnumshot", 1f);
		v.multiply(0.75);
		for (int i = 0; i < r; i++) {
			b.add(v);
			Block c = b.getBlock();
			if (c.getType() == Material.STEP) {
				if (b.getY() % b.getBlockY() < 0.5) {
					break;
				}
			} else if (c.getType().isSolid()) {
				break;
			}
			if (b.distance(p.getEyeLocation()) >= 1.0) {
				ParticleUtil.sendPublicParticlePacket(EnumParticle.CRIT, b.getX(), b.getY(), b.getZ(), 0f, 0f, 0f, 0f,
						0);
			}
		}
		if (sp.getCharges() <= 1) {
			SoundUtil.sendSoundPacket(p, "melee.reload", p.getLocation());
			Cooldown c = new Cooldown(p, 0, 40);
			Smashplex.cooldown.add(c);
		} else {
			p.getInventory().setItem(0, getPrimary(sp.getCharges() - 1));
		}
		sp.setCharges(sp.getCharges() - 1);
		sp.lastshottime = System.currentTimeMillis();
		b = p.getEyeLocation();
		v.normalize().multiply(0.1);
		// Hitbox is about 3.8(h)x1.0(w)x1.0(l)
		double bonusxz = 0.7; // 0.35 player model width/length
		double bonusy = 0.5; // 1.8 player model height
		boolean didhit = false;
		for (double j = 0; j <= 100; j += 0.1) {
			Location midm = b.add(v);
			Block c = midm.getBlock();
			if (c.getType() == Material.STEP) {
				if (b.getY() % b.getBlockY() < 0.5) {
					break;
				}
			} else if (c.getType().isSolid()) {
				break;
			}
			for (LivingEntity le : b.getWorld().getLivingEntities()) {
				if (!(le instanceof ArmorStand)) {
					if (le.getUniqueId().compareTo(p.getUniqueId()) != 0) {
						boolean canhit = true;
						for (int i = 0; i < hitted.size(); i++) {
							if (hitted.get(i).compareTo(le.getUniqueId()) == 0) {
								canhit = false;
							}
						}
						if (canhit) {
							Location enemy = le.getLocation();
							if (Math.abs(enemy.getX() - midm.getX()) <= 0.35 + bonusxz) {
								if (Math.abs(enemy.getZ() - midm.getZ()) <= 0.35 + bonusxz) {
									double dy = enemy.getY() - midm.getY();
									if (dy >= -1.8 - bonusy && dy <= 0 + bonusy) {
										SoundUtil.sendSoundPacket(p, "random.successful_hit", p.getLocation());
										hitted.add(le.getUniqueId());
										if (le instanceof Player) {
											Player target = (Player) le;
											SmashPlayer spt = Smashplex.getSmashPlayer(target);
											if (spt != null) {
												if (spt.getSelectedHero() != -1) {
													int reduction = (int) (enemy.distance(p.getLocation()) / 20.0);
													if (reduction > 3) {
														reduction = 3;
													}
													spt.damage(4 - reduction);
													//TODO: KB
													didhit = true;
													sp.setFlameJump(true);
													p.setAllowFlight(true);
												}
											}
										}
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		if (didhit) {
			if (p.getExp() < 0.6) {
				p.setExp(p.getExp() + 0.33f);
				p.setLevel((int) (p.getExp() * 100.0));
			} else {
				p.setExp(0);
				p.setLevel(0);
				p.getInventory().setItem(1, sp.getHero().getSecondary(1.0));
				for (int i = 0; i < Smashplex.cooldown.size(); i++) {
					Cooldown c = Smashplex.cooldown.get(i);
					if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
						if (c.getSkill() == 1) {
							Smashplex.cooldown.remove(i);
						}
					}
				}
			}
		} else {
			p.setExp(0);
			p.setLevel(0);
		}
	}

	@Override
	public void doSecondary(SmashPlayer sp) {
		Player p = sp.getPlayer();
		double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
		double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.cos(pitch);
		double z = Math.sin(pitch) * Math.sin(yaw);
		Location l = p.getLocation();
		Vector v = new Vector(1.8 * x, 0.2 + y * 1.8, 1.8 * z);
		Vector vm = new Vector(v.getX(), v.getY() / 0.98 + 0.08, v.getZ());
		WorldServer s = ((CraftWorld) l.getWorld()).getHandle();
		ArmorStandM fn = new ArmorStandM(s);
		fn.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		fn.noclip = true;
		s.addEntity(fn);
		CraftArmorStand an = (CraftArmorStand) fn.getBukkitEntity();
		an.setVelocity(vm);
		an.setVisible(false);
		an.setHelmet(new ItemStack(Material.PUMPKIN, 1));
		an.setHeadPose(an.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
		SoundUtil.sendPublicSoundPacket("Skullfire.grenadethrow", p.getLocation());
		Cooldown c = new Cooldown(p, 1, 200);
		Smashplex.cooldown.add(c);
		Grenade nade = new Grenade(fn, p.getLocation().getPitch(), p.getLocation().getYaw(), p, l);
		Smashplex.nade.add(nade);
	}

	@Override
	public void doSmash(SmashPlayer sp) {
	}

	@Override
	public double getMeleeDamage() {
		return 2.0;
	}

	@Override
	public void doDamageSound() {
		SoundUtil.sendPublicSoundPacket("Skullfire.pain", 1f);
	}

	@Override
	public void doDeathSound() {
		SoundUtil.sendPublicSoundPacket("Skullfire.smashed", 1f);
	}

}
