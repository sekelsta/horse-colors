package sekelsta.horse_colors.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.breed.horse.*;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.Species;
import sekelsta.horse_colors.util.Util;

public class HorseGeneticEntity extends AbstractHorseGenetic
{
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("minecraft", "entities/horse");

    public HorseGeneticEntity(EntityType<? extends HorseGeneticEntity> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        if (!this.inventory.getItem(1).isEmpty()) {
            compound.put("ArmorItem", this.inventory.getItem(1).save(new CompoundNBT()));
        }
    }

   /**
    * Helper method to read subclass entity data from NBT.
    */
    public void readAdditionalSaveData(CompoundNBT compound) {
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
        return this.getItemBySlot(EquipmentSlotType.CHEST);
    }

    private void setArmor(ItemStack itemstack) {
        this.setItemSlot(EquipmentSlotType.CHEST, itemstack);
        this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
    }
   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
    @Override
    protected void updateContainerEquipment() {
        if (!this.level.isClientSide()) {
            super.updateContainerEquipment();
            this.setArmorStack(this.inventory.getItem(1));
            this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
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
    public void containerChanged(IInventory invBasic) {
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
    public boolean canMate(AnimalEntity otherAnimal)
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
                || otherAnimal instanceof DonkeyEntity 
                || otherAnimal instanceof HorseEntity)
        {
            return this.canParent() && Util.horseCanMate((AbstractHorseEntity)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    @Override
    public AbstractHorseEntity getChild(ServerWorld world, AgeableEntity ageable)
    {
        if (ageable instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic child = null;
            AbstractHorseGenetic other = (AbstractHorseGenetic)ageable;
            if (ageable instanceof HorseGeneticEntity) {
                child = ModEntities.HORSE_GENETIC.create(this.level);
            }
            else if (ageable instanceof DonkeyGeneticEntity) {
                child = ModEntities.MULE_GENETIC.create(this.level);
                if (HorseConfig.BREEDING.enableGenders.get()
                        && this.isMale() && !((DonkeyGeneticEntity)ageable).isMale()) {
                    ((MuleGeneticEntity)child).setSpecies(Species.HINNY);
                }
            }  
            return child;
        }
        else if (ageable instanceof HorseEntity) {
            // Breed the vanilla horse to itself
            // func_241840_a = createChild
            AgeableEntity child = ageable.getBreedOffspring(world, ageable);
            if (child instanceof AbstractHorseEntity) {
                return (AbstractHorseEntity)child;
            }
            // else
            return null;
        }
        else if (ageable instanceof DonkeyEntity) {
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
                    && ((BlockItem)stack.getItem()).getBlock() instanceof CarpetBlock);
    }

    @Override
    public Breed getDefaultBreed() {
        return DefaultHorse.breed;
    }

    @Override
    public int getPopulation() {
        return 60000000;
    }

    @Override
    public List<Breed> getBreeds() {
        // This must only be called serverside, because the client does not
        // load the data packs the breeds use
        return ImmutableList.of(Appaloosa.breed, 
        Hucul.breed, MongolianHorse.breed, QuarterHorse.breed, Friesian.breed, 
        ClevelandBay.breed);
    }

    // Set stats for vanilla-like breeding
    @Override
    protected void randomizeAttributes() {
        super.randomizeAttributes();
        if (!HorseConfig.GENETICS.useGeneticStats.get()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
            this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
        }
    }
}
