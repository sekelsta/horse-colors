package sekelsta.horse_colors.entity;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BookItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ai.*;
import sekelsta.horse_colors.entity.genetics.*;
import sekelsta.horse_colors.entity.genetics.breed.*;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.item.ModItems;
import sekelsta.horse_colors.item.GeneBookItem;
import sekelsta.horse_colors.util.Util;

public abstract class AbstractHorseGenetic extends AbstractChestedHorseEntity implements IGeneticEntity {
    public static final double PLAYER_OFFSET = -0.295;

    protected HorseGenome genes = new HorseGenome(this.getSpecies(), this);
    protected static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT4 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT5 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT6 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_SPEED = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_JUMP = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_HEALTH = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_MHC1 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_MHC2 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_IMMUNE = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_RANDOM = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> DISPLAY_AGE = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> GENDER = EntityDataManager.<Boolean>createKey(AbstractHorseGenetic.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> IS_CASTRATED = EntityDataManager.<Boolean>createKey(AbstractHorseGenetic.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> PREGNANT_SINCE = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Float> MOTHER_SIZE = EntityDataManager.<Float>createKey(AbstractHorseGenetic.class, DataSerializers.FLOAT);
    protected int trueAge;

    protected static final UUID CSNB_SPEED_UUID = UUID.fromString("84ca527a-5c70-4336-a737-ae3f6d40ef45");
    protected static final UUID CSNB_JUMP_UUID = UUID.fromString("72323326-888b-4e46-bf52-f669600642f7");
    protected static final AttributeModifier CSNB_SPEED_MODIFIER = (new AttributeModifier(CSNB_SPEED_UUID, "CSNB speed penalty", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL));
    protected static final AttributeModifier CSNB_JUMP_MODIFIER = (new AttributeModifier(CSNB_JUMP_UUID, "CSNB jump penalty", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL));

    protected static final int HORSE_GENETICS_VERSION = 1;

    protected List<AbstractHorseGenetic> unbornChildren = new ArrayList<>();

    public AbstractHorseGenetic(EntityType<? extends AbstractHorseGenetic> entityType, World worldIn)
    {
        super(entityType, worldIn);
        this.setChromosome("random", this.rand.nextInt());
        this.setMale(this.rand.nextBoolean());
        this.dataManager.set(PREGNANT_SINCE, -1);
    }

    public HorseGenome getGenes() {
        return genes;
    }

    public abstract boolean fluffyTail();
    public abstract boolean longEars();
    public abstract boolean thinMane();
    public abstract Species getSpecies();

    public boolean canEquipChest() {
        return true;
    }

    @Override
    public Random getRand() {
        return super.getRNG();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
        if (HorseConfig.COMMON.spookyHorses.get()) {
            this.goalSelector.addGoal(1, new SpookGoal(this, MonsterEntity.class, 8.0F, 1.5, 1.5));
        }
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorseEntity.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomWalkGroundTie(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.initExtraAI();
    }

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(HORSE_VARIANT, 0);
        this.dataManager.register(HORSE_VARIANT2, 0);
        this.dataManager.register(HORSE_VARIANT3, 0);
        this.dataManager.register(HORSE_VARIANT4, 0);
        this.dataManager.register(HORSE_VARIANT5, 0);
        this.dataManager.register(HORSE_VARIANT6, 0);
        this.dataManager.register(HORSE_SPEED, 0);
        this.dataManager.register(HORSE_HEALTH, 0);
        this.dataManager.register(HORSE_MHC1, 0);
        this.dataManager.register(HORSE_MHC2, 0);
        this.dataManager.register(HORSE_IMMUNE, 0);
        this.dataManager.register(HORSE_JUMP, 0);
        this.dataManager.register(HORSE_RANDOM, 0);
        this.dataManager.register(DISPLAY_AGE, 0);
        this.dataManager.register(GENDER, false);
        this.dataManager.register(IS_CASTRATED, false);
        this.dataManager.register(PREGNANT_SINCE, -1);
        this.dataManager.register(MOTHER_SIZE, 1f);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        // Mark what version the data was written in
        this.getPersistentData().putInt("HorseGeneticsVersion", HORSE_GENETICS_VERSION);
        compound.putInt("Variant", this.getChromosome("0"));
        compound.putInt("Variant2", this.getChromosome("1"));
        compound.putInt("Variant3", this.getChromosome("2"));
        compound.putInt("Variant4", this.getChromosome("3"));
        compound.putInt("Variant5", this.getChromosome("4"));
        compound.putInt("Variant6", this.getChromosome("5"));
        compound.putInt("SpeedGenes", this.getChromosome("speed"));
        compound.putInt("JumpGenes", this.getChromosome("jump"));
        compound.putInt("HealthGenes", this.getChromosome("health"));
        compound.putInt("MHC1", this.getChromosome("mhc1"));
        compound.putInt("MHC2", this.getChromosome("mhc2"));
        compound.putInt("Immune", this.getChromosome("immune"));
        compound.putInt("Random", this.getChromosome("random"));
        compound.putInt("true_age", trueAge);
        compound.putBoolean("gender", this.isMale());
        compound.putBoolean("is_castrated", this.isCastrated());
        compound.putInt("pregnant_since", this.getPregnancyStart());
        if (this.unbornChildren != null) {
            ListNBT unbornChildrenTag = new ListNBT();
            for (AbstractHorseGenetic child : this.unbornChildren) {
                CompoundNBT childNBT = new CompoundNBT();
                childNBT.putString("species", child.getSpecies().toString());
                childNBT.putString("genes", child.getGenes().genesToString());
                unbornChildrenTag.add(childNBT);
            }
            compound.put("unborn_children", unbornChildrenTag);
        }   
        compound.putFloat("mother_size", this.getMotherSize());
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setChromosome("0", compound.getInt("Variant"));
        this.setChromosome("1", compound.getInt("Variant2"));
        this.setChromosome("2", compound.getInt("Variant3"));
        this.setChromosome("3", compound.getInt("Variant4"));
        if (compound.contains("Variant5")) {
            this.setChromosome("4", compound.getInt("Variant5"));
        }
        else if (!this.getPersistentData().contains("HorseGeneticsVersion")) {
            this.getPersistentData().putInt("HorseGeneticsVersion", HORSE_GENETICS_VERSION);
            this.getGenes().datafixAddingFourthChromosome();
        }
        this.setChromosome("5", compound.getInt("Variant6"));
        this.setChromosome("speed", compound.getInt("SpeedGenes"));
        this.setChromosome("jump", compound.getInt("JumpGenes"));
        this.setChromosome("health", compound.getInt("HealthGenes"));
        if (compound.contains("MHC1")) {
            this.setChromosome("mhc1", compound.getInt("MHC1"));
            this.setChromosome("mhc2", compound.getInt("MHC2"));
        }
        else {
            this.getGenes().setNamedGene("leopard", 0);
            this.setChromosome("mhc1", this.rand.nextInt());
            this.setChromosome("mhc2", this.rand.nextInt());
        }
        if (compound.contains("Immune")) {
            this.setChromosome("immune", compound.getInt("Immune"));
        }
        else {
            for (int i = 0; i < 2; ++i) {
                if (this.getGenes().getAllele("dun", i) == HorseAlleles.DUN_OTHER) {
                    this.getGenes().setAllele("dun", i, HorseAlleles.DUN);
                }
                if (this.getGenes().getAllele("dun", i) == HorseAlleles.NONDUN1) {
                    this.getGenes().setAllele("dun", i, HorseAlleles.NONDUN2);
                }
            }
            this.getGenes().setNamedGene("gray_suppression", 0);
            this.setChromosome("immune", this.rand.nextInt());
        }
        this.setChromosome("random", compound.getInt("Random"));
        this.trueAge = compound.getInt("true_age");
        if (compound.contains("gender")) {
            this.setMale(compound.getBoolean("gender"));
        }
        else {
            this.setMale(rand.nextBoolean());
        }
        this.setCastrated(compound.getBoolean("is_castrated"));
        int pregnantSince = -1;
        if (compound.contains("pregnant_since")) {
            pregnantSince = compound.getInt("pregnant_since");
        }
        this.dataManager.set(PREGNANT_SINCE, pregnantSince);
        if (compound.contains("unborn_children")) {
            INBT nbt = compound.get("unborn_children");
            if (nbt instanceof ListNBT) {
                ListNBT childListTag = (ListNBT)nbt;
                for (int i = 0; i < childListTag.size(); ++i) {
                    INBT cnbt = childListTag.get(i);
                    if (!(cnbt instanceof CompoundNBT)) {
                        continue;
                    }
                    CompoundNBT childNBT = (CompoundNBT)cnbt;
                    Species species = Species.valueOf(childNBT.getString("species"));
                    AbstractHorseGenetic child = null;
                    switch(species) {
                        case HORSE:
                            child = ModEntities.HORSE_GENETIC.create(this.world);
                            break;
                        case DONKEY:
                            child = ModEntities.DONKEY_GENETIC.create(this.world);
                            break;
                        case MULE:
                        case HINNY:
                            child = ModEntities.MULE_GENETIC.create(this.world);
                            ((MuleGeneticEntity)child).setSpecies(species);
                            break;
                    }
                    if (child != null) {
                        HorseGenome genome = new HorseGenome(child.getSpecies(), child);
                        genome.genesFromString(childNBT.getString("genes"));
                        this.unbornChildren.add(child);
                    }
                }
            }
        }
        float motherSize = 1f;
        if (compound.contains("mother_size")) {
            motherSize = compound.getFloat("mother_size");
        }
        setMotherSize(motherSize);

        // updateHorseSlots
        this.func_230275_fc_();

        if (this instanceof HorseGeneticEntity) {
            int spawndata = compound.getInt("VillageSpawn");
            if (spawndata != 0) {
                this.initFromVillageSpawn();
            }
        }
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
                this.dataManager.set(HORSE_VARIANT, variant);
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
            case "3":
                this.dataManager.set(HORSE_VARIANT4, variant);
                this.getGenes().resetTexture();
                return;
            case "4":
                this.dataManager.set(HORSE_VARIANT5, variant);
                this.getGenes().resetTexture();
                return;
            case "5":
                this.dataManager.set(HORSE_VARIANT6, variant);
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
            case "mhc1":
                this.dataManager.set(HORSE_MHC1, variant);
                this.useGeneticAttributes();
                return;
            case "mhc2":
                this.dataManager.set(HORSE_MHC2, variant);
                this.useGeneticAttributes();
                return;
            case "immune":
                this.dataManager.set(HORSE_IMMUNE, variant);
                this.useGeneticAttributes();
                return;
            case "random":
                this.dataManager.set(HORSE_RANDOM, Integer.valueOf(variant));
                return;
            default:
                HorseColors.logger.error("Unrecognized horse data for setting: "
                                 + name + "\n");
        }
    }

    public int getChromosome(String name)
    {
        switch(name) {
            case "0":
                return ((Integer)this.dataManager.get(HORSE_VARIANT)).intValue();
            case "1":
                return ((Integer)this.dataManager.get(HORSE_VARIANT2)).intValue();
            case "2":
                return ((Integer)this.dataManager.get(HORSE_VARIANT3)).intValue();
            case "3":
                return ((Integer)this.dataManager.get(HORSE_VARIANT4)).intValue();
            case "4":
                return ((Integer)this.dataManager.get(HORSE_VARIANT5)).intValue();
            case "5":
                return ((Integer)this.dataManager.get(HORSE_VARIANT6)).intValue();
            case "speed":
                return ((Integer)this.dataManager.get(HORSE_SPEED)).intValue();
            case "jump":
                return ((Integer)this.dataManager.get(HORSE_JUMP)).intValue();
            case "health":
                return ((Integer)this.dataManager.get(HORSE_HEALTH)).intValue();
            case "mhc1":
                return ((Integer)this.dataManager.get(HORSE_MHC1)).intValue();
            case "mhc2":
                return ((Integer)this.dataManager.get(HORSE_MHC2)).intValue();
            case "immune":
                return ((Integer)this.dataManager.get(HORSE_IMMUNE)).intValue();
            case "random":
                return ((Integer)this.dataManager.get(HORSE_RANDOM)).intValue();
            default:
                HorseColors.logger.error("Unrecognized horse data for getting: " 
                                + name + "\n");
                return 0;
        }
        
    }

    public void setMotherSize(float size) {
        this.dataManager.set(MOTHER_SIZE, size);
    }

    public float getMotherSize() {
        return ((Float)this.dataManager.get(MOTHER_SIZE)).floatValue();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HORSE_VARIANT5.equals(key)
            || GENDER.equals(key)
            || MOTHER_SIZE.equals(key)) {
            this.recalculateSize();
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public void recalculateSize() {
        super.recalculateSize();
        // Remove this if Forge fixes the eye height issue
        float eyeHeight = getStandingEyeHeight(getPose(), getSize(getPose()));
        ObfuscationReflectionHelper.setPrivateValue(Entity.class, this, eyeHeight, "field_213326_aJ");
    }

    @Override
    public boolean isMale() {
        return ((Boolean)this.dataManager.get(GENDER)).booleanValue();
    }

    @Override
    public void setMale(boolean gender) {
        if (gender) {
            // Prepare to become male
            this.unbornChildren = new ArrayList<>();
            this.dataManager.set(PREGNANT_SINCE, -1);
        }
        else {
            // Prepare to become female
            this.setCastrated(false);
        }
        this.dataManager.set(GENDER, gender);
    }

    public boolean isCastrated() {
        return ((Boolean)this.dataManager.get(IS_CASTRATED)).booleanValue();
    }

    public void setCastrated(boolean isCastrated) {
        this.dataManager.set(IS_CASTRATED, isCastrated);
    }

    public boolean isPregnant() {
        return this.getPregnancyStart() >= 0;
    }

    public int getPregnancyStart() {
        return this.dataManager.get(PREGNANT_SINCE);
    }

    public float getPregnancyProgress() {
        int passed = getDisplayAge() - getPregnancyStart();
        int total = HorseConfig.getHorsePregnancyLength();
        return (float)passed / (float)total;
    }

    public int getRebreedTicks() {
        return HorseConfig.getHorseRebreedTicks(this.isMale());
    }

    public int getBirthAge() {
        return HorseConfig.getHorseBirthAge();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        // Don't stop and rear in response to suffocation or cactus damage
        if (damageSourceIn != DamageSource.IN_WALL && damageSourceIn != DamageSource.CACTUS) {
            // Chance to rear up
            super.getHurtSound(damageSourceIn);
        }
        return null;
    }

    /**
     * Set whether this creature is a child.
     */
    @Override
    public void setChild(boolean isChild) {
        this.setGrowingAge(isChild ? this.getBirthAge() : 0);
    }

    private boolean itemInteract(PlayerEntity player, ItemStack itemstack, Hand hand) {
        // Enter genetic test results
        if (itemstack.getItem() == Items.BOOK
                && (HorseConfig.GENETICS.bookShowsGenes.get()
                    || HorseConfig.GENETICS.bookShowsTraits.get())
                && (this.isTame() || player.abilities.isCreativeMode)) {
            ItemStack book = new ItemStack(ModItems.geneBookItem);
            if (book.getTag() == null) {
                book.setTag(new CompoundNBT());
            }
            book.getTag().putString("species", this.getSpecies().name());
            book.getTag().putString("genes", this.getGenes().genesToString());
            if (this.hasCustomName()) {
                book.setDisplayName(this.getCustomName());
            }
            if (!player.addItemStackToInventory(book)) {
                this.entityDropItem(book);
            }
            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            return true;
        }
        // Only allow taming with an empty hand
        if (!this.isTame()) {
            this.makeMad();
            return true;
        }
        // If tame, equip chest
        if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem()) {
            this.setChested(true);
            this.playChestEquipSound();
            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }

            this.initHorseChest();
            return true;
        }
        // If tame, equip saddle
        if (!this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE) {
             if (HorseConfig.COMMON.autoEquipSaddle.get()) {
                if (!this.world.isRemote) {
                    ItemStack saddle = itemstack.split(1);
                    this.horseChest.setInventorySlotContents(0, saddle);
                }
            }
            else {
                this.openGUI(player);
            }
            return true;
        }
        // If tame, equip armor
        // func_230276_fq_ = wearsArmor
        if (this.isArmor(itemstack) && this.func_230276_fq_()) {
             if (HorseConfig.COMMON.autoEquipSaddle.get() && this.horseChest.getStackInSlot(1).isEmpty()) {
                if (!this.world.isRemote) {
                    ItemStack armor = itemstack.split(1);
                    this.horseChest.setInventorySlotContents(1, armor);
                }
            }
            else {
                this.openGUI(player);
            }
            return true;
        }
        // Nothing left
        return false;
    }

    @Override
    // Before 1.16 this was public boolean processInteract(PlayerEntity player, Hand hand)
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!this.isChild()) {
            if (this.isTame() && player.isSecondaryUseActive()) {
                this.openGUI(player);
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }

            if (this.isBeingRidden()) {
                return super.func_230254_b_(player, hand);
            }
        }

        if (!itemstack.isEmpty()) {
            // Try to eat it
            if (this.isBreedingItem(itemstack)) {
                // Eat the item
                return this.func_241395_b_(player, itemstack);
            }
            // See if the item interacts
            ActionResultType actionresulttype = itemstack.interactWithEntity(player, this, hand);
            if (actionresulttype.isSuccessOrConsume()) {
                return actionresulttype;
            }
            // Try other interactions
            if (itemInteract(player, itemstack, hand)) {
                return ActionResultType.func_233537_a_(this.world.isRemote);
            }
        }

        if (this.isChild()) {
            return super.func_230254_b_(player, hand);
        }
        // else
        this.mountTo(player);
        return ActionResultType.func_233537_a_(this.world.isRemote);
    }

    protected void useGeneticAttributes()
    {
        if (HorseConfig.GENETICS.useGeneticStats.get())
        {
            HorseGenome genes = this.getGenes();
            float maxHealth = this.getGenes().getHealth();
            // Vanilla horse speed ranges from 0.1125 to 0.3375, as does ours
            float speedStat = genes.getStatValue("speed1")
                                + genes.getStatValue("speed2")
                                + genes.getStatValue("speed3")
                                + genes.getStatValue("athletics1") / 2f
                                + genes.getStatValue("athletics2") / 2f;
            double movementSpeed = 0.1125D + speedStat * (0.225D / 32.0D);
            // Vanilla horse jump strength ranges from 0.4 to 1.0, as does ours
            float jumpStat = genes.getStatValue("jump1")
                                + genes.getStatValue("jump2")
                                + genes.getStatValue("jump3")
                                + genes.getStatValue("athletics1") / 2f
                                + genes.getStatValue("athletics2") / 2f;
            double jumpStrength = 0.4D + jumpStat * (0.6D / 32.0D);

            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            this.getAttribute(Attributes.HORSE_JUMP_STRENGTH).setBaseValue(jumpStrength);
        }
        else {
            float maxHealth = this.getModifiedMaxHealth() + this.getGenes().getBaseHealth();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        }
    }

    @Override
    // In 1.16.2, this code was moved from BreedGoal's spawnBaby() to AnimalEntity
    public void func_234177_a_(ServerWorld world, AnimalEntity mate) {
        // If vanilla mate, handle the vanilla way
        if (!(mate instanceof IGeneticEntity)) {
            super.func_234177_a_(world, mate);
            return;
        }
        IGeneticEntity geneticMate = (IGeneticEntity)mate;

        // func_241840_a = createChild
        AgeableEntity ageableentity = this.func_241840_a(world, mate);
        // If ageableentity is null, leave this and the mate in love mode to try again
        // Note posting an event with a null child could cause crashes with other
        // mods that do not check their input carefully, so we don't do that
        if (ageableentity == null) {
            return;
        }
        final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, mate, ageableentity);
        final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
        ageableentity = event.getChild();
        if (cancelled) {
            //Reset the "inLove" state for the animals
            this.setGrowingAge(this.getRebreedTicks());
            mate.setGrowingAge(geneticMate.getRebreedTicks());
            this.resetInLove();
            mate.resetInLove();
            return;
        }
        // Don't spawn a null entity
        if (ageableentity == null) {
            return;
        }

        if (HorseConfig.isPregnancyEnabled()) {
            if (this.setPregnantWith(ageableentity, mate)) {
                ageableentity = null;
                // Spawn heart particles
                this.world.setEntityState(this, (byte)18);
            }
        }
        ServerPlayerEntity serverplayerentity = this.getLoveCause();
        if (serverplayerentity == null && mate.getLoveCause() != null) {
            serverplayerentity = mate.getLoveCause();
        }

        if (serverplayerentity != null) {
            serverplayerentity.addStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this, mate, ageableentity);
        }

        this.setGrowingAge(this.getRebreedTicks());
        mate.setGrowingAge(geneticMate.getRebreedTicks());
        this.resetInLove();
        mate.resetInLove();
        // Spawn if pregnancy is not enabled
        if (ageableentity != null) {
            spawnChild(ageableentity, world);
        }
        // Spawn XP orbs regardless of pregnancy
        if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            world.addEntity(new ExperienceOrbEntity(world, this.getPosX(), this.getPosY(), this.getPosZ(), this.getRNG().nextInt(7) + 1));
        }
    }

    public void spawnChild(AgeableEntity child, ServerWorld world) {
        child.setChild(true);
        child.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), 0.0F, 0.0F);
        // world.addEntity(child);
        world.func_242417_l(child);
        // Spawn heart particles
        world.setEntityState(this, (byte)18);
    }

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    abstract AbstractHorseEntity getChild(ServerWorld world, AgeableEntity otherparent);

    public boolean isOppositeGender(AbstractHorseGenetic other) {
        if (!HorseConfig.isGenderEnabled()) {
            return true;
        }
        if (this.isCastrated() || other.isCastrated()) {
            return false;
        }
        return this.isMale() != other.isMale();
    }

    @Override
    // func_241840_a = createChild
    public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity ageable)
    {
        if (!(ageable instanceof AnimalEntity)) {
            return null;
        }
        AnimalEntity otherAnimal = (AnimalEntity)ageable;
        // Have the female create the child if possible
        if (this.isMale() 
                && ageable instanceof AbstractHorseGenetic
                && !((AbstractHorseGenetic)ageable).isMale()) {
            return ageable.func_241840_a(world, this);
        }
        AbstractHorseEntity child = this.getChild(world, ageable);
        if (child != null) {
            this.setOffspringAttributes(ageable, child);
        }
        if (child instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic foal = (AbstractHorseGenetic)child;
            if (ageable instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic other = (AbstractHorseGenetic)ageable;
                foal.getGenes().inheritGenes(this.getGenes(), other.getGenes());
            }
            // Dominant white is homozygous lethal early in pregnancy. No child
            // is born.
            if (foal.getGenes().isEmbryonicLethal())
            {
                // Exit love mode
                this.resetInLove();
                otherAnimal.resetInLove();
                // Spawn smoke particles
                this.world.setEntityState(this, (byte)6);
                return null;
            }
            foal.setMotherSize(this.getGenes().getGeneticScale());
            foal.setMale(rand.nextBoolean());
            foal.useGeneticAttributes();
            foal.setGrowingAge(HorseConfig.GROWTH.getMinAge());
        }
        return child;
    }

    @Override
    public boolean setPregnantWith(AgeableEntity child, AgeableEntity otherParent) {
        if (otherParent instanceof IGeneticEntity) {
            IGeneticEntity otherGenetic = (IGeneticEntity)otherParent;
            if (this.isMale() == otherGenetic.isMale()) {
                return false;
            }
            else if (this.isMale() && !otherGenetic.isMale()) {
                return otherGenetic.setPregnantWith(child, this);
            }
        }
        if (this.isMale()) {
            return false;
        }

        if (child instanceof AbstractHorseGenetic) {
            unbornChildren.add((AbstractHorseGenetic)child);
            if (!this.world.isRemote) {
                // Can't be a child
                this.trueAge = Math.max(0, this.trueAge);
                this.dataManager.set(PREGNANT_SINCE, this.trueAge);
            }
            return true;
        }
        return false;
    }

    public boolean shouldRecordAge() {
        return this.getGenes().clientNeedsAge() || this.isPregnant();
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick()
    {
        super.tick();
        if (this.world.isRemote && this.dataManager.isDirty()) {
            this.dataManager.setClean();
            this.getGenes().resetTexture();
        }

        // Keep track of age
        if (!this.world.isRemote && this.shouldRecordAge()) {
            // For children, align with growing age in case they have been fed
            if (this.growingAge < 0) {
                this.trueAge = this.growingAge;
            }
            else {
                this.trueAge = Math.max(0, this.trueAge + 1);
            }
            // Allow imprecision
            final int c = 400;
            if (this.trueAge / c != this.getDisplayAge() / c
                    || (this.trueAge < 0 != this.getDisplayAge() < 0)) {
                this.setDisplayAge(this.trueAge);
            }
        }

        // Pregnancy
        if (!this.world.isRemote && this.isPregnant()) {
            // Check pregnancy
            if (this.unbornChildren == null
                    || this.unbornChildren.size() == 0) {
                this.dataManager.set(PREGNANT_SINCE, -1);
            }
            // Handle birth
            int totalLength = HorseConfig.getHorsePregnancyLength();
            int currentLength = this.trueAge - this.getPregnancyStart();
            if (currentLength >= totalLength) {
                for (AbstractHorseGenetic child : unbornChildren) {
                    if (this.world instanceof ServerWorld) {
                        this.spawnChild(child, (ServerWorld)this.world);
                    }
                }
                this.unbornChildren = new ArrayList<>();
                this.dataManager.set(PREGNANT_SINCE, -1);
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

    public void livingTick() {
        if (this.unbornChildren != null && this.unbornChildren.size() > 0
                && this.getPregnancyStart() < 0) {
            this.dataManager.set(PREGNANT_SINCE, 0);
        }

        if (this.getGenes().isHomozygous("leopard", HorseAlleles.LEOPARD) && !this.world.isRemote()) {
            ModifiableAttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            ModifiableAttributeInstance jumpAttribute = this.getAttribute(Attributes.HORSE_JUMP_STRENGTH);
            float brightness = this.getBrightness();
            if (brightness > 0.5f) {
                //setSprinting(true);
                if (speedAttribute.getModifier(CSNB_SPEED_UUID) != null) {
                    speedAttribute.removeModifier(CSNB_SPEED_MODIFIER);
                }
                if (jumpAttribute.getModifier(CSNB_JUMP_UUID) != null) {
                    jumpAttribute.removeModifier(CSNB_JUMP_MODIFIER);
                }
            }
            else {
                //setSprinting(false);
                if (speedAttribute.getModifier(CSNB_SPEED_UUID) == null) {
                    speedAttribute.applyNonPersistentModifier(CSNB_SPEED_MODIFIER);
                }
                if (jumpAttribute.getModifier(CSNB_JUMP_UUID) == null) {
                    jumpAttribute.applyNonPersistentModifier(CSNB_JUMP_MODIFIER);
                }
            }
        }

        super.livingTick();
    }

    // Returns the Y offset from the entity's position for any entity riding this one.
    @Override
    public double getMountedYOffset() {
        double coef = 0.833;
        // Compensate for saddle
        if (this.isHorseSaddled()) {
            coef += 0.04;
        }
        return (double)this.getHeight() * coef;
    }

    @Override
    // Overriden so passenger position white rearing depends on the horse's size
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (passenger instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)passenger;
            this.renderYawOffset = mobentity.renderYawOffset;
        }

        double yOffset = this.getMountedYOffset() + passenger.getYOffset();
        if (passenger instanceof PlayerEntity) {
            yOffset += PLAYER_OFFSET;
        }
        float prevRearingAmount = this.getRearingAmount(0F);
        if (prevRearingAmount > 0.0F) {
            float facingX = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
            float facingZ = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
            // A rearing amount of 1 corresponds to 45 degrees up
            float rearAngle = prevRearingAmount * (float)Math.PI / 4F;
            float rearXZ = (1F - MathHelper.cos(rearAngle)) * this.getWidth();
            float rearY = MathHelper.sin(rearAngle) * this.getWidth() / 2F;
            passenger.setPosition(this.getPosX() + (double)(rearXZ * facingX), this.getPosY() + yOffset + (double)rearY, this.getPosZ() - (double)(rearXZ * facingZ));
            if (passenger instanceof LivingEntity) {
                ((LivingEntity)passenger).renderYawOffset = this.renderYawOffset;
            }
        }
        else {
            passenger.setPosition(this.getPosX(), this.getPosY() + yOffset, this.getPosZ());
        }
    }

    @Override
    protected ITextComponent getProfessionName() {
        String species = this.getSpecies().toString().toLowerCase();
        String s = "entity." + HorseColors.MODID + "." + species + ".";
        if (this.isChild()) {
            // Foal
            if (!HorseConfig.BREEDING.enableGenders.get()) {
                return new TranslationTextComponent(s + "foal");
            }
            // Colt
            if (this.isMale()) {
                return new TranslationTextComponent(s + "colt");
            }
            // Filly
            return new TranslationTextComponent(s + "filly");
        }

        // Horse
        if (!HorseConfig.BREEDING.enableGenders.get()) {
            return super.getProfessionName();
        }
        // Stallion
        if (this.isMale()) {
            return new TranslationTextComponent(s + "male");
        }
        // Mare
        return new TranslationTextComponent(s + "female");
    }

    @Override
    // This is needed so when the mutation chance is high, mules bred
    // with spawn eggs do not produce all splashed white foals.
    public Breed getDefaultBreed() {
        return BaseEquine.breed;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.randomize();
        return spawnDataIn;
    }

    private void randomize() {
        this.getGenes().randomize(getDefaultBreed());
        // Choose a random age
        this.trueAge = this.rand.nextInt(HorseConfig.GROWTH.getMaxAge());
        // This preserves the ratio of child/adult
        if (this.rand.nextInt(5) == 0) {
            // Foals pick a random age within the younger half
            this.trueAge = this.getBirthAge() + this.rand.nextInt(-this.getBirthAge() / 2);
        }
        this.setMale(rand.nextBoolean());
        // Don't set the growing age to a positive value, that would be bad
        this.setGrowingAge(Math.min(0, this.trueAge));
        this.useGeneticAttributes();
    }

    public void initFromVillageSpawn() {
        this.randomize();
        // All village horses are easier to tame
        this.increaseTemper(this.getMaxTemper() / 2);
        if (!this.isChild() && rand.nextInt(16) == 0) {
            // Tame and saddle
            this.setHorseTamed(true);
            ItemStack saddle = new ItemStack(Items.SADDLE);
            this.horseChest.setInventorySlotContents(0, saddle);
        }
    }

    public float fractionGrown() {
        if (this.isChild()) {
            if (HorseConfig.GROWTH.growGradually.get()) {
                int minAge = HorseConfig.GROWTH.getMinAge();
                int age = Math.min(0, this.getDisplayAge());
                // 0 can't be accurate so assume it hasn't been set yet
                if (this.getDisplayAge() == 0) {
                    age = minAge;
                }
                float fractionGrown = (minAge - age) / (float)minAge;
                return Math.max(0, fractionGrown);
            }
            return 0;
        }
        return 1;
    }

    // Total size change based on age that does not change proportions
    public float getProportionalAgeScale() {
        // TODO: use size genes once they exist and once I've found how to make
        // players sit at the right height for different sizes
        float ageScale = 0.5f + 0.5f * fractionGrown();
        return ageScale / getGangliness();
    }

    // The horse model uses this number to decide how foal-shaped to make the
    // horse. 0.5 is the most foal-shaped and 1 is the most adult-shaped.
    public float getGangliness() {
        return 0.5f + 0.5f * fractionGrown() * fractionGrown();
    }

    @Override
    public float getRenderScale() {
        return this.getGenes().getGeneticScale() * super.getRenderScale();
    }
}
