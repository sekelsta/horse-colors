package sekelsta.horse_colors.entity.ai;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class StayWithHerd extends FollowGoal {
    protected int lastSearchTick = 0;
    protected int acceptableDelay = 100;

    public StayWithHerd(AbstractHorseGenetic horse, double speedModifier) {
        super(horse, speedModifier);
        horizontalSearch = 20;
        verticalSearch = 12;
    }

    @Override
    public boolean targetMeetsConditions() {
        return super.targetMeetsConditions() && !target.isVehicle() && !target.isLeashed();
    }

    @Override
    public boolean meetsConditions() {
        return !horse.isBaby() && !horse.isVehicle() && !horse.isLeashed();
    }

    @Override
    public boolean shouldRecalculateTarget() {
        return horse.tickCount - lastSearchTick > acceptableDelay;
    }

    @Override
    public AbstractHorse getBestTarget(List<AbstractHorse> equines) {
        lastSearchTick = horse.tickCount + horse.getRandom().nextInt(50) - 25;
        if (horse.isMale()) {
            Stream<AbstractHorse> s = equines.stream();
            if (horse.isFertile()) {
                s = s.filter((h) -> !(h instanceof AbstractHorseGenetic) || ((AbstractHorseGenetic)h).isFertile());
            }
            return s
                .filter(
                        (h) -> (!(h instanceof AbstractHorseGenetic) || !((AbstractHorseGenetic)h).isMale())
                    || h.isBaby() || h.getMaxHealth() > horse.getMaxHealth()
                )
                .sorted((h1, h2) -> {
                            boolean h1c = h1.getClass().equals(horse.getClass());
                            boolean h2c = h2.getClass().equals(horse.getClass());
                            if (h1c != h2c) {
                                return Boolean.compare(h2c, h1c);
                            }
                            boolean h1m = h1 instanceof AbstractHorseGenetic && ((AbstractHorseGenetic)h1).isMale() && !h1.isBaby();
                            boolean h2m = h2 instanceof AbstractHorseGenetic && ((AbstractHorseGenetic)h2).isMale() && !h2.isBaby();
                            if (h1m != h2m) {
                                return Boolean.compare(h1m, h2m);
                            }
                            if (h1m) {
                                return Float.compare(h2.getMaxHealth(), h1.getMaxHealth());
                            }
                            else {
                                return Double.compare(h1.distanceToSqr(horse), h2.distanceToSqr(horse));
                            }
                        }
                    )
                .findFirst().orElse(null);
        }
        // Else mare
        return equines.stream()
            .filter((h) -> h.getMaxHealth() > horse.getMaxHealth())
            .sorted((h1, h2) -> {
                        boolean h1b = h1.getClass().equals(horse.getClass());
                        boolean h2b = h2.getClass().equals(horse.getClass());
                        return h1b != h2b ? Boolean.compare(h2b, h1b) :
                            Float.compare(h2.getMaxHealth(), h1.getMaxHealth());
                    }
                )
            .findFirst().orElse(null);
    }

    @Override
    protected double minFollowDistance() {
        return 12;
    }

    @Override
    protected double maxFollowDistance() {
        return 24;
    }
}
