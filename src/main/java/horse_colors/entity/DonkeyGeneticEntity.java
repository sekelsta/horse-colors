package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.genetics.HorseBreeds;
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

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    @Override
    public AbstractHorseEntity getChild(AgeableEntity ageable)
    {
        if (ageable instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic child = null;
            AbstractHorseGenetic other = (AbstractHorseGenetic)ageable;
            if (ageable instanceof HorseGeneticEntity) {
                child = ModEntities.MULE_GENETIC.create(this.world);
            }
            else if (ageable instanceof DonkeyGeneticEntity) {
                child = ModEntities.DONKEY_GENETIC.create(this.world);
            }
            return child;
        }
        else if (ageable instanceof HorseEntity) {
            return EntityType.MULE.create(this.world);
        }
        else if (ageable instanceof DonkeyEntity) {
            return EntityType.DONKEY.create(this.world);
        }
        return null;
    }

    @Override
    public Map<String, List<Float>> getSpawnFrequencies() {
        return HorseBreeds.DONKEY;
    }
}
