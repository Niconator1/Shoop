package main;

import org.bukkit.entity.Player;

public class Shoop {
	private Player shoop;
	public Shoop(Player s){
		this.shoop=s;
	}
	public Player getPlayer(){
		return shoop;
	}
}
