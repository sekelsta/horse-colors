package sekelsta.horse_colors.entity.ai;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import sekelsta.horse_colors.config.HorseConfig;

public class RandomWalkGroundTie extends WaterAvoidingRandomStrollGoal {

    public RandomWalkGroundTie(PathfinderMob creature, double speedIn) {
        super(creature, speedIn);
    }

    public RandomWalkGroundTie(PathfinderMob creature, double speedIn, float probabilityIn) {
        super(creature, speedIn, probabilityIn);
    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof AbstractHorse && HorseConfig.COMMON.enableGroundTie.get()) {
            if (((AbstractHorse)this.mob).isSaddled()) {
                return false;
            }
        }
        return super.canUse();
    }
}
