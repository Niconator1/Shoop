package abilities;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Lightningbolt {
	private ArmorStandM a;
	private Vector v;
	private Player shoop;
	private Location start;
	private boolean isPassive;
	private ArrayList<UUID> hitted = new ArrayList<UUID>();

	public Lightningbolt(ArmorStandM f, Vector v, Player shoop, Location start, boolean passive) {
		this.a = f;
		this.v = v;
		this.shoop = shoop;
		this.start = start;
		this.isPassive = passive;
	}

	public ArmorStandM getStand() {
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

	public void addHitted(UUID p) {
		hitted.add(p);
	}

	public boolean isPassive() {
		return isPassive;
	}

	public boolean canHit(LivingEntity p) {
		for (int i = 0; i < hitted.size(); i++) {
			if (hitted.get(i).compareTo(p.getUniqueId()) == 0) {
				return false;
			}
		}
		return true;
	}

}
