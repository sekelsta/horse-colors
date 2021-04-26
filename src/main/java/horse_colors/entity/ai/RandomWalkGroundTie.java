package sekelsta.horse_colors.entity.ai;

import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;

import sekelsta.horse_colors.config.HorseConfig;

public class RandomWalkGroundTie extends WaterAvoidingRandomWalkingGoal {

    public RandomWalkGroundTie(CreatureEntity creature, double speedIn) {
        super(creature, speedIn);
    }

    public RandomWalkGroundTie(CreatureEntity creature, double speedIn, float probabilityIn) {
        super(creature, speedIn, probabilityIn);
    }

    @Override
    public boolean canUse() {
        if (this.mob instanceof AbstractHorseEntity && HorseConfig.COMMON.enableGroundTie.get()) {
            if (((AbstractHorseEntity)this.mob).isSaddled()) {
                return false;
            }
        }
        return super.canUse();
    }
}
