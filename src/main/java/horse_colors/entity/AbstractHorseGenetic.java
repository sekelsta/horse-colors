package sekelsta.horse_colors.entity;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ai.*;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.item.ModItems;
import sekelsta.horse_colors.item.GeneBookItem;
import sekelsta.horse_colors.genetics.*;
import sekelsta.horse_colors.util.Util;

public abstract class AbstractHorseGenetic extends AbstractChestedHorseEntity implements IGeneticEntity {

    protected HorseGenome genes;
    protected static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT4 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
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
    protected int trueAge;

    protected static final UUID CSNB_SPEED_UUID = UUID.fromString("84ca527a-5c70-4336-a737-ae3f6d40ef45");
    protected static final UUID CSNB_JUMP_UUID = UUID.fromString("72323326-888b-4e46-bf52-f669600642f7");
    protected static final AttributeModifier CSNB_SPEED_MODIFIER = (new AttributeModifier(CSNB_SPEED_UUID, "CSNB speed penalty", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL)).setSaved(false);
    protected static final AttributeModifier CSNB_JUMP_MODIFIER = (new AttributeModifier(CSNB_JUMP_UUID, "CSNB jump penalty", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL)).setSaved(false);

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
        this.goalSelector.addGoal(2, new GenderedBreedGoal(this, 1.0D, AbstractHorseEntity.class));
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
        this.dataManager.register(HORSE_VARIANT, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT2, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT3, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT4, Integer.valueOf(0));
        this.dataManager.register(HORSE_SPEED, Integer.valueOf(0));
        this.dataManager.register(HORSE_HEALTH, Integer.valueOf(0));
        this.dataManager.register(HORSE_MHC1, Integer.valueOf(0));
        this.dataManager.register(HORSE_MHC2, Integer.valueOf(0));
        this.dataManager.register(HORSE_IMMUNE, Integer.valueOf(0));
        this.dataManager.register(HORSE_JUMP, Integer.valueOf(0));
        this.dataManager.register(HORSE_RANDOM, Integer.valueOf(0));
        this.dataManager.register(DISPLAY_AGE, Integer.valueOf(0));
        this.dataManager.register(GENDER, false);
        this.dataManager.register(IS_CASTRATED, false);
        this.dataManager.register(PREGNANT_SINCE, -1);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getChromosome("0"));
        compound.putInt("Variant2", this.getChromosome("1"));
        compound.putInt("Variant3", this.getChromosome("2"));
        compound.putInt("Variant4", this.getChromosome("3"));
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
    }

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setChromosome("0", compound.getInt("Variant"));
        this.setChromosome("1", compound.getInt("Variant2"));
        this.setChromosome("2", compound.getInt("Variant3"));
        this.setChromosome("3", compound.getInt("Variant4"));
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
                            child = ModEntities.MULE_GENETIC.create(this.world);
                            break;
                    }
                    if (child != null) {
                        HorseGenome genome = new HorseGenome(child);
                        genome.genesFromString(childNBT.getString("genes"));
                        this.unbornChildren.add(child);
                    }
                }
            }
        }

        this.updateHorseSlots();

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

    public int getRebreedTicks() {
        return HorseConfig.getHorseRebreedTicks(this.isMale());
    }

    public int getBirthAge() {
        return HorseConfig.getHorseBirthAge();
    }

    @Override
    protected void onChildSpawnFromEgg(PlayerEntity playerIn, AgeableEntity child) {
        if (child instanceof IGeneticEntity) {
            child.setGrowingAge(((IGeneticEntity)child).getBirthAge());
        }
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && itemstack.getItem() instanceof SpawnEggItem) {
            return super.processInteract(player, hand);
        }

        if (!this.isChild()) {
            if (this.isTame() && player.func_226563_dT_()) {
                this.openGUI(player);
                return true;
            }

            if (this.isBeingRidden()) {
                return super.processInteract(player, hand);
            }
        }

        if (itemstack.isEmpty()) {
            if (this.isChild()) {
                return super.processInteract(player, hand);
            }
            else {
                this.mountTo(player);
                return true;
            }
        }

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

        if (this.handleEating(player, itemstack)) {
            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            return true;
        }

        if (itemstack.interactWithEntity(player, this, hand)) {
            return true;
        }

        if (!this.isTame()) {
            this.makeMad();
            return true;
        }

        if (this.isChild()) {
            return false;
        }

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

        if (this.isArmor(itemstack) && this.wearsArmor()) {
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

        if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem()) {
            if (this.canEquipChest()) {
                this.setChested(true);
                this.playChestEquipSound();
                this.initHorseChest();
                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }
        }

        this.mountTo(player);
        return true;
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

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    abstract AbstractHorseEntity getChild(AgeableEntity otherparent);

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
    public AgeableEntity createChild(AgeableEntity ageable)
    {
        if (!(ageable instanceof AnimalEntity)) {
            return null;
        }
        AnimalEntity otherAnimal = (AnimalEntity)ageable;
        // Have the female create the child if possible
        if (this.isMale() 
                && ageable instanceof AbstractHorseGenetic
                && !((AbstractHorseGenetic)ageable).isMale()) {
            return ageable.createChild(this);
        }
        AbstractHorseEntity child = this.getChild(ageable);
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
        }

        // Align age with client
        if (!this.world.isRemote && (this.getGenes().clientNeedsAge())) {
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
                    GenderedBreedGoal.spawnChild(this, child, this.world);
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
        IAttributeInstance speedAttribute = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        IAttributeInstance jumpAttribute = this.getAttribute(JUMP_STRENGTH);
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
                speedAttribute.applyModifier(CSNB_SPEED_MODIFIER);
            }
            if (jumpAttribute.getModifier(CSNB_JUMP_UUID) == null) {
                jumpAttribute.applyModifier(CSNB_JUMP_MODIFIER);
            }
        }
      }

      super.livingTick();
   }

    public Map<String, List<Float>> getSpawnFrequencies() {
        return new HashMap<String, List<Float>>();
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

    private void randomize() {
        this.getGenes().randomize(getSpawnFrequencies());
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

    // Total size change that does not change proportions
    public float getProportionalScale() {
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
}
