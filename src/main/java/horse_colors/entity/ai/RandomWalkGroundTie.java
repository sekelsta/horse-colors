package sekelsta.horse_colors.entity.ai;

import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;

import sekelsta.horse_colors.config.HorseConfig;

public class RandomWalkGroundTie extends EntityAIWanderAvoidWater {

    public RandomWalkGroundTie(EntityCreature creature, double speedIn) {
        super(creature, speedIn);
    }

    public RandomWalkGroundTie(EntityCreature creature, double speedIn, float probabilityIn) {
        super(creature, speedIn, probabilityIn);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity instanceof AbstractHorse && HorseConfig.getEnableGroundTie()) {
            if (((AbstractHorse)entity).isHorseSaddled()) {
                return false;
            }
        }
        return super.shouldExecute();
    }
}
