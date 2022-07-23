package sekelsta.horse_colors.entity;
import net.minecraft.world.entity.animal.horse.*;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.breed.BreedManager;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;
import sekelsta.horse_colors.entity.genetics.Species;
import sekelsta.horse_colors.util.Util;

public class HorseGeneticEntity extends AbstractHorseGenetic
{
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "entities/horse");

    public HorseGeneticEntity(EntityType<? extends HorseGeneticEntity> entityType, Level worldIn)
    {
        super(entityType, worldIn);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (!this.inventory.getItem(1).isEmpty()) {
            compound.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
        }
    }

   /**
    * Helper method to read subclass entity data from NBT.
    */
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.of(compound.getCompound("ArmorItem"));
            if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
                this.inventory.setItem(1, itemstack);
            }
        }
        this.updateContainerEquipment();
    }

    public ItemStack getArmor() {
        return this.getItemBySlot(EquipmentSlot.CHEST);
    }

    private void setArmor(ItemStack itemstack) {
        this.setItemSlot(EquipmentSlot.CHEST, itemstack);
        this.setDropChance(EquipmentSlot.CHEST, 0.0F);
    }
   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
    @Override
    protected void updateContainerEquipment() {
        if (!this.level.isClientSide()) {
            super.updateContainerEquipment();
            this.setArmorStack(this.inventory.getItem(1));
            this.setDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }

    private void setArmorStack(ItemStack itemstack) {
        // this.func_213805_k(itemStack);
        this.setArmor(itemstack);
        if (!this.level.isClientSide) {
            this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            // Do not use this.isArmor(itemstack)) because that can return true for things which
            // can't be cast to HorseArmorItem
            if (itemstack.getItem() instanceof HorseArmorItem) {
                int i = ((HorseArmorItem)itemstack.getItem()).getProtection();
                if (i != 0) {
                    this.getAttribute(Attributes.ARMOR).addTransientModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION)));
                }
            }
        }
    }

   /**
    * Called by InventoryBasic.containerChanged() on a array that is never filled.
    */
    public void containerChanged(Container invBasic) {
        ItemStack itemstack = this.getArmor();
        super.containerChanged(invBasic);
        ItemStack itemstack1 = this.getArmor();
        if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
        }
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
    public void tick() {
        super.tick();
        ItemStack stack = this.inventory.getItem(1);
        if (isArmor(stack)) stack.onHorseArmorTick(this.level, this);
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.HORSE_HURT;
    }

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
                child = ModEntities.HORSE_GENETIC.get().create(this.level);
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
            return EntityType.MULE.create(this.level);
        }
        return null;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof HorseArmorItem
                || (stack.getItem() instanceof BlockItem
                    && ((BlockItem)stack.getItem()).getBlock() instanceof WoolCarpetBlock);
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
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed(rand));
            this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength(rand));
        }
    }
}
