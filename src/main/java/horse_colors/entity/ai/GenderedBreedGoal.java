package sekelsta.horse_colors.entity.ai;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class GenderedBreedGoal extends EntityAIBase {
    private final EntityAnimal animal;
    private final Class <? extends EntityAnimal > mateClass;
    World world;
    private EntityAnimal targetMate;
    /** Delay preventing a baby from spawning immediately when two mate-able animals find each other. */
    int spawnBabyDelay;
    /** The speed the creature moves at during mating behavior. */
    double moveSpeed;


    public GenderedBreedGoal(EntityAnimal animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public GenderedBreedGoal(EntityAnimal animal, double moveSpeed, Class<? extends EntityAnimal> clazz) {
        this.animal = animal;
        this.world = animal.world;
        this.mateClass = clazz;
        this.moveSpeed = moveSpeed;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.animal.isInLove())
        {
            return false;
        }
        else
        {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void updateTask()
    {
        this.animal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.animal.getVerticalFaceSpeed());
        this.animal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay >= 60 && this.animal.getDistanceSq(this.targetMate) < 9.0D)
        {
            this.spawnBaby();
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityAnimal getNearbyMate()
    {
        List<EntityAnimal> list = this.world.<EntityAnimal>getEntitiesWithinAABB(this.mateClass, this.animal.getEntityBoundingBox().grow(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityAnimal entityanimal = null;

        for (EntityAnimal entityanimal1 : list)
        {
            if (this.animal.canMateWith(entityanimal1) && this.animal.getDistanceSq(entityanimal1) < d0)
            {
                entityanimal = entityanimal1;
                d0 = this.animal.getDistanceSq(entityanimal1);
            }
        }

        return entityanimal;
    }

   /**
    * Spawns a baby animal of the same type.
    */
    protected void spawnBaby() {
        EntityAgeable child = this.animal.createChild(this.targetMate);
        // If child is null, just let them try again
        if (child == null) {
            return;
        }
        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(animal, targetMate, child);
        boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        child = event.getChild();
        // Don't spawn a null entity
        if (child == null) {
            return;
        }

        int animalRebreedTicks = 6000;
        int mateRebreedTicks = 6000;

        if (this.animal instanceof IGeneticEntity && this.targetMate instanceof IGeneticEntity) {
            IGeneticEntity geneticAnimal = (IGeneticEntity)this.animal;
            IGeneticEntity geneticMate = (IGeneticEntity)this.targetMate;
            animalRebreedTicks = geneticAnimal.getRebreedTicks();
            mateRebreedTicks = geneticMate.getRebreedTicks();
            if (!cancelled && HorseConfig.isPregnancyEnabled()) {
                if (geneticAnimal.setPregnantWith(child, this.targetMate)) {
                    child = null;
                    // Spawn heart particles
                    this.world.setEntityState(this.animal, (byte)18);
                }
            }
        }

        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.animal.setGrowingAge(animalRebreedTicks);
            this.targetMate.setGrowingAge(mateRebreedTicks);
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            return;
        }

        EntityPlayerMP serverplayerentity = this.animal.getLoveCause();
        if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
            serverplayerentity = this.targetMate.getLoveCause();
        }

        if (serverplayerentity != null) {
            serverplayerentity.addStat(StatList.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, child);
        }

        this.animal.setGrowingAge(animalRebreedTicks);
        this.targetMate.setGrowingAge(mateRebreedTicks);
        this.animal.resetInLove();
        this.targetMate.resetInLove();
        if (child != null) {
            spawnChild(this.animal, child, this.world);
        }
        if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ, this.animal.getRNG().nextInt(7) + 1));
        }
    }

    public static void spawnChild(EntityAgeable mother, EntityAgeable child, World world) {
            if (child instanceof IGeneticEntity) {
                child.setGrowingAge(((IGeneticEntity)child).getBirthAge());
            }
            else {
                child.setGrowingAge(-24000);
            }
            child.setLocationAndAngles(mother.posX, mother.posY, mother.posZ, 0.0F, 0.0F);
            world.spawnEntity(child);
            // Spawn heart particles
            world.setEntityState(mother, (byte)18);
    }
}
