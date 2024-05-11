package sekelsta.horse_colors.entity.ai;

import java.util.*;
import java.util.stream.Stream;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class StayWithHerd extends Goal {
    protected final AbstractHorseGenetic horse;
    protected AbstractHorse target;
    protected AbstractHorse staynear = null;
    protected double staynearDistance = 16;
    protected double speedModifier = 1.0;
    protected int timeUntilRecalculatePath = 0;

    protected int lastSearchTick = 0;
    protected int acceptableDelay = 100;

    public StayWithHerd(AbstractHorseGenetic horse) {
        this.horse = horse;
    }

    protected double closeEnoughDistance() {
        return 2 + 10 * horse.getFractionGrown();
    }

    protected double tooFarDistance() {
        return 16 + 8 * horse.getFractionGrown();
    }

    @Override
    public boolean canUse() {
        if (horse.isVehicle() || horse.isLeashed()) {
            return false;
        }

        if (horse.isBaby() && target != null && target.isAlive() && target.getUUID().equals(horse.getMotherUUID())) {
            double distSq = target.distanceToSqr(horse);
            double min = closeEnoughDistance();
            if (distSq < min * min) {
                return false;
            }
            double max = tooFarDistance();
            if (distSq < max * max) {
                return true;
            }
        }

        float age = horse.getFractionGrown();
        if (horse.tickCount - lastSearchTick > acceptableDelay * age) {
            lastSearchTick = horse.tickCount + horse.getRandom().nextInt(8);
            double horizontalSearch = 4 + 16 * age;
            double verticalSearch = 4 + 8 * age;
            List<AbstractHorse> equines = horse.level().getEntitiesOfClass(AbstractHorse.class, horse.getBoundingBox().inflate(horizontalSearch, verticalSearch, horizontalSearch));
            target = getBestTarget(equines);
        }
        return canContinueToUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (staynear != null && staynear.distanceToSqr(horse) > staynearDistance * staynearDistance) {
            target = staynear;
            staynear = null;
        }
        double distSq = target.distanceToSqr(horse);
        double max = tooFarDistance();
        double min = closeEnoughDistance();
        return distSq < max * max && distSq > min * min;
    }

    public int bestMother(AbstractHorse h1, AbstractHorse h2) {
        if (h1.getUUID().equals(horse.getMotherUUID())) {
            return -1;
        }
        else if (h2.getUUID().equals(horse.getMotherUUID())) {
            return 1;
        }
        boolean h1b = h1.isBaby();
        boolean h2b = h2.isBaby();
        if (h1b != h2b) {
            return Boolean.compare(h2b, h1b);
        }
        return nearestIdeallyMatchingClass(h1, h2);
    }

    public int nearestIdeallyMatchingClass(AbstractHorse h1, AbstractHorse h2) {
        boolean h1c = h1.getClass().equals(horse.getClass());
        boolean h2c = h2.getClass().equals(horse.getClass());
        if (h1c != h2c) {
            return Boolean.compare(h2c, h1c);
        }
        return Double.compare(h1.distanceToSqr(horse), h2.distanceToSqr(horse));
    }

    public int strongestIdeallyMatchingClass(AbstractHorse h1, AbstractHorse h2) {
        boolean h1c = h1.getClass().equals(horse.getClass());
        boolean h2c = h2.getClass().equals(horse.getClass());
        if (h1c != h2c) {
            return Boolean.compare(h2c, h1c);
        }
        return Float.compare(h2.getMaxHealth(), h1.getMaxHealth());
    }

    public int highestHealth(AbstractHorse h1, AbstractHorse h2) {
        return Float.compare(h2.getMaxHealth(), h1.getMaxHealth());
    }

    public AbstractHorse getBestTarget(List<AbstractHorse> equines) {
        if (horse.isBaby()) {
            return equines.stream().sorted(this::bestMother).findFirst().orElse(null);
        }

        List<AbstractHorse> vanillaEquines = new ArrayList<>();
        List<AbstractHorseGenetic> geneticEquines = new ArrayList<>();
        for (AbstractHorse h : equines) {
            if (h instanceof AbstractHorseGenetic) {
                geneticEquines.add((AbstractHorseGenetic)h);
            }
            else {
                vanillaEquines.add(h);
            }
        }

        if (!horse.isMale()) {
            AbstractHorseGenetic foal = null;
            for (AbstractHorseGenetic h : geneticEquines) {
                if (h.isBaby() && horse.getUUID().equals(h.getMotherUUID())) {
                    if (foal == null || foal.distanceToSqr(horse) < h.distanceToSqr(horse)) {
                        foal = h;
                    }
                }
            }
            if (foal != null && foal.distanceToSqr(horse) > 12 * 12) {
                return foal;
            }
        }
        else if (horse.isFertile()) {
            AbstractHorseGenetic mare = geneticEquines.stream()
                .filter((h) -> isFertileMare(h) && h.getClass().equals(horse.getClass()))
                .sorted((h1, h2) -> Double.compare(h1.distanceToSqr(horse), h2.distanceToSqr(horse)))
                .findFirst().orElse(null);
            if (mare != null) {
                if (!HorseConfig.COMMON.jealousStallions.get() || mare.distanceToSqr(horse) > staynearDistance * staynearDistance) {
                    return mare;
                }
                AbstractHorseGenetic competitor = geneticEquines.stream()
                    .filter(this::isFertileStallion)
                    .sorted(this::highestHealth)
                    .findFirst().orElse(null);
                if (competitor == null) {
                    return mare;
                }
                if (competitor.getMaxHealth() < horse.getMaxHealth()) {
                    speedModifier = 0.75;
                    staynear = mare;
                    competitor.fleeFrom(horse);
                    return competitor;
                }
            }
        }

        AbstractHorse t = equines.stream()
            .sorted(horse.isMale() ? this::nearestIdeallyMatchingClass : this::strongestIdeallyMatchingClass)
            .findFirst().orElse(null);
        if (t != null && t.getMaxHealth() <= horse.getMaxHealth()) {
            return null;
        }
        return t;
    }

    public boolean isFertileMare(AbstractHorseGenetic h) {
        return !h.isBaby() && !h.isMale() && h.isFertile();
    }

    public boolean isFertileStallion(AbstractHorseGenetic h) {
        return !h.isBaby() && h.isMale() && h.isFertile();
    }

    @Override
    public void start() {
        timeUntilRecalculatePath = 0;
    }

    @Override
    public void stop() {
        speedModifier = 1.0;
        staynear = null;
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
