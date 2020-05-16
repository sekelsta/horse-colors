package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import javax.annotation.Nullable;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.genetics.DonkeyBreeds;
import sekelsta.horse_colors.genetics.HorseBreeds;
import sekelsta.horse_colors.genetics.HorseGenome;
import sekelsta.horse_colors.item.GeneBookItem;
import sekelsta.horse_colors.init.ModEntities;

public class MuleGeneticEntity extends AbstractHorseGenetic {
    public MuleGeneticEntity(EntityType<? extends MuleGeneticEntity> p_i50239_1_, World p_i50239_2_) {
        super(p_i50239_1_, p_i50239_2_);
    }

    public boolean fluffyTail() {
        return true;
    }

    public boolean longEars() {
        return true;
    }

    public boolean thinMane() {
        return false;
    }

    @Override
    public GeneBookItem.Species getSpecies() {
        return GeneBookItem.Species.MULE;
    }

    @Override
    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    public AbstractHorseEntity getChild(AgeableEntity ageable) {
        MuleGeneticEntity child = ModEntities.MULE_GENETIC.create(this.world);
        return child;
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_MULE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_MULE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_MULE_HURT;
    }

    protected void playChestEquipSound() {
        this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }
    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        HorseGenome horse = new HorseGenome();
        horse.randomize(HorseBreeds.DEFAULT);
        HorseGenome donkey = new HorseGenome();
        donkey.randomize(DonkeyBreeds.DEFAULT);
        this.genes.inheritGenes(horse, donkey);
        this.useGeneticAttributes();
        return spawnDataIn;
    }
}
