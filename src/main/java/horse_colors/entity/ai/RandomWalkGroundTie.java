package sekelsta.horse_colors.entity.ai;

import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class RandomWalkGroundTie extends WaterAvoidingRandomStrollGoal {
    protected AbstractHorseGenetic horse = null;

    public RandomWalkGroundTie(AbstractHorseGenetic creature, double speedIn) {
        super(creature, speedIn);
        this.horse = creature;
    }

    public RandomWalkGroundTie(AbstractHorseGenetic creature, double speedIn, float probabilityIn) {
        super(creature, speedIn, probabilityIn);
        this.horse = creature;
    }

    @Override
    public boolean canUse() {
        if (horse.isGroundTied()) {
            return false;
        }
        return super.canUse();
    }
}
