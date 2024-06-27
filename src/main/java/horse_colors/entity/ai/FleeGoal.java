package sekelsta.horse_colors.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.*;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;

public class FleeGoal extends Goal {
    public final AbstractHorseGenetic entity;
    public Entity toAvoid = null;
    public float maxDist = 24;
    public float runSpeed = 1.6f;
    public float walkSpeed = 1f;
    protected Path path = null;

    public FleeGoal(AbstractHorseGenetic entityIn) {
        this.entity = entityIn;
        setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (toAvoid == null) {
            return false;
        }
        if (!canContinueToUse()) {
            return false;
        }
        setPath();
        return path != null;
    }

    private void setPath() {
        path = null;
        Vec3 loc = LandRandomPos.getPosAway(entity, 16, 7, toAvoid.position());
        if (loc == null) {
            loc = DefaultRandomPos.getPosAway(entity, 12, 7, toAvoid.position());
        }
        if (loc != null) {
            path = entity.getNavigation().createPath(loc.x, loc.y, loc.z, 0);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.isGroundTied() || entity.isLeashed() || entity.isVehicle()) {
            return false;
        }
        return entity.distanceToSqr(toAvoid) < maxDist * maxDist;
    }

    @Override
    public void start() {
        entity.getNavigation().moveTo(path, walkSpeed);
    }

    @Override
    public void stop() {
        toAvoid = null;
    }

    @Override
    public void tick() {
        if (entity.getNavigation().isDone()) {
            setPath();
            entity.getNavigation().moveTo(path, walkSpeed);
        }
        if (entity.distanceToSqr(toAvoid) < 49.0) {
            entity.getNavigation().setSpeedModifier(runSpeed);
        }
        else {
            entity.getNavigation().setSpeedModifier(walkSpeed);
        }
    }
}

