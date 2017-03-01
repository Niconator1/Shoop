package main;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import abilities.Cooldown;
import heroes.Hero;
import heroes.Shoop;
import heroes.Skullfire;
import util.SoundUtil;

public class SmashPlayer {

	private Player p;
	private Hero h;
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

	public Player getPlayer() {
		return p;
	}

	public Hero getSelectedHero() {
		return h;
	}

	public int getRemainingJumps() {
		return jumps;
	}

	public void setJumps(int jumps) {
		this.jumps = jumps;
	}

	public void giveHeroItems() {

	}

	public void doPrimary() {
		h.doPrimary();
	}

	public void doSecondary() {
		h.doSecondary();
	}

	public void doSmash() {
		h.doSmash();
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
				if (System.currentTimeMillis() - h.getLastDmgSound() > 1000) {
					h.doDamageSound();
					h.setLastDmgSound(System.currentTimeMillis());
				}
				double health = (getHP() - amount) / (maxhp) * p.getMaxHealth();
				p.setHealth(health);
			} else {
				p.setHealth(20);
				p.sendMessage(ChatColor.YELLOW + "You died.");
				p.teleport(p.getWorld().getSpawnLocation());
				h.doDeathSound();
			}
		}
	}

	public double getMasxHP() {
		return maxhp;
	}

	public void selectHero(int i) {
		switch (i) {
		case 1:
			h = new Skullfire(p);
			SoundUtil.sendSoundPacket(p, "Skullfire.select", p.getLocation());
			break;
		default:
			h = new Shoop(p);
			SoundUtil.sendSoundPacket(p, "ShoopDaWhoop.select", p.getLocation());
			break;
		}
		p.setAllowFlight(true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1, false, false));
		p.setWalkSpeed(0.4f);
		h.giveItems();
		Cooldown c = new Cooldown(p, 2, h.getSmashCooldown());
		Smashplex.cooldown.add(c);
	}

	public void resetHero() {
		jumps = 2;
		h.resetHero();
	}

}
