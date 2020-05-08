package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

//import sekelsta.horse_colors.genetics.*;
import sekelsta.horse_colors.init.ModEntities;
import sekelsta.horse_colors.item.GeneBookItem;
import sekelsta.horse_colors.util.Util;

public class DonkeyGeneticEntity extends AbstractHorseGenetic {
    public DonkeyGeneticEntity(EntityType<? extends DonkeyGeneticEntity> p_i50239_1_, World p_i50239_2_) {
        super(p_i50239_1_, p_i50239_2_);
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_DONKEY_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_DONKEY_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_DONKEY_HURT;
    }

    public boolean fluffyTail() {
        return false;
    }

    public boolean longEars() {
        return true;
    }

    public boolean thinMane() {
        return true;
    }

    @Override
    public GeneBookItem.Species getSpecies() {
        return GeneBookItem.Species.DONKEY;
    }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        if (otherAnimal == this)
        {
            return false;
        }
        else if (otherAnimal instanceof DonkeyGeneticEntity 
                || otherAnimal instanceof HorseGeneticEntity
                || otherAnimal instanceof DonkeyEntity 
                || otherAnimal instanceof HorseEntity)
        {
            return this.canMate() && Util.horseCanMate((AbstractHorseEntity)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        AbstractHorseEntity abstracthorse;

        if (ageable instanceof HorseGeneticEntity)
        {
            abstracthorse = ModEntities.MULE_GENETIC.create(this.world);
            HorseGeneticEntity entityHorse = (HorseGeneticEntity)ageable;
            this.getGenes().setChildGenes(entityHorse.getGenes(), ((MuleGeneticEntity)abstracthorse));

            int i =  this.rand.nextInt();
            ((MuleGeneticEntity)abstracthorse).setChromosome("random", i);

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((MuleGeneticEntity)abstracthorse).getGenes().isEmbryonicLethal())
            {
                return null;
            }
            this.setOffspringAttributes(ageable, abstracthorse);
            ((MuleGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        else if (ageable instanceof DonkeyGeneticEntity)
        {
            abstracthorse = ModEntities.DONKEY_GENETIC.create(this.world);
            DonkeyGeneticEntity entityHorse = (DonkeyGeneticEntity)ageable;
            this.getGenes().setChildGenes(entityHorse.getGenes(), ((DonkeyGeneticEntity)abstracthorse));

            int i =  this.rand.nextInt();
            ((DonkeyGeneticEntity)abstracthorse).setChromosome("random", i);

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((DonkeyGeneticEntity)abstracthorse).getGenes().isEmbryonicLethal())
            {
                return null;
            }
            this.setOffspringAttributes(ageable, abstracthorse);
            ((DonkeyGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        else
        {
            return super.createChild(ageable);
        }

        return abstracthorse;
    }
}
