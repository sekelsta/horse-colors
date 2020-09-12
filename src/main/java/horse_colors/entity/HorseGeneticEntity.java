package sekelsta.horse_colors.entity;

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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.genetics.breed.*;
import sekelsta.horse_colors.genetics.breed.horse.*;
import sekelsta.horse_colors.genetics.Species;
import sekelsta.horse_colors.util.Util;

public class HorseGeneticEntity extends AbstractHorseGenetic
{
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    public HorseGeneticEntity(EntityType<? extends HorseGeneticEntity> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (!this.horseChest.getStackInSlot(1).isEmpty()) {
            compound.put("ArmorItem", this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
        }
    }

   /**
    * Helper method to read subclass entity data from NBT.
    */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.read(compound.getCompound("ArmorItem"));
            if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
                this.horseChest.setInventorySlotContents(1, itemstack);
            }
        }
        this.func_230275_fc_();
    }

    public ItemStack getHorseArmor() {
        return this.getItemStackFromSlot(EquipmentSlotType.CHEST);
    }

    private void setHorseArmor(ItemStack itemstack) {
        this.setItemStackToSlot(EquipmentSlotType.CHEST, itemstack);
        this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
    }
   /**
    * Updates the items in the saddle and armor slots of the horse's inventory.
    */
    @Override
    // func_230275_fc_ = updateHorseSlots
    protected void func_230275_fc_() {
        if (!this.world.isRemote()) {
            super.func_230275_fc_();
            this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
            this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
        }
    }

    private void setHorseArmorStack(ItemStack itemstack) {
        // this.func_213805_k(itemStack);
        this.setHorseArmor(itemstack);
        if (!this.world.isRemote) {
            this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            // Do not use this.isArmor(itemstack)) because that can return true forthings which
            // can't be cast to HorseArmorItem
            if (itemstack.getItem() instanceof HorseArmorItem) {
                int i = ((HorseArmorItem)itemstack.getItem()).getArmorValue();
                if (i != 0) {
                    this.getAttribute(Attributes.ARMOR).applyNonPersistentModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION)));
                }
            }
        }
    }

   /**
    * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
    */
    public void onInventoryChanged(IInventory invBasic) {
        ItemStack itemstack = this.getHorseArmor();
        super.onInventoryChanged(invBasic);
        ItemStack itemstack1 = this.getHorseArmor();
        if (this.ticksExisted > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
        }
    }

    protected void playGallopSound(SoundType p_190680_1_) {
        super.playGallopSound(p_190680_1_);
        if (this.rand.nextInt(10) == 0) {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
        }
    }

    @Override
    public void tick() {
        super.tick();
        ItemStack stack = this.horseChest.getStackInSlot(1);
        if (isArmor(stack)) stack.onHorseArmorTick(world, this);
    }

    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.ENTITY_HORSE_ANGRY;
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
    public boolean canMateWith(AnimalEntity otherAnimal)
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
                child = ModEntities.HORSE_GENETIC.create(this.world);
            }
            else if (ageable instanceof DonkeyGeneticEntity) {
                child = ModEntities.MULE_GENETIC.create(this.world);
            }  
            return child;
        }
        else if (ageable instanceof HorseEntity) {
            // Breed the vanilla horse to itself
            AgeableEntity child = ageable.createChild(ageable);
            if (child instanceof AbstractHorseEntity) {
                return (AbstractHorseEntity)child;
            }
            // else
            return null;
        }
        else if (ageable instanceof DonkeyEntity) {
            return EntityType.MULE.create(this.world);
        }
        return null;
    }

    @Override
    // func_230276_fq_ = wearsArmor
    public boolean func_230276_fq_() {
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
}
