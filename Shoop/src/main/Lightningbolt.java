package main;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Lightningbolt {
	private ArmorStand a;
	private Vector v;
	private Player shoop;
	private double damage = 1.5;
	private Location start;
	private boolean isPassive;
	private ArrayList<LivingEntity> hitted = new ArrayList<LivingEntity>();

	public Lightningbolt(ArmorStand f, Vector v, Player shoop, Location start, boolean passive) {
		this.a = f;
		this.v = v;
		this.shoop = shoop;
		this.start = start;
		this.isPassive = passive;
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

	public void addHitted(LivingEntity p) {
		hitted.add(p);
	}

	public boolean isPassive() {
		return isPassive;
	}

	public boolean canHit(LivingEntity p) {
		for (int i = 0; i < hitted.size(); i++) {
			if (hitted.get(i).getUniqueId().compareTo(p.getUniqueId()) == 0) {
				return false;
			}
		}
		return true;
	}

}
