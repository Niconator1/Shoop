package main;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import abilities.Cooldown;
import heroes.Shoop;

public class SmashPlayer {

	private Player p;
	private int hero = -1;
	private int charges = 0;
	private int jumps = 2;
	private double maxhp;

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

	public Hero getHero() {
		if (hero == 0) {
			return new Shoop();
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
	}

	public void giveHeroItems() {
		p.getInventory().setHelmet(getHero().getHelmet());
		p.getInventory().setChestplate(getHero().getChestplate());
		p.getInventory().setLeggings(getHero().getLeggings());
		p.getInventory().setBoots(getHero().getBoots());
		p.getInventory().setItem(0, getHero().getPrimary());
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
				double health = (getHP() - amount) / (maxhp) * p.getMaxHealth();
				p.setHealth(health);
			} else {
				p.setHealth(0);
			}
		}
	}

	public Vector getKnockback(Vector base, Vector vector, double hp) {
		double ey = 0.0047 * hp;
		double exz = 0.0125 * hp;
		Vector v = new Vector(base.getZ() * vector.getX(), base.getY() + base.getZ() * vector.getY(),
				base.getZ() * vector.getZ());
		Vector w = new Vector(exz * vector.getX(), ey + exz * vector.getY(), exz * vector.getZ());
		return v.add(w);
	}

	public double getMasxHP() {
		return maxhp;
	}

}
