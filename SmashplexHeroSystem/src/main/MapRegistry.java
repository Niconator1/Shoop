package main;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MapRegistry {

	public static void registerMaps() {
		Map kingdom = new Map("Kingdom", 2);
		kingdom.setLobbyLocation(new Location(Bukkit.getWorld("Kingdom"), 0, 84, 0));
		kingdom.setSpawnLocations(new Location[] { new Location(Bukkit.getWorld("Kingdom"), 1.5, 62, -21.5),
				new Location(Bukkit.getWorld("Kingdom"), 25.5, 63, -2.5),
				new Location(Bukkit.getWorld("Kingdom"), -0.5, 62, 22.5),
				new Location(Bukkit.getWorld("Kingdom"), -24.5, 63, 3.5) });
		kingdom.setVoidLimit(0.0);
		Smashplex.maplist.add(kingdom);
	}

	public static Map getRandomMap() {
		int size = Smashplex.maplist.size();
		int index = (int) (Math.random() * size);
		return Smashplex.maplist.get(index);
	}
}
