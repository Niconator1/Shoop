package main;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import abilities.Cooldown;
import heroes.Shoop;

public class SmashPlayer {

	private Player p;
	private int hero = -1;
	private int charges = 0;

	public SmashPlayer(Player p) {
		this.p = p;
		p.setAllowFlight(false);
		p.removePotionEffect(PotionEffectType.JUMP);
		p.setWalkSpeed(0.2f);
		p.setLevel(0);
		p.setExp(0);
	}

	public SmashPlayer(Player p, int hero) {
		this.p = p;
		this.hero = hero;
	}

	public Player getPlayer() {
		return p;
	}

	public int getSelectedHero() {
		return hero;
	}
	public int getCharges(){
		return charges;	
	}
	public void setCharges(int charges){
		this.charges=charges;
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
		Cooldown c = new Cooldown(p, 2, 1799);
		Smashplex.cooldown.add(c);
		// DJump dj = new DJump(p, 2);
		// jumps.add(dj);
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

}
