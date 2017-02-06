package abilities;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.World;

public class ArmorStandM extends EntityArmorStand {

	public ArmorStandM(World world) {
		super(world);
	}

	@Override
	public void g(float f, float f1) {
		double d0;
		float f2;
		if (this.bM()) {
			float f3;
			float f4;
			float f5 = 0.91F;
			if (this.onGround) {
				f5 = this.world
						.getType(new BlockPosition(MathHelper.floor(this.locX),
								MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ)))
						.getBlock().frictionFactor * 0.91F;
			}

			float f6 = 0.16277136F / (f5 * f5 * f5);

			if (this.onGround) {
				f3 = this.bI() * f6;
			} else {
				f3 = this.aM;
			}

			this.a(f, f1, f3);
			f5 = 0.91F;
			if (this.onGround) {
				f5 = this.world
						.getType(new BlockPosition(MathHelper.floor(this.locX),
								MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ)))
						.getBlock().frictionFactor * 0.91F;
			}

			if (this.k_()) {
				f4 = 0.15F;
				this.motX = MathHelper.a(this.motX, (double) (-f4), (double) f4);
				this.motZ = MathHelper.a(this.motZ, (double) (-f4), (double) f4);
				this.fallDistance = 0.0F;
				if (this.motY < -0.15D) {
					this.motY = -0.15D;
				}
			}
			this.move(this.motX, this.motY, this.motZ);
			if (this.positionChanged && this.k_()) {
				this.motY = 0.2D;
			}
			// this.motY -= 0.08D;
			// this.motY *= 0.9800000190734863D;
			// this.motX *= (double) f5;
			// this.motZ *= (double) f5;
		}

		this.aA = this.aB;
		d0 = this.locX - this.lastX;
		double d1 = this.locZ - this.lastZ;

		f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
		if (f2 > 1.0F) {
			f2 = 1.0F;
		}

		this.aB += (f2 - this.aB) * 0.4F;
		this.aC += this.aB;
	}

}
