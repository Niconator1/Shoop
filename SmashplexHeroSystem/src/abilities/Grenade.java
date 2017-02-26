package abilities;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Grenade {
	private ArmorStandM a;
	private float pitch;
	private float yaw;
	private Player shoop;
	private double damage = 10.0;
	private Location start;

	public Grenade(ArmorStandM fn, float pitch, float yaw, Player p, Location l) {
		this.a = fn;
		this.pitch = pitch;
		this.yaw = yaw;
		this.shoop = p;
		this.start = l;
	}

	public ArmorStandM getStand() {
		return a;
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

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}
}
