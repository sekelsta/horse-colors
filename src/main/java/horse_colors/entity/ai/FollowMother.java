package sekelsta.horse_colors.entity.ai;

import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

// Doesn't extend FollowParentGoal only because it's not worth doing reflection on obfusctated names to access the private fields
public class FollowMother extends Goal {
    protected final AbstractHorseGenetic horse;
    protected AbstractHorse mother;
    protected final double speedModifier;
    protected int timeUntilRecalculatePath = 0;

    public FollowMother(AbstractHorseGenetic horse, double speedModifier) {
        this.horse = horse;
        this.speedModifier = speedModifier;
    }

    @Override
    public boolean canUse() {
        if (!horse.isBaby()) {
            return false;
        }
        double horizontal = 10;
        double vertical = 5;
        List<? extends AbstractHorse> equines = horse.level().getEntitiesOfClass(AbstractHorse.class, horse.getBoundingBox().inflate(horizontal, vertical, horizontal));
        mother = equines.stream().filter((h) -> h.getUUID().equals(horse.getMotherUUID())).findAny().orElse(null);
        if (mother == null) {
            mother = equines.stream()
                .filter((h) -> h.getClass().equals(horse.getClass()))
                .sorted((h1, h2) -> Double.compare(h1.distanceToSqr(horse), h2.distanceToSqr(horse)))
                .findFirst().orElse(null);
        }
        return canContinueToUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (mother == null || !mother.isAlive()) {
            return false;
        }
        double distSq = mother.distanceToSqr(horse);
        double maxFollowDistance = 16;
        return distSq < maxFollowDistance * maxFollowDistance && distSq > minFollowDistance() * minFollowDistance();
    }

    protected double minFollowDistance() {
        return 6 * horse.getFractionGrown();
    }

    @Override
    public void start() {
        timeUntilRecalculatePath = 0;
    }

    @Override
    public void tick() {
        timeUntilRecalculatePath -= 1;
        if (timeUntilRecalculatePath < 0) {
            timeUntilRecalculatePath = adjustedTickDelay(10);
            horse.getNavigation().moveTo(mother, speedModifier);
        }
    }
}
