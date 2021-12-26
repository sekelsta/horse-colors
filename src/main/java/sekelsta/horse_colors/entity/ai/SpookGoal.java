package sekelsta.horse_colors.entity.ai;

import java.util.function.Predicate;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class SpookGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public SpookGoal(AbstractHorse entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    public SpookGoal(AbstractHorse entityIn, Class<T> avoidClass, Predicate<LivingEntity> targetPredicate, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> predicate) {
        super(entityIn, avoidClass, targetPredicate, distance, nearSpeedIn, farSpeedIn, predicate);
    }

    public SpookGoal(AbstractHorse entityIn, Class<T> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, avoidClass, distance, nearSpeedIn, farSpeedIn, targetPredicate);
    }

    @Override
    public void start() {
        AbstractHorse horse = (AbstractHorse)this.mob;
        if (horse.isVehicle()) {
            horse.ejectPassengers();
            horse.makeMad();
        }
        super.start();
    }


}
