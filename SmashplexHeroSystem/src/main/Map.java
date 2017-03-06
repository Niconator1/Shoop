package main;

import org.bukkit.Location;

public class Map {
	private String name;
	private int maxplayers;
	private Location lobby;
	private Location[] spawnpos;
	private double deadheight;

	public Map(String name, int players) {
		this.name = name;
		this.maxplayers = players;
	}

	public int getMaxPlayerCount() {
		return maxplayers;
	}

	public String getName() {
		return name;
	}

	public Location getLobbyLocation() {
		return lobby;
	}

	public void setLobbyLocation(Location location) {
		this.lobby = location;
	}

	public void setSpawnLocations(Location[] loc) {
		this.spawnpos = loc;
	}

	public Location[] getSpawnPositions() {
		return spawnpos;
	}

	public double getVoidLimit() {
		return deadheight;
	}

	public void setVoidLimit(double i) {
		this.deadheight = i;
	}
}
