package main;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public abstract class Hero {

	private String name;
	private int smashc;

	public Hero(String name, int smashc) {
		this.name = name;
		this.smashc = smashc;
	}

	public String getName() {
		return name;
	}

	public int getSmashCooldown() {
		return smashc;
	}

	public abstract ItemStack getHelmet();

	public abstract ItemStack getChestplate();

	public abstract ItemStack getLeggings();

	public abstract ItemStack getBoots();

	public abstract ItemStack getPrimary();

	public abstract ItemStack getSecondary(double loaded);

	public abstract ItemStack getSmash(double loaded);

	public static ItemStack addColor(ItemStack item, Color c) {
		LeatherArmorMeta lch = (LeatherArmorMeta) item.getItemMeta();
		lch.setColor(c);
		item.setItemMeta(lch);
		return item;
	}

	public abstract void doPrimary(SmashPlayer sp);

	public abstract void doSecondary(SmashPlayer sp);

	public abstract void doSmash(SmashPlayer sp);

	public abstract double getMeleeDamage();

}
