package sekelsta.horse_colors.entity;
import net.minecraft.entity.passive.horse.*;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.genetics.HorseBreeds;
import sekelsta.horse_colors.init.ModEntities;
import sekelsta.horse_colors.item.GeneBookItem;
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
        this.updateHorseSlots();
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
    protected void updateHorseSlots() {
        super.updateHorseSlots();
        this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
        this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
    }

    private void setHorseArmorStack(ItemStack itemstack) {
        this.setHorseArmor(itemstack);
        if (!this.world.isRemote) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            if (itemstack.getItem() instanceof HorseArmorItem) {
                int i = ((HorseArmorItem)itemstack.getItem()).func_219977_e();
                if (i != 0) {
                    this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION)).setSaved(false));
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
    public GeneBookItem.Species getSpecies() {
        return GeneBookItem.Species.HORSE;
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
                child = ModEntities.HORSE_GENETIC.create(this.world);
            }
            else if (ageable instanceof DonkeyGeneticEntity) {
                child = ModEntities.MULE_GENETIC.create(this.world);
            }  
            return child;
        }
        else if (ageable instanceof HorseEntity) {
            HorseEntity child = EntityType.HORSE.create(this.world);
            ((HorseEntity)child).setHorseVariant(((HorseEntity)ageable).getHorseVariant());
            return child;
        }
        else if (ageable instanceof DonkeyEntity) {
            return EntityType.MULE.create(this.world);
        }
        return null;
    }

    public boolean wearsArmor() {
        return true;
    }

    @Override
    public boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof HorseArmorItem
                || (stack.getItem() instanceof BlockItem
                    && ((BlockItem)stack.getItem()).getBlock() instanceof CarpetBlock);
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
        this.getGenes().randomize(HorseBreeds.DEFAULT);
        this.useGeneticAttributes();
        return spawnDataIn;
    }
}
