package sekelsta.horse_colors.entity.ai;
import net.minecraft.entity.ai.goal.*;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class GenderedBreedGoal extends BreedGoal {

    public GenderedBreedGoal(AnimalEntity animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public GenderedBreedGoal(AnimalEntity animal, double moveSpeed, Class<? extends AnimalEntity> clazz) {
        super(animal, moveSpeed, clazz);
    }

   /**
    * Spawns a baby animal of the same type.
    */
    @Override
    protected void spawnBaby() {
        if (!(this.animal instanceof IGeneticEntity) || !(this.targetMate instanceof IGeneticEntity)) {
            super.spawnBaby();
            return;
        }
        IGeneticEntity geneticAnimal = (IGeneticEntity)this.animal;
        IGeneticEntity geneticMate = (IGeneticEntity)this.targetMate;

        AgeableEntity ageableentity = this.animal.createChild(this.targetMate);
        // If ageableentity is null, just let them try again
        if (ageableentity == null) {
            return;
        }
        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(animal, targetMate, ageableentity);
        boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.animal.setGrowingAge(geneticAnimal.getRebreedTicks());
            this.targetMate.setGrowingAge(geneticMate.getRebreedTicks());
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            return;
        }
        // Don't spawn a null entity
        if (ageableentity == null) {
            return;
        }
        if (HorseConfig.isPregnancyEnabled()) {
            if (geneticAnimal.setPregnantWith(ageableentity, this.targetMate)) {
                ageableentity = null;
                // Spawn heart particles
                this.world.setEntityState(this.animal, (byte)18);
            }
        }
        ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
        if (serverplayerentity == null && this.targetMate.getLoveCause() != null) {
            serverplayerentity = this.targetMate.getLoveCause();
        }

        if (serverplayerentity != null) {
            serverplayerentity.addStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.targetMate, ageableentity);
        }

        this.animal.setGrowingAge(geneticAnimal.getRebreedTicks());
        this.targetMate.setGrowingAge(geneticMate.getRebreedTicks());
        this.animal.resetInLove();
        this.targetMate.resetInLove();
        if (ageableentity != null) {
            spawnChild(this.animal, ageableentity, this.world);
        }
        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), this.animal.getRNG().nextInt(7) + 1));
        }
    }

    public static void spawnChild(AgeableEntity mother, AgeableEntity child, World world) {
            if (child instanceof IGeneticEntity) {
                child.setGrowingAge(((IGeneticEntity)child).getBirthAge());
            }
            else {
                child.setGrowingAge(-24000);
            }
            child.setLocationAndAngles(mother.getPosX(), mother.getPosY(), mother.getPosZ(), 0.0F, 0.0F);
            world.addEntity(child);
            // Spawn heart particles
            world.setEntityState(mother, (byte)18);
    }
}
