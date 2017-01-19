package util;

import org.bukkit.util.Vector;

import main.SmashPlayer;

public class KnockbackUtil {
	public static Vector getKnockback(SmashPlayer sp) {
		double sshp = sp.getMasxHP() - sp.getHP();
		double ey = 0.0125 * sshp;
		return new Vector(0, ey, 0);
	}

}
