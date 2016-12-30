package main;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ItemStackManipulation {
	public static ItemStack getBoltItem() {
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

	public static ItemStack getChargedItem(int charges) {
		ItemStack is = new ItemStack(Material.IRON_PICKAXE, 1, (short) 250);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.RED + "Charged Lazor " + ChatColor.GRAY + "-" + ChatColor.AQUA + " [2]");
		if (charges > 0) {
			is = new ItemStack(Material.INK_SACK, charges, (short) 2);
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

	public static ItemStack getSmashItem(int loaded) {
		ItemStack is = new ItemStack(Material.IRON_SPADE, 1, (short) (250 - loaded));
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

	public static ItemStack getShoopHelmet() {
		ItemStack is = new ItemStack(Material.CARPET, 1, (short) 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Mask");
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack getShoopChestplate() {
		ItemStack is = new ItemStack(Material.LEATHER_CHESTPLATE);
		is = addColor(is, Color.fromRGB(0x009E10));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Chestpiece");
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack getShoopLeggings() {
		ItemStack is = new ItemStack(Material.LEATHER_LEGGINGS);
		is = addColor(is, Color.fromRGB(0x201C1D));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Pants");
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack getShoopBoots() {
		ItemStack is = new ItemStack(Material.LEATHER_BOOTS);
		is = addColor(is, Color.fromRGB(0x009E10));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.AQUA + "Shoop's Boots");
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack addColor(ItemStack item, Color c) {
		LeatherArmorMeta lch = (LeatherArmorMeta) item.getItemMeta();
		lch.setColor(c);
		item.setItemMeta(lch);
		return item;
	}

}
