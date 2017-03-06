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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Cooldown;
import abilities.Grenade;
import main.SmashPlayer;
import main.Smashplex;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.WorldServer;
import util.KnockbackUtil;
import util.ParticleUtil;
import util.SoundUtil;
import util.TextUtil;

public class Skullfire extends Hero {

	private static int smashticks = 1999;
	private int bullets = 7;
	private int flamejumps = 0;

	public Skullfire(Player p, boolean ms) {
		super(p, "Skullfire", smashticks, 1, ms);
		if (p != null) {
			Cooldown c = new Cooldown(p, 0, -1);
			Cooldown c2 = new Cooldown(p, 1, -1);
			TextUtil.sendCooldownMessage(c);
			Smashplex.cooldown.add(c2);
		}
	}

	@Override
	public ItemStack getHelmet() {
		if (isMasterSkin()) {
			ItemStack is = new ItemStack(Material.ANVIL, 1, (short) 2);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + "Skullfire's Mask");
			is.setItemMeta(im);
			return is;
		}
		else{
			ItemStack is = new ItemStack(Material.CARPET, 1, (short) 9);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + "Skullfire's Mask");
			is.setItemMeta(im);
			return is;
		}
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
		im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE });
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
		im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE });
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
			im.addEnchant(Enchantment.OXYGEN, 1, false);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
	public void doPrimary() {
		if (bullets > 0 && System.currentTimeMillis() - getLastShotTime() > 200) {
			if (getPlayer().isSneaking()) {
				if (bullets > 1) {
					doShot(false);
					y = 1;
					x = Bukkit.getServer().getScheduler()
							.scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Smashplex.class), new Runnable() {
								public void run() {
									if (bullets > 0 && y < 3) {
										doShot(false);
										y++;
									} else {
										Bukkit.getServer().getScheduler().cancelTask(x);
									}
								}
							}, 4L, 4L);
				} else {
					doShot(true);
				}
			} else {
				doShot(true);
			}
		}
	}

	private void doShot(boolean single) {
		double pitch = ((getPlayer().getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
		double yaw = ((getPlayer().getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.cos(pitch);
		double z = Math.sin(pitch) * Math.sin(yaw);
		Vector v = new Vector(x, y, z).normalize();
		Location b = getPlayer().getEyeLocation();
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
			if (b.distance(getPlayer().getEyeLocation()) >= 1.0) {
				ParticleUtil.sendPublicParticlePacket(EnumParticle.CRIT, b.getX(), b.getY(), b.getZ(), 0f, 0f, 0f, 0f,
						0);
			}
		}
		if (bullets <= 1) {
			SoundUtil.sendSoundPacket(getPlayer(), "melee.reload", getPlayer().getLocation());
			Cooldown c = new Cooldown(getPlayer(), 0, 40);
			Smashplex.cooldown.add(c);
		} else {
			getPlayer().getInventory().setItem(0, getPrimary(bullets - 1));
		}
		bullets -= 1;
		setLastShotTime(System.currentTimeMillis());
		b = getPlayer().getEyeLocation();
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
					if (le.getUniqueId().compareTo(getPlayer().getUniqueId()) != 0) {
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
										SoundUtil.sendSoundPacket(getPlayer(), "random.successful_hit",
												getPlayer().getLocation());
										hitted.add(le.getUniqueId());
										if (le instanceof Player) {
											Player target = (Player) le;
											SmashPlayer spt = Smashplex.getSmashPlayer(target);
											if (spt != null) {
												if (spt.getSelectedHero() != null) {
													int reduction = (int) (enemy.distance(getPlayer().getLocation())
															/ 20.0);
													if (reduction > 3) {
														reduction = 3;
													}
													spt.damage(4 - reduction);
													// TODO: KB
													didhit = true;
													if (single) {
														flamejumps = 1;
													} else {
														if (flamejumps < 2) {
															flamejumps++;
														}
													}
													flamejumps++;
													getPlayer().setAllowFlight(true);
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
			if (getPlayer().getExp() < 0.6) {
				getPlayer().setExp(getPlayer().getExp() + 0.33f);
				getPlayer().setLevel((int) (getPlayer().getExp() * 100.0));
			} else {
				getPlayer().setExp(0);
				getPlayer().setLevel(0);
				getPlayer().getInventory().setItem(1, getSecondary(1.0));
				for (int i = 0; i < Smashplex.cooldown.size(); i++) {
					Cooldown c = Smashplex.cooldown.get(i);
					if (c.getPlayer().getUniqueId().compareTo(getPlayer().getUniqueId()) == 0) {
						if (c.getSkill() == 1) {
							Smashplex.cooldown.remove(i);
						}
					}
				}
			}
		} else {
			getPlayer().setExp(0);
			getPlayer().setLevel(0);
		}
	}

	@Override
	public void doSecondary() {
		double pitch = ((getPlayer().getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
		double yaw = ((getPlayer().getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.cos(pitch);
		double z = Math.sin(pitch) * Math.sin(yaw);
		Location l = getPlayer().getLocation();
		Vector v = new Vector(1.8 * x, 0.2 + y * 1.8, 1.8 * z);
		WorldServer s = ((CraftWorld) l.getWorld()).getHandle();
		ArmorStandM fn = new ArmorStandM(s, 4);
		fn.setLocation(l.getX(), l.getY() + 0.52, l.getZ(), l.getYaw(), l.getPitch());
		fn.noclip = true;
		fn.motX = v.getX();
		fn.motY = v.getY();
		fn.motZ = v.getZ();
		s.addEntity(fn);
		KnockbackUtil.changeEntityTrack(fn, 2);
		CraftArmorStand an = (CraftArmorStand) fn.getBukkitEntity();
		an.setVisible(false);
		an.setHelmet(new ItemStack(Material.PUMPKIN, 1));
		an.setHeadPose(an.getHeadPose().setX(getPlayer().getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
		SoundUtil.sendPublicSoundPacket("Skullfire.grenadethrow", getPlayer().getLocation());
		Cooldown c = new Cooldown(getPlayer(), 1, 200);
		Smashplex.cooldown.add(c);
		Grenade nade = new Grenade(fn, getPlayer().getLocation().getPitch(), getPlayer().getLocation().getYaw(),
				getPlayer(), l);
		Smashplex.nade.add(nade);
	}

	@Override
	public void doSmash() {
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

	public int getFlameJumps() {
		return this.flamejumps;
	}

	public void setFlameJumps(int i) {
		flamejumps = i;
	}

	@Override
	public void giveItems() {
		super.giveItems();
		getPlayer().getInventory().setItem(0, getPrimary(bullets));
	}

	public int getBullets() {
		return this.bullets;
	}

	public void setBullets(int i) {
		this.bullets = i;
	}

}
