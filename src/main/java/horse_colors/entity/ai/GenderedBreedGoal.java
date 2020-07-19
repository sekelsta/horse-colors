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
    protected void spawnBaby() {
        if (!(this.animal instanceof IGeneticEntity) || !(this.targetMate instanceof IGeneticEntity)) {
            super.spawnBaby();
            return;
        }
        IGeneticEntity geneticAnimal = (IGeneticEntity)this.animal;
        IGeneticEntity geneticMate = (IGeneticEntity)this.targetMate;

        AgeableEntity ageableentity = this.animal.createChild(this.targetMate);
        boolean cancelled = ageableentity == null;
        if (!cancelled) {
            final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(animal, targetMate, ageableentity);
            cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            ageableentity = event.getChild();
        }
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.animal.setGrowingAge(geneticAnimal.getRebreedTicks());
            this.targetMate.setGrowingAge(geneticMate.getRebreedTicks());
            this.animal.resetInLove();
            this.targetMate.resetInLove();
            return;
        }
        if (ageableentity != null) {
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
            if (ageableentity instanceof IGeneticEntity) {
                ageableentity.setGrowingAge(((IGeneticEntity)ageableentity).getBirthAge());
            }
            else {
                ageableentity.setGrowingAge(-24000);
            }
            ageableentity.setLocationAndAngles(this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), 0.0F, 0.0F);
            this.world.addEntity(ageableentity);
            this.world.setEntityState(this.animal, (byte)18);
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.addEntity(new ExperienceOrbEntity(this.world, this.animal.getPosX(), this.animal.getPosY(), this.animal.getPosZ(), this.animal.getRNG().nextInt(7) + 1));
            }

        }
    }
}
