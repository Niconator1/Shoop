package main;

import org.bukkit.entity.Player;

public class Cooldown {
	private int skill;
	private int maxticks;
	private int ticks;
	private Player p;
	private long start;

	public Cooldown(Player p, int skill, int ticks) {
		this.p = p;
		this.skill = skill;
		this.maxticks = ticks;
		this.ticks = ticks;
		this.start = System.currentTimeMillis();
	}

	public int getSkill() {
		return skill;
	}

	public int getTicks() {
		return ticks;
	}

	public Player getPlayer() {
		return p;
	}

	public int getMaxTicks() {
		return maxticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public long getStart() {
		return start;
	}
}
