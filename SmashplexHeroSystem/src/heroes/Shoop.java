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
import org.bukkit.util.Vector;

import abilities.ArmorStandM;
import abilities.Cooldown;
import abilities.Lightningbolt;
import abilities.ShoopLazor;
import configuration.heroes;
import main.SmashPlayer;
import main.Smashplex;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.WorldServer;
import util.MathUtil;
import util.ParticleUtil;
import util.SoundUtil;
import util.TextUtil;

public class Shoop extends Hero {

	private int charges = 0;

	public Shoop(Player p, boolean ms) {
		super(p, "Shoop", heroes.SHOOPSMASHTICKS, 0, ms);
	}

	@Override
	public ItemStack getHelmet() {
		if (isMasterSkin()) {
			ItemStack is = new ItemStack(Material.WOOL, 1, (short) 7);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + "Shoop's Mask");
			is.setItemMeta(im);
			return is;
		} else {
			ItemStack is = new ItemStack(Material.CARPET, 1, (short) 1);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + "Shoop's Mask");
			is.setItemMeta(im);
			return is;
		}
	}

	@Override
	public ItemStack getChestplate() {
		if (isMasterSkin()) {
			ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
			is = addColor(is, Color.fromRGB(0x3C1362));
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + "Shoop's Chestpiece");
			is.setItemMeta(im);
			return is;
		} else {
			ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
			is = addColor(is, Color.fromRGB(0x009E10));
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.AQUA + "Shoop's Chestpiece");
			is.setItemMeta(im);
			return is;
		}
	}

	@Override
	public ItemStack getLeggings() {
		ItemStack is = new ItemStack(Material.LEATHER_LEGGINGS);
		is = addColor(is, Color.fromRGB(0x201C1D));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Pants");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getBoots() {
		ItemStack is = new ItemStack(Material.LEATHER_BOOTS);
		is = addColor(is, Color.fromRGB(0x009E10));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Boots");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getPrimary(int a) {
		ItemStack is = new ItemStack(Material.RAW_BEEF);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Lightning Bolt " + ChatColor.GRAY + "-" + ChatColor.AQUA + " Right Click");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.AQUA + "Right Click" + ChatColor.GRAY + " to throw a");
		lore.add(ChatColor.GRAY + "bolt of lightning towards the");
		lore.add(ChatColor.GRAY + "targeted location, dealing");
		lore.add(ChatColor.GRAY + "damage to anyone it passes");
		lore.add(ChatColor.GRAY + "through. Grants " + ChatColor.GREEN + "1" + ChatColor.GRAY + " charge of");
		lore.add(ChatColor.GREEN + "Charged Lazor " + ChatColor.GRAY + "per enemy");
		lore.add(ChatColor.GRAY + "hit. A maximum of " + ChatColor.GREEN + heroes.SHOOPMAXCHARGES + ChatColor.GRAY
				+ " charges");
		lore.add(ChatColor.GRAY + "can be stored.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getSecondary(double charges) {
		ItemStack is = new ItemStack(Material.IRON_PICKAXE, 1, (short) 250);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Charged Lazor " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [2]");
		if (charges > 0) {
			is = new ItemStack(Material.INK_SACK, (int) (charges * ((double) heroes.SHOOPMAXCHARGES) + 0.5), (short) 2);
			im.setDisplayName(ChatColor.GREEN + "Charged Lazor " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [2]");
		}
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Press" + ChatColor.AQUA + " [2]" + ChatColor.GRAY + " to consume");
		lore.add(ChatColor.GRAY + "all charges you have to fire a");
		lore.add(ChatColor.GRAY + "long-range beam, dealing damage");
		lore.add(ChatColor.GRAY + "to enemies it hits. The more");
		lore.add(ChatColor.GRAY + "charges you have, the higher the");
		lore.add(ChatColor.GRAY + "damage will be.");
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

	@Override
	public void doPrimary() {
		double pitch = ((getPlayer().getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
		double yaw = ((getPlayer().getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.cos(pitch);
		double z = Math.sin(pitch) * Math.sin(yaw);
		Vector v = new Vector(x, y, z).multiply(heroes.SHOOPBOLTSPEED);
		Location l = getPlayer().getLocation();
		double xo = Math.cos(yaw);
		double zo = -Math.sin(yaw);
		Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
		l.add(vo.getX(), -0.45, vo.getZ());
		WorldServer s = ((CraftWorld) l.getWorld()).getHandle();
		ArmorStandM fn = new ArmorStandM(s, 0);
		fn.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		fn.noclip = true;
		s.addEntity(fn);
		CraftArmorStand an = (CraftArmorStand) fn.getBukkitEntity();
		fn.motX = v.getX();
		fn.motY = v.getY();
		fn.motZ = v.getZ();
		an.setVisible(false);
		an.setHelmet(new ItemStack(Material.WOOL, 1, (short) 10));
		an.setHeadPose(an.getHeadPose().setX(getPlayer().getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
		SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.active", getPlayer().getLocation());
		Cooldown c = new Cooldown(getPlayer(), 0, heroes.SHOOPBOLTTICKS);
		TextUtil.sendCooldownMessage(c);
		Smashplex.cooldown.add(c);
		Lightningbolt bolt = new Lightningbolt(fn, v, getPlayer(), l, false);
		Smashplex.bolt.add(bolt);
	}

	@Override
	public void doSecondary() {
		if (charges > 0) {
			double pitch = ((getPlayer().getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
			double yaw = ((getPlayer().getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
			double x = Math.sin(pitch) * Math.cos(yaw);
			double y = Math.cos(pitch);
			double z = Math.sin(pitch) * Math.sin(yaw);
			Vector v = new Vector(x, y, z).normalize();
			Location b = getPlayer().getEyeLocation();
			ArrayList<UUID> hitted = new ArrayList<UUID>();
			int r = heroes.SHOOPCHARGEDRANGE * 2;
			if (charges > 4) {
				SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.chargedbeam.big", 1f);
				v.multiply(0.5);
			} else {
				SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.chargedbeam.small", 1f);
				v.multiply(0.25);
				r *= 2;
			}
			for (int i = 0; i < r; i++) {
				b.add(v);
				Block c = b.getBlock();
				if (c.getType() == Material.STEP) {
					if (b.getY() % b.getBlockY() < 0.5) {
						ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_LARGE, b.getX(), b.getY(),
								b.getZ(), 0f, 0f, 0f, 0f, 1);
						break;
					}
				} else if (c.getType().isSolid()) {
					ParticleUtil.sendPublicParticlePacket(EnumParticle.EXPLOSION_LARGE, b.getX(), b.getY(), b.getZ(),
							0f, 0f, 0f, 0f, 1);
					break;
				}
				if (b.distance(getPlayer().getEyeLocation()) >= 1.0) {
					switch (charges) {
					case 2:
						ParticleUtil.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
								-1.0f, 1f, 0.4f, 1f, 0);
						break;
					case 3:
						ParticleUtil.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
								-1.0f, 0.4f, 1f, 1f, 0);
						break;
					case 4:
						ParticleUtil.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
								-0.19999999f, 0f, 1f, 1f, 0);
						break;
					case 5:
						for (int j = 0; j < 5; j++) {
							double x1 = Math.sin(j / 5.0 * 2.0 * Math.PI) * 0.2;
							double y1 = Math.cos(j / 5.0 * 2.0 * Math.PI) * 0.2;
							Vector v1 = new Vector(y1, 0, x1);
							v1 = MathUtil.rotateAroundAxisX(v1, ((b.getPitch() + 90) / 180.0 * Math.PI));
							v1 = MathUtil.rotateAroundAxisY(v1, -(b.getYaw()) * Math.PI / 180);
							ParticleUtil.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX() + v1.getX(),
									b.getY() + v1.getY(), b.getZ() + v1.getZ(), 1f, 1f, 1f, 1f, 0);
						}
						ParticleUtil.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(), 1f,
								0f, 0f, 1f, 0);
						break;
					default:
						ParticleUtil.sendPublicParticlePacket(EnumParticle.REDSTONE, b.getX(), b.getY(), b.getZ(),
								-0.19999999f, 1f, 0f, 1f, 0);
						break;
					}
				}
			}
			charges = 0;
			getPlayer().getInventory().setItem(1, getSecondary(0.0));
			b = getPlayer().getEyeLocation();
			v.normalize().multiply(0.1);
			// Hitbox is about 2.5(h)x0.75(w)x0.75(l)
			double bonusxz = 1.0; // 0.35 player model width/length
			double bonusy = 0.8; // 1.8 player model height
			for (double j = 0; j <= heroes.SHOOPCHARGEDRANGE; j += 0.1) {
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
														if (heroes.SHOOPCHARGEDDAMAGE.length > charges - 1) {
															spt.damage(heroes.SHOOPCHARGEDDAMAGE[charges - 1]);
														}
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
		}
	}

	@Override
	public void doSmash() {
		SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.crystal", 1.0f);
		getPlayer().getInventory().setItem(2, getSmash(0));
		Cooldown c = new Cooldown(getPlayer(), 2, getSmashCooldown());
		Smashplex.cooldown.add(c);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Smashplex.getPlugin(Smashplex.class), new Runnable() {
			@Override
			public void run() {
				ShoopLazor sl = new ShoopLazor(getPlayer());
				Smashplex.lazor.add(sl);
				SoundUtil.sendPublicSoundPacket("mob.wither.death", 0.5f);
			}
		}, 45);
	}

	@Override
	public double getMeleeDamage() {
		return heroes.SHOOPMELEEDAMAGE;
	}

	@Override
	public void doDamageSound() {
		SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.pain", 1f);
	}

	@Override
	public void doDeathSound() {
		SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.smashed", 1f);
	}

	public int getCharges() {
		return this.charges;
	}

	public void setCharges(int i) {
		this.charges = i;
	}

}
