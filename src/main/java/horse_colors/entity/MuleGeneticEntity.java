package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import javax.annotation.Nullable;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.breed.*;
import sekelsta.horse_colors.entity.genetics.breed.donkey.*;
import sekelsta.horse_colors.entity.genetics.breed.horse.*;
import sekelsta.horse_colors.entity.genetics.HorseGenome;
import sekelsta.horse_colors.entity.genetics.Species;
import sekelsta.horse_colors.HorseColors;

public class MuleGeneticEntity extends AbstractHorseGenetic {
    protected static final DataParameter<Integer> SPECIES = EntityDataManager.<Integer>createKey(MuleGeneticEntity.class, DataSerializers.VARINT);

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "entities/mule");

    public MuleGeneticEntity(EntityType<? extends MuleGeneticEntity> entity, World world) {
        super(entity, world);
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(SPECIES, Species.MULE.ordinal());
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("Species", this.getSpecies().toString());
    }

   /**
    * Helper method to read subclass entity data from NBT.
    */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSpecies(Species.valueOf(compound.getString("Species")));
    }

    @Override
    public ResourceLocation getLootTable() {
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
        this.dataManager.set(SPECIES, species.ordinal());
    }

    @Override
    public Species getSpecies() {
        return Species.values()[this.dataManager.get(SPECIES).intValue()];
    }

    @Override
    protected boolean canMate() {
        return false;
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

    @Override
    // func_225513_by_() is getProfessionName()
    protected ITextComponent func_225513_by_() {
        if (!this.isChild() && !HorseConfig.BREEDING.enableGenders.get()
            && this.getSpecies() == Species.HINNY) {
            String s = "entity." + HorseColors.MODID + ".hinny";
            return new TranslationTextComponent(s);
        }
        return super.func_225513_by_();
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
        HorseGenome horse = new HorseGenome(Species.HORSE);
        horse.randomize(DefaultHorse.breed);
        HorseGenome donkey = new HorseGenome(Species.DONKEY);
        donkey.randomize(DefaultDonkey.breed);
        this.genes.inheritGenes(horse, donkey);
        this.useGeneticAttributes();
        return spawnDataIn;
    }
}
