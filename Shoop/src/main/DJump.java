package main;

import org.bukkit.entity.Player;

public class DJump {
	private Player shoop;
	private int count;

	public DJump(Player shoop, int count) {
		this.shoop = shoop;
		this.count = count;
	}

	public Player getShoop() {
		return shoop;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int c) {
		this.count = c;
	}

}
