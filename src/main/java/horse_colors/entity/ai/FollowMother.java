package sekelsta.horse_colors.entity.ai;

import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class FollowMother extends FollowGoal {
    public FollowMother(AbstractHorseGenetic horse, double speedModifier) {
        super(horse, speedModifier);
    }

    @Override
    public boolean meetsConditions() {
        return horse.isBaby();
    }

    @Override
    public AbstractHorse getBestTarget(List<AbstractHorse> equines) {
        AbstractHorse mother = equines.stream().filter(
                (h) -> h.getUUID().equals(horse.getMotherUUID())
            ).findAny().orElse(null);
        if (mother != null) {
            return mother;
        }
        return equines.stream()
                .filter((h) -> h.getClass().equals(horse.getClass()))
                .sorted((h1, h2) -> Double.compare(h1.distanceToSqr(horse), h2.distanceToSqr(horse)))
                .findFirst().orElse(null);
    }

    @Override
    protected double minFollowDistance() {
        return 6 * horse.getFractionGrown();
    }

    @Override
    protected double maxFollowDistance() {
        return 16;
    }
}
