package sekelsta.horse_colors.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BookItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ai.RandomWalkGroundTie;
import sekelsta.horse_colors.init.ModEntities;
import sekelsta.horse_colors.init.ModItems;
import sekelsta.horse_colors.item.GeneBookItem;
import sekelsta.horse_colors.genetics.*;
import sekelsta.horse_colors.util.Util;


public class HorseGeneticEntity extends HorseEntity implements IHorseShape, IGeneticEntity
{
    private HorseGenome genes;
    //protected static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_SPEED = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_JUMP = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_HEALTH = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_RANDOM = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DISPLAY_AGE = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);

    public HorseGeneticEntity(EntityType<? extends HorseGeneticEntity> entityType, World worldIn)
    {
        super(entityType, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomWalkGroundTie(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.initExtraAI();
    }

    public HorseGenome getGenes() {
        return genes;
    }

    public java.util.Random getRand() {
        return this.rand;
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        //this.dataManager.register(HORSE_VARIANT, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT2, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT3, Integer.valueOf(0));
        this.dataManager.register(HORSE_SPEED, Integer.valueOf(0));
        this.dataManager.register(HORSE_HEALTH, Integer.valueOf(0));
        this.dataManager.register(HORSE_JUMP, Integer.valueOf(0));
        this.dataManager.register(HORSE_RANDOM, Integer.valueOf(0));
        this.dataManager.register(DISPLAY_AGE, Integer.valueOf(0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        //compound.putInt("Variant", this.getHorseVariant());
        compound.putInt("Variant2", this.getChromosome("1"));
        compound.putInt("Variant3", this.getChromosome("2"));
        compound.putInt("SpeedGenes", this.getChromosome("speed"));
        compound.putInt("JumpGenes", this.getChromosome("jump"));
        compound.putInt("HealthGenes", this.getChromosome("health"));
        compound.putInt("Random", this.getChromosome("random"));
        compound.putInt("display_age", this.getDisplayAge());
    }

    public ItemStack getHorseArmor() {
        return this.getItemStackFromSlot(EquipmentSlotType.CHEST);
    }
/*
   private void setArmor(ItemStack itemStackIn) {
      this.setItemStackToSlot(EquipmentSlotType.CHEST, itemStackIn);
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }
*/
    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        //this.setHorseVariant(compound.getInt("Variant"));
        this.setChromosome("1", compound.getInt("Variant2"));
        this.setChromosome("2", compound.getInt("Variant3"));
        this.setChromosome("speed", compound.getInt("SpeedGenes"));
        this.setChromosome("jump", compound.getInt("JumpGenes"));
        this.setChromosome("health", compound.getInt("HealthGenes"));
        this.setChromosome("random", compound.getInt("Random"));
        this.setDisplayAge(compound.getInt("display_age"));

        this.updateHorseSlots();
    }

    /**
     * Set horse armor stack (for example: new ItemStack(Items.iron_horse_armor))
     *//*
    public void setHorseArmorStack(ItemStack itemStackIn)
    {
      this.setArmor(itemStackIn);
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         if (this.isArmor(itemStackIn)) {
            // getProtection, possibly
            int i = ((HorseArmorItem)itemStackIn.getItem()).func_219977_e();
            if (i != 0) {
               this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION)).setSaved(false));
            }
         }
      }
    }*/

    private void useGeneticAttributes()
    {
        if (HorseConfig.COMMON.useGeneticStats.get())
        {
            // Default horse health ranges from 15 to 30, but ours goes from
            // 15 to 31
            float maxHealth = 15.0F + this.getGenes().getStat("health") * 0.5F;
            maxHealth += this.getGenes().getBaseHealth();
            // Vanilla horse speed ranges from 0.1125 to 0.3375, as does ours
            double movementSpeed = 0.1125D + this.getGenes().getStat("speed") * (0.225D / 32.0D);
            // Vanilla horse jump strength ranges from 0.4 to 1.0, as does ours
            double jumpStrength = 0.4D + this.getGenes().getStat("jump") * (0.6D / 32.0D);

            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            this.getAttribute(JUMP_STRENGTH).setBaseValue(jumpStrength);
        }
        else {
            float maxHealth = this.getModifiedMaxHealth() + this.getGenes().getBaseHealth();
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        }
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        genes = new HorseGenome(this);
        float maxHealth = this.getModifiedMaxHealth() + this.getGenes().getBaseHealth();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
        this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick()
    {
        // This has to be done before the call to super because HorseEntity
        // will set the datamanager to clean
        if (this.world.isRemote && this.dataManager.isDirty()) {
            this.dataManager.setClean();
            this.getGenes().resetTexture();
        }
        super.tick();

        // Align age
        if (!this.world.isRemote) {
            // Adjust age, compensating for what AgeableEntity's tick function does
            // Bound above so there is no wrapping
            if (this.growingAge >= 0 && this.growingAge < 0x1000000) {
                // Compensate for AgeableEntity's tick function decreasing it
                this.setGrowingAge(this.growingAge + 2);
            }
            // Allow imprecision
            final int c = 200;
            if (this.growingAge / c != this.getDisplayAge() / c) {
                this.setDisplayAge(this.growingAge);
            }
        }

        // Overo lethal white syndrome
        if ((!this.world.isRemote || true)
            && this.getGenes().isLethalWhite()
            && this.ticksExisted > 80)
        {
            if (!this.isPotionActive(Effects.POISON))
            {
                this.addPotionEffect(new EffectInstance(Effects.POISON, 100, 3));
            }
            if (this.getHealth() < 2)
            {
                this.addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE, 1, 3));
            }
        }
    }

    // For IHorseShape
    public boolean fluffyTail() {
        return true;
    }

    public boolean longEars() {
        return false;
    }

    public boolean thinMane() {
        return false;
    }

    public int getDisplayAge() {
        return this.dataManager.get(DISPLAY_AGE);
    }

    public void setDisplayAge(int age) {
        this.dataManager.set(DISPLAY_AGE, age);
    }

    public void setChromosome(String name, int variant)
    {
        switch(name) {
            case "0":
                this.setHorseVariant(variant);
                this.getGenes().resetTexture();
                return;
            case "1":
                this.dataManager.set(HORSE_VARIANT2, variant);
                this.getGenes().resetTexture();
                return;
            case "2":
                this.dataManager.set(HORSE_VARIANT3, variant);
                this.getGenes().resetTexture();
                return;
            case "speed":
                this.dataManager.set(HORSE_SPEED, variant);
                this.useGeneticAttributes();
                return;
            case "jump":
                this.dataManager.set(HORSE_JUMP, variant);
                this.useGeneticAttributes();
                return;
            case "health":
                this.dataManager.set(HORSE_HEALTH, variant);
                this.useGeneticAttributes();
                return;
            case "random":
                this.dataManager.set(HORSE_RANDOM, Integer.valueOf(variant));
                break;
            default:
                System.out.print("Unrecognized horse data for setting: "
                                 + name + "\n");
        }
    }

    public int getChromosome(String name)
    {
        switch(name) {
            case "0":
                return this.getHorseVariant();
            case "1":
                return ((Integer)this.dataManager.get(HORSE_VARIANT2)).intValue();
            case "2":
                return ((Integer)this.dataManager.get(HORSE_VARIANT3)).intValue();
            case "speed":
                return ((Integer)this.dataManager.get(HORSE_SPEED)).intValue();
            case "jump":
                return ((Integer)this.dataManager.get(HORSE_JUMP)).intValue();
            case "health":
                return ((Integer)this.dataManager.get(HORSE_HEALTH)).intValue();
            case "random":
                return ((Integer)this.dataManager.get(HORSE_RANDOM)).intValue();
            default:
                System.out.print("Unrecognized horse data for getting: " 
                                + name + "\n");
                return 0;
        }
        
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof BookItem 
                && (this.isTame() || player.abilities.isCreativeMode)) {
            ItemStack book = new ItemStack(ModItems.geneBookItem);
            if (book.getTag() == null) {
                book.setTag(new CompoundNBT());
            }
            book.getTag().putString("species", GeneBookItem.Species.HORSE.name());
            book.getTag().putString("genes", this.getGenes().genesToString());
            if (!player.addItemStackToInventory(book)) {
                this.entityDropItem(book);
            }
            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            return true;
        }
        else {
            return super.processInteract(player, hand);
        }
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

    @Override
    public AgeableEntity createChild(AgeableEntity ageable)
    {
        AbstractHorseEntity abstracthorse;

        if (ageable instanceof DonkeyGeneticEntity)
        {
            abstracthorse = ModEntities.MULE_GENETIC.create(this.world);
            DonkeyGeneticEntity entityHorse = (DonkeyGeneticEntity)ageable;
            this.getGenes().setChildGenes(entityHorse.getGenes(), ((MuleGeneticEntity)abstracthorse));

            int i =  this.rand.nextInt();
            ((MuleGeneticEntity)abstracthorse).setChromosome("random", i);

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((MuleGeneticEntity)abstracthorse).getGenes().isEmbryonicLethal())
            {
                return null;
            }
            this.setOffspringAttributes(ageable, abstracthorse);
            ((MuleGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        else if (ageable instanceof HorseGeneticEntity)
        {
            abstracthorse = ModEntities.HORSE_GENETIC.create(this.world);
            HorseGeneticEntity entityHorse = (HorseGeneticEntity)ageable;
            this.getGenes().setChildGenes(entityHorse.getGenes(), ((HorseGeneticEntity)abstracthorse));

            int i =  this.rand.nextInt();
            ((HorseGeneticEntity)abstracthorse).setChromosome("random", i);

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((HorseGeneticEntity)abstracthorse).getGenes().isEmbryonicLethal())
            {
                return null;
            }
            this.setOffspringAttributes(ageable, abstracthorse);
            ((HorseGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        else
        {
            return super.createChild(ageable);
        }

        return abstracthorse;
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
        this.getGenes().randomize();
        this.useGeneticAttributes();
        return spawnDataIn;
    }
}
