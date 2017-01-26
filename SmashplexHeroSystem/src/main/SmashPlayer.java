package main;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import abilities.Cooldown;
import heroes.Shoop;
import heroes.Skullfire;

public class SmashPlayer {

	private Player p;
	private int hero = -1;
	private int charges = 0;
	private int jumps = 2;
	private boolean flamejump = false;
	private double maxhp;
	private long lastdmgsound = 0;
	public long lastshottime = 0;

	public SmashPlayer(Player p, double maxhp) {
		this.p = p;
		this.maxhp = maxhp;
		p.setAllowFlight(false);
		p.removePotionEffect(PotionEffectType.JUMP);
		p.setWalkSpeed(0.2f);
		p.setLevel(0);
		p.setExp(0);
	}

	public SmashPlayer(Player p, double maxhp, int hero) {
		this.p = p;
		this.hero = hero;
		this.maxhp = maxhp;
	}

	public Player getPlayer() {
		return p;
	}

	public int getSelectedHero() {
		return hero;
	}

	public int getCharges() {
		return charges;
	}

	public void setCharges(int charges) {
		this.charges = charges;
	}

	public int getRemainingJumps() {
		return jumps;
	}

	public void setJumps(int jumps) {
		this.jumps = jumps;
	}

	public void setSelectedHero(int hero) {
		this.hero = hero;
	}

	public void setFlameJump(boolean b) {
		flamejump = b;
	}

	public boolean getFlameJump() {
		return flamejump;
	}

	public Hero getHero() {
		if (hero == 0) {
			return new Shoop();
		} else if (hero == 1) {
			return new Skullfire();
		}
		return null;
	}

	public void removeHeroItems() {
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
		jumps = 2;
		charges = 0;
	}

	public void giveHeroItems() {
		p.getInventory().setHelmet(getHero().getHelmet());
		p.getInventory().setChestplate(getHero().getChestplate());
		p.getInventory().setLeggings(getHero().getLeggings());
		p.getInventory().setBoots(getHero().getBoots());
		p.getInventory().setItem(0, getHero().getPrimary(charges));
		p.getInventory().setItem(1, getHero().getSecondary(0));
		p.getInventory().setItem(2, getHero().getSmash(0));
		p.setAllowFlight(true);
		Cooldown c = new Cooldown(p, 2, getHero().getSmashCooldown());
		Smashplex.cooldown.add(c);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1, false, false));
		p.setWalkSpeed(0.4f);
	}

	public void doPrimary() {
		Hero h = getHero();
		h.doPrimary(this);
	}

	public void doSecondary() {
		Hero h = getHero();
		h.doSecondary(this);
	}

	public void doSmash() {
		Hero h = getHero();
		h.doSmash(this);
	}

	public double getHP() {
		double hp = (maxhp) * p.getHealth() / p.getMaxHealth();
		return hp;
	}

	public void damage(double amount) {
		if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
			CraftLivingEntity cle = ((CraftLivingEntity) p);
			cle.getHandle().world.broadcastEntityEffect(cle.getHandle(), (byte) 2);
			if (getHP() - amount >= 0) {
				if (System.currentTimeMillis() - lastdmgsound > 1000) {
					this.getHero().doDamageSound();
					lastdmgsound = System.currentTimeMillis();
				}
				double health = (getHP() - amount) / (maxhp) * p.getMaxHealth();
				p.setHealth(health);
			} else {
				p.setHealth(20);
				p.sendMessage(ChatColor.YELLOW + "You died.");
				p.teleport(p.getWorld().getSpawnLocation());
				this.getHero().doDeathSound();
			}
		}
	}

	public double getMasxHP() {
		return maxhp;
	}

}
