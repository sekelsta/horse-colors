package sekelsta.horse_colors.entity.ai;

import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;

public class SpookGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public SpookGoal(AbstractHorseEntity entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    public SpookGoal(AbstractHorseEntity entityIn, Class<T> avoidClass, Predicate<LivingEntity> targetPredicate, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> predicate) {
        super(entityIn, avoidClass, targetPredicate, distance, nearSpeedIn, farSpeedIn, predicate);
    }

    public SpookGoal(AbstractHorseEntity entityIn, Class<T> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, avoidClass, distance, nearSpeedIn, farSpeedIn, targetPredicate);
    }
/*
    @Override
    public boolean shouldExecute() {
        this.avoidTarget = this.entity.world.func_225318_b(this.classToAvoid, this.builtTargetSelector, this.entity, this.entity.getPosX(), this.entity.getPosY(), this.entity.getPosZ(), this.entity.getBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance));
        int i = this.entity.getRNG().nextInt();
        if (i % 4 == 0) {
            return super.shouldExecute();
        }
        return false;
    }
*/
    @Override
    public void startExecuting() {
        AbstractHorseEntity horse = (AbstractHorseEntity)this.entity;
        if (horse.isBeingRidden()) {
            horse.removePassengers();
            horse.makeMad();
        }
        super.startExecuting();
    }


}
