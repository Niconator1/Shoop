package abilities;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;

public class ArmorStandM extends EntityArmorStand {
	private int delay = 0;

	public ArmorStandM(World world, int delay) {
		super(world);
		this.delay = delay;
	}

	@Override
	public void g(float f, float f1) {
		if (this.bM()) {
			// this.a(f, f1, f3);
			if (this.delay > 0) {
				delay--;
			} else {
				this.move(this.motX, this.motY, this.motZ);
			}
			// this.motY -= 0.08D;
			// this.motY *= 0.9800000190734863D;
			// this.motX *= (double) f5;
			// this.motZ *= (double) f5;
		}
	}

}
