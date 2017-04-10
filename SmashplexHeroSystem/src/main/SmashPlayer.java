package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
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
	private int lives = 3;
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

	public void setLives(int a) {
		this.lives = a;
	}

	public int getLives() {
		return lives;
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
				Game g = Smashplex.getGame(p);
				if (g != null && g.hasBegan()) {
					g.hasDied(this);
				} else {
					p.setHealth(20);
					p.sendMessage(ChatColor.YELLOW + "You died.");
					p.teleport(p.getWorld().getSpawnLocation());
					SoundUtil.sendSoundPacket(p, "mob.endermen.portal", p.getLocation(), 0f);
				}
				h.doDeathSound();
			}
		}
	}

	public double getMasxHP() {
		return maxhp;
	}

	public void selectHero(int i, boolean prepare) {
		p.getInventory().clear();
		switch (i) {
		case 1:
			h = new Skullfire(p, false);
			SoundUtil.sendSoundPacket(p, "Skullfire.select", p.getLocation());
			break;
		case 0:
			h = new Shoop(p, false);
			SoundUtil.sendSoundPacket(p, "ShoopDaWhoop.select", p.getLocation());
			break;
		default:
			break;
		}
		if (h != null && prepare) {
			preparePlayer(2);
		}
	}

	public void preparePlayer(int step) {
		if (h != null) {
			if (step >= 0) {
				p.setAllowFlight(false);
				p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1, false, false));
				p.setWalkSpeed(0.4f);
			}
			if (step >= 1) {
				h.giveItems(true);
			}
			if (step >= 2) {
				p.getInventory().setHeldItemSlot(0);
				h.initialize();
				h.giveItems(false);
			}
			if (step >= 3) {
				p.setAllowFlight(true);
			}
			if (step >= 4) {
				Cooldown c = new Cooldown(p, 2, h.getSmashCooldown());
				Smashplex.cooldown.add(c);
			}
		}
	}

	public void resetHero(boolean full, boolean lives) {
		jumps = 2;
		if (lives) {
			this.lives = 3;
		}
		for (int i = 0; i < Smashplex.cooldown.size(); i++) {
			Cooldown c = Smashplex.cooldown.get(i);
			if (c.getPlayer().getUniqueId().compareTo(p.getUniqueId()) == 0) {
				Smashplex.cooldown.remove(i);
			}
		}
		h.resetHero();
		if (full) {
			h = null;
		}
	}

	public int getSelectedNumber() {
		if (h != null) {
			return h.getNumber();
		}
		return -1;
	}

	public boolean firstJoin() {
		File f = new File(
				Smashplex.getPlugin(Smashplex.class).getDataFolder() + "/players/" + p.getUniqueId() + ".txt");
		if (f.exists()) {
			return false;
		}
		return true;
	}

	public void readData() {
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(
					Smashplex.getPlugin(Smashplex.class).getDataFolder() + "/players/" + p.getUniqueId() + ".txt")));
			while (is.ready()) {
				String param = is.readLine();
				param = param.substring(1, param.length() - 1);
				String[] parts = param.split(",");
				if (parts.length > 0) {
					selectHero(Integer.parseInt(parts[0]), false);
				}
			}
			is.close();
		} catch (Exception e) {
			System.err.println("Fehler beim einlesen von Spielerdaten: " + p.getUniqueId());
			e.printStackTrace();
		}
	}

	public void saveData() {
		File f = new File(
				Smashplex.getPlugin(Smashplex.class).getDataFolder() + "/players/" + p.getUniqueId() + ".txt");
		if (!f.exists()) {
			try {
				f.getParentFile().mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				System.err.println("Fehler beim erstellen von Spielerdaten: " + p.getUniqueId());
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			String s = "{";
			s += getSelectedNumber() + "";
			// s += wp.getMode() + ",";
			s += "}";
			os.write(s + "\n");
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
