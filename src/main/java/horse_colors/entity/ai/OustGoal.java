package sekelsta.horse_colors.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.*;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class OustGoal extends Goal {
    public final AbstractHorseGenetic entity;
    public AbstractHorseGenetic target = null;
    public AbstractHorseGenetic stayNear = null;
    public float stayNearDistance = 16;
    public float maxDist = 18;
    public float runSpeed = 1.2f;
    public float walkSpeed = 0.9f;
    protected Path path = null;

    public OustGoal(AbstractHorseGenetic entityIn) {
        this.entity = entityIn;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (target == null) {
            return false;
        }
        if (stayNear == null) {
            return true;
        }
        if (entity.distanceToSqr(stayNear) > stayNearDistance * stayNearDistance) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.distanceToSqr(target) < maxDist * maxDist && !entity.getNavigation().isDone()
            && (stayNear == null || entity.distanceToSqr(stayNear) < stayNearDistance * stayNearDistance);
    }

    @Override
    public void start() {
        entity.getNavigation().moveTo(target, walkSpeed);
        target.fleeFrom(entity);
    }

    @Override
    public void stop() {
        target = null;
        stayNear = null;
    }

    @Override
    public void tick() {
        if (entity.distanceToSqr(target) < 49.0) {
            entity.getNavigation().setSpeedModifier(walkSpeed);
        }
        else {
            entity.getNavigation().setSpeedModifier(runSpeed);
        }
    }
}

