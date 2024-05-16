package sekelsta.horse_colors.entity;
import net.minecraft.world.entity.animal.horse.*;

import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.SoundType;

import sekelsta.horse_colors.HorseConfig;
import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.breed.BreedManager;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;
import sekelsta.horse_colors.entity.genetics.Species;
import sekelsta.horse_colors.util.Util;

public class HorseGeneticEntity extends AbstractHorseGenetic
{
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "entities/horse");

    public HorseGeneticEntity(EntityType<? extends HorseGeneticEntity> entityType, Level worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.LOOT_TABLE;
    }

    protected void playGallopSound(SoundType p_190680_1_) {
        super.playGallopSound(p_190680_1_);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.HORSE_ANGRY;
    }

    @Override
    public boolean fluffyTail() {
        return true;
    }
    @Override
    public boolean longEars() {
        return false;
    }

    @Override
    public boolean thinMane() {
        return false;
    }

    @Override
    public boolean canEquipChest() {
        return false;
    }

    @Override
    public Species getSpecies() {
        return Species.HORSE;
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    @Override
    public boolean canMate(Animal otherAnimal)
    {
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
                || otherAnimal instanceof Donkey 
                || otherAnimal instanceof Horse)
        {
            return this.canParent() && Util.horseCanMate((AbstractHorse)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    @Override
    public AbstractHorse getChild(ServerLevel world, AgeableMob ageable)
    {
        if (ageable instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic child = null;
            AbstractHorseGenetic other = (AbstractHorseGenetic)ageable;
            if (ageable instanceof HorseGeneticEntity) {
                child = ModEntities.HORSE_GENETIC.get().create(this.level());
            }
            else if (ageable instanceof DonkeyGeneticEntity) {
                child = ModEntities.MULE_GENETIC.get().create(this.level());
                if (HorseConfig.BREEDING.enableGenders.get()
                        && this.isMale() && !((DonkeyGeneticEntity)ageable).isMale()) {
                    ((MuleGeneticEntity)child).setSpecies(Species.HINNY);
                }
            }  
            return child;
        }
        else if (ageable instanceof Horse) {
            // Breed the vanilla horse to itself
            // func_241840_a = createChild
            AgeableMob child = ageable.getBreedOffspring(world, ageable);
            if (child instanceof AbstractHorse) {
                return (AbstractHorse)child;
            }
            // else
            return null;
        }
        else if (ageable instanceof Donkey) {
            return EntityType.MULE.create(this.level());
        }
        return null;
    }

    @Override
    public boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof HorseArmorItem
                || stack.is(ItemTags.WOOL_CARPETS);
    }

    @Override
    public boolean isBreedingFood(ItemStack stack) {
        return HorseConfig.isHorseBreedingFood(stack);
    }

    @Override
    public Breed getDefaultBreed() {
        return BreedManager.HORSE.getBreed("default_horse");
    }

    @Override
    public Collection<Breed<Gene>> getBreeds() {
        return BreedManager.HORSE.getAllBreeds();
    }

    @Override
    public int getPopulation() {
        return 60000000;
    }

    // Set stats for vanilla-like breeding
    @Override
    protected void randomizeAttributes(RandomSource rand) {
        super.randomizeAttributes(rand);
        if (!HorseConfig.GENETICS.useGeneticStats.get()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateSpeed(rand::nextDouble));
            this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateJumpStrength(rand::nextDouble));
        }
    }
}
