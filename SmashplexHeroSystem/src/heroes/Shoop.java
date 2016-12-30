package heroes;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import abilities.Cooldown;
import abilities.Lightningbolt;
import main.Hero;
import main.Smashplex;
import util.SoundUtil;
import util.TextUtil;

public class Shoop extends Hero {

	public Shoop() {
		super("Shoop");
	}

	@Override
	public ItemStack getHelmet() {
		ItemStack is = new ItemStack(Material.CARPET, 1, (short) 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Mask");
		is.setItemMeta(im);
		return is;
	}

	@Override
	public ItemStack getChestplate() {
		ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
		is = addColor(is, Color.fromRGB(0x009E10));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Chestpiece");
		is.setItemMeta(im);
		return is;
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
	public ItemStack getPrimary() {
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
		lore.add(ChatColor.GRAY + "hit. A maximum of " + ChatColor.GREEN + "5" + ChatColor.GRAY + " charges");
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
			is = new ItemStack(Material.INK_SACK, (int) (charges * 5.0 + 0.5), (short) 2);
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
		if (loaded > 249) {
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

	@Override
	public void doPrimary(Player p) {
		double pitch = ((p.getLocation().getPitch() + 90.0) * Math.PI) / 180.0;
		double yaw = ((p.getLocation().getYaw() + 90.0) * Math.PI) / 180.0;
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.cos(pitch);
		double z = Math.sin(pitch) * Math.sin(yaw);
		Vector v = new Vector(x, y, z).multiply(2.5);
		Location l = p.getLocation();
		double xo = Math.cos(yaw);
		double zo = -Math.sin(yaw);
		Vector vo = new Vector(zo, 0, xo).normalize().multiply(0.35);
		l.add(vo.getX(), -0.45, vo.getZ());
		ArmorStand f = (ArmorStand) p.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
		CraftArmorStand a = (CraftArmorStand) f;
		a.getHandle().noclip = true;
		f.setVelocity(v);
		f.setVisible(false);
		f.setHelmet(new ItemStack(Material.WOOL, 1, (short) 10));
		f.setHeadPose(f.getHeadPose().setX(p.getLocation().getPitch() / 90.0 * 0.5 * Math.PI));
		SoundUtil.sendPublicSoundPacket("ShoopDaWhoop.active", p.getLocation());
		Cooldown c = new Cooldown(p, 0, 5);
		TextUtil.sendCooldownMessage(c);
		Smashplex.cooldown.add(c);
		Lightningbolt bolt = new Lightningbolt(f, v, p, l, false);
		Smashplex.bolt.add(bolt);
	}

}
