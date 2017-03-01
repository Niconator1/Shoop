package abilities;

import org.bukkit.entity.Player;

public class Botmobile {
	private ArmorStandM a;
	private Player p;

	public Botmobile(ArmorStandM f, Player p) {
		this.a = f;
		this.p = p;
	}

	public ArmorStandM getStand() {
		return a;
	}

	public Player getPlayer() {
		return p;
	}

}
