package abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import main.Smashplex;
import net.minecraft.server.v1_8_R3.EnumParticle;
import util.ParticleUtil;

public class FlameJump {
	private int count = 0;
	private int done = 0;
	private double sr = 1.0;
	private Location l;

	public FlameJump(Location l) {
		this.l = l;
	}

	public void doJump() {
		count = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Smashplex.getPlugin(Smashplex.class),
				new Runnable() {
					public void run() {
						for (int i = 0; i < 4; i++) {
							sr += 0.05;
							double angle;
							if ((i + done) == 0) {
								angle = 5.3 / 360.0 * 2 * Math.PI;
							} else {
								angle = (7.4 * (i + done) + 5.3) / 360.0 * 2.0 * Math.PI;
							}
							for (int j = 0; j < 4; j++) {
								double xa = Math.cos(angle + j / 4.0 * 2.0 * Math.PI) * sr;
								double za = Math.sin(angle + j / 4.0 * 2.0 * Math.PI) * sr;
								Location loc = l.clone().add(xa, 0, za);
								ParticleUtil.sendPublicParticlePacket(EnumParticle.FLAME, loc, 6);
							}
						}
						done += 4;
						if (done > 45) {
							Bukkit.getScheduler().cancelTask(count);
						}
					}
				}, 1, 0);
	}
}
