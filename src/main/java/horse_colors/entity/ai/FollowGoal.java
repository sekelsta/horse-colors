package sekelsta.horse_colors.entity.ai;

import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public abstract class FollowGoal extends Goal {
    protected final AbstractHorseGenetic horse;
    protected AbstractHorse target;
    protected final double speedModifier;
    protected int timeUntilRecalculatePath = 0;

    protected double horizontalSearch = 10;
    protected double verticalSearch = 5;

    public FollowGoal(AbstractHorseGenetic horse, double speedModifier) {
        this.horse = horse;
        this.speedModifier = speedModifier;
    }

    public boolean targetMeetsConditions() {
        return target != null && target.isAlive();
    }

    public abstract boolean meetsConditions();

    public boolean shouldRecalculateTarget() {
        return true;
    }

    public abstract AbstractHorse getBestTarget(List<AbstractHorse> equines);

    @Override
    public boolean canUse() {
        if (!meetsConditions()) {
            return false;
        }
        if (shouldRecalculateTarget()) {
            List<AbstractHorse> equines = horse.level().getEntitiesOfClass(AbstractHorse.class, horse.getBoundingBox().inflate(horizontalSearch, verticalSearch, horizontalSearch));
            target = getBestTarget(equines);
        }
        return canContinueToUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (!targetMeetsConditions()) {
            return false;
        }
        double distSq = target.distanceToSqr(horse);
        double max = maxFollowDistance();
        double min = minFollowDistance();
        return distSq < max * max && distSq > min * min;
    }

    // Distance at which we are close enough and can stop following
    protected abstract double minFollowDistance();

    // Distance at which we are too far and should give up
    protected abstract double maxFollowDistance();

    @Override
    public void start() {
        timeUntilRecalculatePath = 0;
    }

    @Override
    public void tick() {
        timeUntilRecalculatePath -= 1;
        if (timeUntilRecalculatePath < 0) {
            timeUntilRecalculatePath = adjustedTickDelay(10);
            horse.getNavigation().moveTo(target, speedModifier);
        }
    }
}
