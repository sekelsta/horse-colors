package sekelsta.horse_colors.entity;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import sekelsta.horse_colors.genetics.breed.*;
import sekelsta.horse_colors.genetics.breed.donkey.*;
import sekelsta.horse_colors.genetics.Species;
import sekelsta.horse_colors.util.Util;

public class DonkeyGeneticEntity extends AbstractHorseGenetic {
    public DonkeyGeneticEntity(World world) {
        super(world);
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

    @Override
    public boolean fluffyTail() {
        return false;
    }

    @Override
    public boolean longEars() {
        return true;
    }

    @Override
    public boolean thinMane() {
        return true;
    }

    @Override
    public Species getSpecies() {
        return Species.DONKEY;
    }

   /**
    * Returns true if the mob is currently able to mate with the specified mob.
    */
    @Override
    public boolean canMateWith(EntityAnimal otherAnimal) {
        if (otherAnimal == this)
        {
            return false;
        }
        if (otherAnimal instanceof AbstractHorseGenetic) {
            if (!this.isOppositeGender((AbstractHorseGenetic)otherAnimal)) {
                return false;
            }
        }
        if (otherAnimal instanceof DonkeyGeneticEntity 
                || otherAnimal instanceof HorseGeneticEntity
                || otherAnimal instanceof EntityDonkey 
                || otherAnimal instanceof EntityHorse)
        {
            return this.canMate() && Util.horseCanMate((AbstractHorse)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    @Override
    public AbstractHorse getChild(EntityAgeable ageable)
    {
        if (ageable instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic child = null;
            AbstractHorseGenetic other = (AbstractHorseGenetic)ageable;
            if (ageable instanceof HorseGeneticEntity) {
                child = new MuleGeneticEntity(this.world);
            }
            else if (ageable instanceof DonkeyGeneticEntity) {
                child = new DonkeyGeneticEntity(this.world);
            }
            return child;
        }
        else if (ageable instanceof EntityHorse) {
            return new EntityMule(this.world);
        }
        else if (ageable instanceof EntityDonkey) {
            return new EntityDonkey(this.world);
        }
        return null;
    }

    @Override
    public Breed getDefaultBreed() {
        return DefaultDonkey.breed;
    }
}
