package main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import heroes.Shoop;

public class NPCRegistry {

	public static void registerShoop() {
		NPC shoop = new NPC("i13u1i3u12i3u1",
				new Location(Bukkit.getWorld("lobby"), 839.5, 39.5, 62.5, 82.96875f, 7.03125f), 82.96875f,
				"eyJ0aW1lc3RhbXAiOjE0NDMyMzE4ODgzNDksInByb2ZpbGVJZCI6ImFhZDIzYTUwZWVkODQ3MTA5OWNmNjRiZThmZjM0ZWY0IiwicHJvZmlsZU5hbWUiOiIxUm9ndWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UwMjE2MTJlMjEwNDZkNDY4OGIyMjhjYjM3MDJiYjZhZWM3YTA4Yjc4YjBmNTYwMTljMDRmYWUyYWQyOTQyZCJ9fX0=",
				"yRVtcYcMOKmDtUkY0c1ruzkJQVswrCExRrG8He5qKUltUT1RI+CMlhjovdEfxfwXRXWQz9BTmfrVlhEwBu7NIfWfimj8twotmGgcJ4ZLSGlkyc/VMwrIMUKzZowvIObQqEWenBQYW3uSfYKFStyi6jCzNOmY4fkCc3VVPBGKKdXOVWo9vGVg63tUjLJZlBSL9r0Cr6IUn7lAQg7cGwGxyikh6/B6tCrpr8/ssJJWJnyDw/rcwnbALJfZVvxAEipA3qA47u3FUntN0CNQIzdVi/y+nNRH+jmyDmV5CSDGELCODrXF1ll/R5gYKn3ZJuSBuY6PjdvpDBr9EeKhD7EFNjTrMqIUoWu+KX6hzwA8DVK2EPn4ZXk31siHQz1L6vd37Y8s8V5fqYj0293frhJ3aiOoUhH5tmfcpV0k7Vdpp2RQVdJk+cHO3t0I6vvroNKoD+pbk8FqA1BxLH4oM8fX4J6Dfk0jFTGSDFdoIp2As8q34ZZrZoTSOf36Hm8tAmXNi0cqxANwQ5zyA9467MDLncTcCWn1NsU1W7KPBBY7mQf4VOhi2zFYKdB2rYMbxbbQ2dGWdUcZFqPmj2DLx/k0HkNiEVOZyxnvuKWydA+L+T+36zHFbs3mo5P414/yI/Zwwxx0xRhE1FIorqj2OphSkmMS4wBzLt96tJjAsaV8af8=",
				"ShoopDaWhoop.select");
		Shoop npc = new Shoop(null, false);
		Smashplex.team.addEntry("i13u1i3u12i3u1");
		shoop.setItemStack(0, npc.getPrimary(0));
		shoop.setItemStack(1, npc.getBoots());
		shoop.setItemStack(2, npc.getLeggings());
		shoop.setItemStack(3, npc.getChestplate());
		shoop.setItemStack(4, npc.getHelmet());
		Inventory invshoop = Bukkit.createInventory(null, 54, "Shoop");
		ItemStack helm = new ItemStack(Material.CARPET, 1, (short) 1);
		ItemMeta imh = helm.getItemMeta();
		imh.setDisplayName(ChatColor.GREEN + "Shoop");
		List<String> loreh = new ArrayList<String>();
		loreh.add(ChatColor.GRAY + "Shoop is capable of emitting light ");
		loreh.add(ChatColor.GRAY + "from his oral cavity through a ");
		loreh.add(ChatColor.GRAY + "process of optimal amplification");
		loreh.add(ChatColor.GRAY + "based on the stimulated emission of ");
		loreh.add(ChatColor.GRAY + "electromagnetic radiation. In other ");
		loreh.add(ChatColor.GRAY + "words... HE CAN SHOOT LASERS OUT OF ");
		loreh.add(ChatColor.GRAY + "HIS MOUTH!");
		loreh.add(ChatColor.GRAY + "");
		loreh.add(ChatColor.GRAY + "Type: " + ChatColor.AQUA + "RANGED");
		loreh.add(ChatColor.GRAY + "Difficulty: " + ChatColor.RED + "●●●" + ChatColor.GRAY + "●");
		imh.setLore(loreh);
		loreh.add(ChatColor.GRAY + "");
		loreh.add(ChatColor.YELLOW + "Click to select!");
		imh.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE });
		helm.setItemMeta(imh);
		invshoop.setItem(4, helm);
		invshoop.setItem(19, npc.getPrimary(0));
		invshoop.setItem(21, npc.getSecondary(1));
		ItemStack smash = npc.getSmash(1);
		ItemMeta ims = smash.getItemMeta();
		ims.setDisplayName(ChatColor.GOLD + "FIRIN MAH LAZOR" + ChatColor.GRAY + " - " + ChatColor.AQUA + "[3]");
		smash.setItemMeta(ims);
		invshoop.setItem(23, smash);
		ItemStack passive = new ItemStack(Material.INK_SACK, 1, (short) 12);
		ItemMeta im4 = passive.getItemMeta();
		im4.setDisplayName(
				ChatColor.DARK_GREEN + "Ride the Lightning" + ChatColor.GRAY + " - " + ChatColor.AQUA + "[Sneak]");
		List<String> lore4 = new ArrayList<String>();
		lore4.add(ChatColor.GRAY + "Passive");
		lore4.add("");
		lore4.add(ChatColor.GRAY + "Press" + ChatColor.AQUA + " [Sneak]" + ChatColor.GRAY + " to ");
		lore4.add(ChatColor.GRAY + "hop onto a Lightning Bolt in ");
		lore4.add(ChatColor.GRAY + "order to travel up to" + ChatColor.GREEN + " 50 ");
		lore4.add(ChatColor.GRAY + "blocks. Costs" + ChatColor.YELLOW + " 98 " + ChatColor.GRAY + "Energy.");
		im4.setLore(lore4);
		passive.setItemMeta(im4);
		invshoop.setItem(25, passive);
		shoop.setInventory(invshoop);
		npc.setMasterSkin(true);
		shoop.setMasterItemStack(0, npc.getPrimary(0));
		shoop.setMasterItemStack(1, npc.getBoots());
		shoop.setMasterItemStack(2, npc.getLeggings());
		shoop.setMasterItemStack(3, npc.getChestplate());
		shoop.setMasterItemStack(4, npc.getHelmet());
		Smashplex.npcs.add(shoop);
	}

}
