package heroes;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffectType;

public abstract class Hero {

	private String name;
	private int smashc;
	private int nr;
	private Player p;
	private long lastdmgsound = 0;
	private long lastshottime = 0;
	private boolean ms;

	public Hero(Player p, String name, int smashc, int nr, boolean ms) {
		this.name = name;
		this.smashc = smashc;
		this.nr = nr;
		this.p = p;
		this.ms = ms;
	}

	public String getName() {
		return name;
	}

	public boolean isMasterSkin() {
		return ms;
	}

	public int getSmashCooldown() {
		return smashc;
	}

	public Player getPlayer() {
		return p;
	}

	public long getLastDmgSound() {
		return lastdmgsound;
	}

	public long getLastShotTime() {
		return lastshottime;
	}

	public void setMasterSkin(boolean is) {
		ms = is;
	}

	public void setLastDmgSound(long i) {
		lastdmgsound = i;
	}

	public void setLastShotTime(long i) {
		lastshottime = i;
	}

	public abstract ItemStack getHelmet();

	public abstract ItemStack getChestplate();

	public abstract ItemStack getLeggings();

	public abstract ItemStack getBoots();

	public abstract ItemStack getPrimary(int amount);

	public abstract ItemStack getSecondary(double loaded);

	public abstract ItemStack getSmash(double loaded);

	public abstract void doPrimary();

	public abstract void doSecondary();

	public abstract void doSmash();

	public abstract void doDamageSound();

	public abstract void doDeathSound();

	public abstract double getMeleeDamage();

	public static ItemStack addColor(ItemStack item, Color c) {
		LeatherArmorMeta lch = (LeatherArmorMeta) item.getItemMeta();
		lch.setColor(c);
		item.setItemMeta(lch);
		return item;
	}

	public int getNumber() {
		return nr;
	}

	public void giveItems() {
		p.getInventory().setHelmet(getHelmet());
		p.getInventory().setChestplate(getChestplate());
		p.getInventory().setLeggings(getLeggings());
		p.getInventory().setBoots(getBoots());
		p.getInventory().setItem(0, getPrimary(0));
		p.getInventory().setItem(1, getSecondary(0));
		p.getInventory().setItem(2, getSmash(0));
	}

	public void resetHero() {
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
	}

}
