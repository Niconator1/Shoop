package abilities;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Grenade {
	private ArmorStand a;
	private Vector v;
	private Player shoop;
	private double damage = 10.0;
	private Location start;

	public Grenade(ArmorStand f, Vector v, Player shoop, Location start) {
		this.a = f;
		this.v = v;
		this.shoop = shoop;
		this.start = start;
	}

	public ArmorStand getStand() {
		return a;
	}

	public Vector getVector() {
		return v;
	}

	public Player getShoop() {
		return shoop;
	}

	public Location start() {
		return start;
	}

	public double getDamage() {
		return damage;
	}

}
