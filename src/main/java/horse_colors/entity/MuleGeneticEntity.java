package sekelsta.horse_colors.entity;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.breed.BreedManager;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.EquineGenome;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;
import sekelsta.horse_colors.entity.genetics.Species;
import sekelsta.horse_colors.HorseColors;

public class MuleGeneticEntity extends AbstractHorseGenetic {
    protected static final EntityDataAccessor<Integer> SPECIES = SynchedEntityData.<Integer>defineId(MuleGeneticEntity.class, EntityDataSerializers.INT);

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "entities/mule");

    public MuleGeneticEntity(EntityType<? extends MuleGeneticEntity> entity, Level world) {
        super(entity, world);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(SPECIES, Species.MULE.ordinal());
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Species", this.getSpecies().toString());
    }

   /**
    * Helper method to read subclass entity data from NBT.
    */
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Species")) {
            this.setSpecies(Species.valueOf(compound.getString("Species")));
        }
    }

    @Override
    protected void readExtraGenes(CompoundTag compound) {
        // Do nothing
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.LOOT_TABLE;
    }

    @Override
    public boolean fluffyTail() {
        return true;
    }

    @Override
    public boolean longEars() {
        return true;
    }

    @Override
    public boolean thinMane() {
        return false;
    }

    public void setSpecies(Species species) {
        this.entityData.set(SPECIES, species.ordinal());
    }

    @Override
    public Species getSpecies() {
        return Species.values()[this.entityData.get(SPECIES).intValue()];
    }

    @Override
    public boolean isFertile() {
        return false;
    }

    @Override
    public void setFertile(boolean fertile) {
        // Pass
    }

    @Override
    protected boolean canTestGenetics() {
        return false;
    }

    @Override
    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    public AbstractHorse getChild(ServerLevel world, AgeableMob ageable) {
        MuleGeneticEntity child = ModEntities.MULE_GENETIC.get().create(this.level());
        return child;
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.MULE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.MULE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.MULE_HURT;
    }

    protected void playChestEquipSound() {
        this.playSound(SoundEvents.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    protected Component getTypeName() {
        if (!this.isBaby() && !HorseConfig.BREEDING.enableGenders.get()
            && this.getSpecies() == Species.HINNY) {
            String s = "entity." + HorseColors.MODID + ".hinny";
            return Component.translatable(s);
        }
        return super.getTypeName();
    }

    @Override
    public Collection<Breed<Gene>> getBreeds() {
        return ImmutableList.of(getDefaultBreed());
    }

    @Override
    protected void randomize(Breed breed) {
        super.randomize(breed);

        EquineGenome horse = new EquineGenome(Species.HORSE);
        horse.randomize(BreedManager.HORSE.getBreed("default_horse"));
        EquineGenome donkey = new EquineGenome(Species.DONKEY);
        donkey.randomize(BreedManager.DONKEY.getBreed("default_donkey"));
        this.genes.inheritGenes(horse, donkey);
    }
}
