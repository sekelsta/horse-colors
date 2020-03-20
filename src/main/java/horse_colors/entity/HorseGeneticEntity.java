package sekelsta.horse_colors.entity;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
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
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.init.ModEntities;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;
import sekelsta.horse_colors.util.HorseAlleles;
import sekelsta.horse_colors.util.HorseColorCalculator;


public class HorseGeneticEntity extends AbstractHorseGenetic
{
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataParameter<Integer> HORSE_RANDOM = EntityDataManager.<Integer>createKey(HorseGeneticEntity.class, DataSerializers.VARINT);



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
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.initExtraAI();
    }

    @Override
    protected void initExtraAI() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(HORSE_RANDOM, Integer.valueOf(0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Random", this.getHorseVariant("random"));

        if (!this.horseChest.getStackInSlot(1).isEmpty())
        {
            compound.put("ArmorItem", this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
        }
    }

   private void setArmor(ItemStack itemStackIn) {
      this.setItemStackToSlot(EquipmentSlotType.CHEST, itemStackIn);
      this.setDropChance(EquipmentSlotType.CHEST, 0.0F);
   }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setHorseVariant(compound.getInt("Random"), "random");

        if (compound.contains("ArmorItem", 10))
        {
            ItemStack itemstack = ItemStack.read(compound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && this.isArmor(itemstack))
            {
                this.horseChest.setInventorySlotContents(1, itemstack);
            }
        }

        this.updateHorseSlots();
    }

    public void setHorseVariant(int variant, String type)
    {
        switch(type) {
            case "random":
                this.dataManager.set(HORSE_RANDOM, Integer.valueOf(variant));
                break;
            default:
                super.setHorseVariant(variant, type);
        }
        this.resetTexturePrefix();
    }

    public int getHorseVariant(String type)
    {
        switch(type) {
            case "random":
                return ((Integer)this.dataManager.get(HORSE_RANDOM)).intValue();
            default:
                return super.getHorseVariant(type);
        }
        
    }

    /**
     * Updates the items in the saddle and armor slots of the horse's inventory.
     */
    @Override
    protected void updateHorseSlots()
    {
        super.updateHorseSlots();
        this.setHorseArmorStack(this.horseChest.getStackInSlot(1));
    }

    /**
     * Set horse armor stack (for example: new ItemStack(Items.iron_horse_armor))
     */
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
    }

    public ItemStack getHorseArmor() {
        return this.getItemStackFromSlot(EquipmentSlotType.CHEST);
    }

    /**
     * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
     */
    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        ItemStack itemstack = this.getHorseArmor();
        super.onInventoryChanged(invBasic);
        ItemStack itemstack1 = this.getHorseArmor();

        if (this.ticksExisted > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
        }
    }

    @Override
    protected void playGallopSound(SoundType p_190680_1_)
    {
        super.playGallopSound(p_190680_1_);

        if (this.rand.nextInt(10) == 0)
        {
            this.playSound(SoundEvents.ENTITY_HORSE_BREATHE, p_190680_1_.getVolume() * 0.6F, p_190680_1_.getPitch());
        }
    }

    private void useGeneticAttributes()
    {
        if (HorseConfig.COMMON.useGeneticStats.get())
        {
            // Default horse health ranges from 15 to 30, but ours goes from
            // 15 to 31
            float maxHealth = 15.0F + getStat("health") * 0.5F;
            // Vanilla horse speed ranges from 0.1125 to 0.3375, as does ours
            double movementSpeed = 0.1125D + getStat("speed") * (0.225D / 32.0D);
            // Vanilla horse jump strength ranges from 0.4 to 1.0, as does ours
            double jumpStrength = 0.4D + getStat("jump") * (0.6D / 32.0D);

            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            this.getAttribute(JUMP_STRENGTH).setBaseValue(jumpStrength);
        }
    }

    @Override
    protected void registerAttributes()
    {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
        this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick()
    {
        super.tick();

        if (this.world.isRemote && this.dataManager.isDirty())
        {
            this.dataManager.setClean();
            this.resetTexturePrefix();
        }
        ItemStack armor = this.horseChest.getStackInSlot(1);
        if (isArmor(armor)) armor.onHorseArmorTick(world, this);
        // Overo lethal white syndrome
        if ((!this.world.isRemote || true)
            && this.getPhenotype("frame") == 2
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

    @Override
    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound()
    {
        super.getAngrySound();
        return SoundEvents.ENTITY_HORSE_ANGRY;
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean notEmpty = !itemstack.isEmpty();

        if (notEmpty && itemstack.getItem() instanceof SpawnEggItem)
        {
            return super.processInteract(player, hand);
        }
        else
        {
            if (!this.isChild())
            {
                //func_226563_dT_() == isSneaking()
                if (this.isTame() && player.func_226563_dT_())
                {
                    this.openGUI(player);
                    return true;
                }

                if (this.isBeingRidden())
                {
                    return super.processInteract(player, hand);
                }
            }

            if (notEmpty)
            {
                if (this.handleEating(player, itemstack))
                {
                    if (!player.abilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }

                    return true;
                }

                if (itemstack.interactWithEntity(player, this, hand))
                {
                    return true;
                }

                if (!this.isTame())
                {
                    this.makeMad();
                    return true;
                }

                boolean saddle = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;

                if (this.isArmor(itemstack) || saddle)
                {
                    this.openGUI(player);
                    return true;
                }
            }

            if (this.isChild())
            {
                return super.processInteract(player, hand);
            }
            else
            {
                this.mountTo(player);
                return true;
            }
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
        // Mate with other horses or donkeys
        else if (otherAnimal instanceof HorseGeneticEntity)
        {
            return this.canMate() && ((HorseGeneticEntity)otherAnimal).canMate();
        }
        else if (otherAnimal instanceof HorseEntity 
                || otherAnimal instanceof DonkeyEntity 
                || otherAnimal instanceof DonkeyGeneticEntity)
        {
         return this.canMate() && otherCanMate((AbstractHorseEntity)otherAnimal);
        }
        else
        {
            return false;
        }
    }

    private int inheritStats(AbstractHorseGenetic other, String chromosome) {
            int mother = this.getRandomGenericGenes(1, this.getHorseVariant(chromosome));
            int father = other.getRandomGenericGenes(0, other.getHorseVariant(chromosome));
            return mother | father;
    }

    @Override
    public AgeableEntity createChild(AgeableEntity ageable)
    {
        AbstractHorseEntity abstracthorse;

        if (ageable instanceof DonkeyEntity || ageable instanceof DonkeyGeneticEntity)
        {
            abstracthorse = EntityType.MULE.create(this.world);
        }
        else if (ageable instanceof HorseEntity) {
            HorseEntity horse = (HorseEntity)ageable;
            return horse.createChild(horse);
        }
        else
        {
            HorseGeneticEntity entityHorse = (HorseGeneticEntity)ageable;
            abstracthorse = ModEntities.HORSE_GENETIC.create(this.world);

            int mother = this.getRandomGenes(1, 0);
            int father = entityHorse.getRandomGenes(0, 0);
            int i = mother | father;
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(i, "0");

            mother = this.getRandomGenes(1, 1);
            father = entityHorse.getRandomGenes(0, 1);
            i = mother | father;
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(i, "1");


            ((HorseGeneticEntity)abstracthorse).setHorseVariant(rand.nextInt(), "2");
            mother = this.getRandomGenes(1, 2);
            father = entityHorse.getRandomGenes(0, 2);
            i = mother | father;
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(i, "2");

            // speed, health, and jump
            int speed = inheritStats(entityHorse, "speed");
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(speed, "speed");
            int health = inheritStats(entityHorse, "health");
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(health, "health");
            int jump = inheritStats(entityHorse, "jump");
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(jump, "jump");


            i =  this.rand.nextInt();
            ((HorseGeneticEntity)abstracthorse).setHorseVariant(i, "random");

            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (((HorseGeneticEntity)abstracthorse).getPhenotype("dominant_white")
                    == 2)
            {
                return null;
            }
        }

        this.setOffspringAttributes(ageable, abstracthorse);
        if (abstracthorse instanceof HorseGeneticEntity)
        {
            ((HorseGeneticEntity)abstracthorse).mutate();
            ((HorseGeneticEntity)abstracthorse).useGeneticAttributes();
        }
        return abstracthorse;
    }

    @Override
    public boolean wearsArmor()
    {
        return true;
    }

    @Override
    public boolean isArmor(ItemStack stack)
    {
        return stack.getItem() instanceof HorseArmorItem;
    }

    // with 1/odds probability gets the gene to 0 or 1, whichever common isn't
    private void setGeneRandom(String name, int n, int odds, int common)
    {
            int i = this.rand.nextInt();
            int rare = common == 0? 1 : 0;
            setGene(name, (i % odds == 0? rare : common) 
                            << (n * getGeneSize(name)));
    }

    /* This function changes the variant and then puts it back to what it was
    before. */
    private int getRandomVariant(int n, String type)
    {
        int answer = 0;
        int startVariant = getHorseVariant(type);

        if (type == "0")
        {
            // logical bitshift to make unsigned
            int i = this.rand.nextInt() >>> 1;
            setGene("extension", (i & 7) << (n * getGeneSize("extension")));
            i >>= 3;
            setGeneRandom("gray", n, 20, 0);
            int dun = (this.rand.nextInt() % 7 == 0? 2 : 0) + (i % 4 == 0? 1: 0);
            setGene("dun", dun << (n * getGeneSize("dun")));
            i >>= 2;

            int ag = i % 16;
            int agouti = ag == 0? HorseAlleles.A_BAY_MEALY 
                       : ag == 1? HorseAlleles.A_BAY_WILD
                       : ag < 4? HorseAlleles.A_BAY_LIGHT
                       : ag < 6? HorseAlleles.A_BAY
                       : ag < 8? HorseAlleles.A_BAY_DARK
                       : ag == 8? HorseAlleles.A_BROWN
                       : ag == 9? HorseAlleles.A_SEAL
                       : HorseAlleles.A_BLACK;
            setGene("agouti", agouti << (n * getGeneSize("agouti")));
            i >>= 4;

            setGeneRandom("silver", n, 32, 0);
            int cr = i % 32;
            int cream = cr == 0? HorseAlleles.CREAM
                      : cr == 1? HorseAlleles.PEARL
                      : cr == 2? HorseAlleles.NONCREAM2
                      : HorseAlleles.NONCREAM;
            setGene("cream", cream << (n * getGeneSize("cream")));
            i >>= 5;
            setGeneRandom("liver", n, 3, 1);
            setGeneRandom("flaxen1", n, 5, 1);
            setGeneRandom("flaxen2", n, 5, 1);

            setGene("dapple", (i % 2) << (n * getGeneSize("dapple")));
            i >>= 1;
        }
        else if (type == "1")
        {
            // logical bitshift to make unsigned
            int i = this.rand.nextInt() >>> 1;

            setGeneRandom("sooty1", n, 4, 1);
            setGeneRandom("sooty2", n, 4, 1);
            setGeneRandom("sooty3", n, 2, 1);
            setGeneRandom("mealy1", n, 4, 1);
            setGeneRandom("mealy2", n, 4, 1);
            setGeneRandom("mealy3", n, 4, 1);
            setGeneRandom("white_suppression", n, 32, 0);

            int kit = i % 4 != 0? 0
//                                : (i >> 2) % 2 == 0? (i >> 3) % 8
                                : (i >> 3) % 16;
            setGene("KIT", kit << (n * getGeneSize("KIT")));
            i >>= 7;

            setGeneRandom("frame", n, 32, 0);
            int mitf = i % 4 == 0? HorseAlleles.MITF_WILDTYPE
                : (i >> 2) % 2 == 0? (i >> 3) % 4
                : HorseAlleles.MITF_WILDTYPE;
            setGene("MITF", mitf << (n * getGeneSize("MITF")));
            i >>= 5;
            int pax3 = i % 4 != 0? HorseAlleles.PAX3_WILDTYPE
                : (i >> 2) % 4;
            setGene("PAX3", pax3 << (n * getGeneSize("PAX3")));
        }
        else if (type == "2")
        {
            // Initialize any bits currently unused to random values
            setHorseVariant(this.rand.nextInt(), "2");
            int i = this.rand.nextInt();
            setGeneRandom("leopard", n, 32, 0);
            setGeneRandom("PATN1", n, 16, 0);
            setGeneRandom("PATN2", n, 16, 0);
            setGeneRandom("PATN3", n, 16, 0);
            setGeneRandom("gray_suppression", n, 40, 0);
            setGeneRandom("gray_mane", n, 4, 0);
            setGeneRandom("slow_gray1", n, 8, 0);
            setGeneRandom("slow_gray2", n, 4, 0);
            setGeneRandom("white_star", n, 4, 0);
            setGeneRandom("white_forelegs", n, 4, 0);
            setGeneRandom("white_hindlegs", n, 4, 0);
        }

        answer = getHorseVariant(type);
        setHorseVariant(startVariant, type);
        return answer;
    }

    private void randomizeSingleVariant(String variant)
    {
        int i = getRandomVariant(0, variant);
        int j = getRandomVariant(1, variant);
        setHorseVariant(i | j, variant);
    }

    /* Make the horse have random genetics. */
    public void randomize()
    {
        randomizeSingleVariant("0");
        randomizeSingleVariant("1");
        randomizeSingleVariant("2");

        // Replace lethal white overos with heterozygotes
        if (getPhenotype("frame") == 2)
        {
            setGene("frame", 1);
        }

        // Homozygote dominant whites will be replaced with heterozygotes
        if (getPhenotype("dominant_white") == 2)
        {
            setGene("KIT", 15);
        }

        setHorseVariant(this.rand.nextInt(), "speed");
        setHorseVariant(this.rand.nextInt(), "jump");
        setHorseVariant(this.rand.nextInt(), "health");
        setHorseVariant(this.rand.nextInt(), "random");
        useGeneticAttributes();
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
        this.randomize();
        return spawnDataIn;
    }

    public static class HerdData implements ILivingEntityData
        {
            public int variant;

            public HerdData(int variantIn)
            {
                this.variant = variantIn;
            }
        }
}
