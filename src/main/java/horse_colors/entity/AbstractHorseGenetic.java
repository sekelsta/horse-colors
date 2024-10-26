package sekelsta.horse_colors.entity;

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import sekelsta.horse_colors.breed.*;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ai.*;
import sekelsta.horse_colors.entity.genetics.*;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.item.ModItems;
import sekelsta.horse_colors.item.GeneBookItem;
import sekelsta.horse_colors.util.Util;

public abstract class AbstractHorseGenetic extends AbstractChestedHorse implements IGeneticEntity<Gene> {
    protected EquineGenome genes = new EquineGenome(this.getSpecies(), this);
    protected static final EntityDataAccessor<String> GENES = SynchedEntityData.<String>defineId(AbstractHorseGenetic.class, EntityDataSerializers.STRING);

    protected static final EntityDataAccessor<Integer> HORSE_RANDOM = SynchedEntityData.<Integer>defineId(AbstractHorseGenetic.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DISPLAY_AGE = SynchedEntityData.<Integer>defineId(AbstractHorseGenetic.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.<Boolean>defineId(AbstractHorseGenetic.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> PREGNANT_SINCE = SynchedEntityData.<Integer>defineId(AbstractHorseGenetic.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> MOTHER_SIZE = SynchedEntityData.<Float>defineId(AbstractHorseGenetic.class, EntityDataSerializers.FLOAT);
    protected int trueAge;

    protected static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    protected static final UUID CSNB_SPEED_UUID = UUID.fromString("84ca527a-5c70-4336-a737-ae3f6d40ef45");
    protected static final UUID CSNB_JUMP_UUID = UUID.fromString("72323326-888b-4e46-bf52-f669600642f7");
    protected static final AttributeModifier CSNB_SPEED_MODIFIER = new AttributeModifier(CSNB_SPEED_UUID, "CSNB speed penalty", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL);
    protected static final AttributeModifier CSNB_JUMP_MODIFIER = new AttributeModifier(CSNB_JUMP_UUID, "CSNB jump penalty", -0.6, AttributeModifier.Operation.MULTIPLY_TOTAL);

    protected List<AbstractHorseGenetic> unbornChildren = new ArrayList<>();

    // f_30514_ = standAnimO
    private Field rearingAmountField = ObfuscationReflectionHelper.findField(AbstractHorse.class, "f_30514_");

    public AbstractHorseGenetic(EntityType<? extends AbstractHorseGenetic> entityType, Level worldIn)
    {
        super(entityType, worldIn);
        this.setSeed(this.random.nextInt());
        this.setMale(this.random.nextBoolean());
        this.entityData.set(PREGNANT_SINCE, -1);
    }

    public EquineGenome getGenome() {
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
    public int getSeed() {
        return this.entityData.get(HORSE_RANDOM).intValue();
    }

    @Override
    public void setSeed(int seed) {
        this.entityData.set(HORSE_RANDOM, seed);
    }

    @Override
    public Random getRand() {
        return this.getRandom();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
        if (HorseConfig.COMMON.spookyHorses.get()) {
            this.goalSelector.addGoal(1, new SpookGoal(this, Monster.class, 8.0F, 1.5, 1.5));
        }
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, AbstractHorse.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomWalkGroundTie(this, 0.7D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(GENES, "");
        this.entityData.define(HORSE_RANDOM, 0);
        this.entityData.define(DISPLAY_AGE, 0);
        this.entityData.define(GENDER, false);
        this.entityData.define(PREGNANT_SINCE, -1);
        this.entityData.define(MOTHER_SIZE, 1f);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        writeGeneticData(compound);
        if (!this.inventory.getItem(1).isEmpty()) {
            compound.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
        }
    }

    private void writeGeneticData(CompoundTag compound) {
        compound.putString("Genes", this.getGenome().getBase64());
        compound.putInt("Random", this.getSeed());
        compound.putInt("true_age", this.trueAge);
        compound.putBoolean("gender", this.isMale());
        compound.putInt("pregnant_since", this.getPregnancyStart());
        if (this.unbornChildren != null) {
            ListTag unbornChildrenTag = new ListTag();
            for (AbstractHorseGenetic child : this.unbornChildren) {
                CompoundTag childNBT = new CompoundTag();
                childNBT.putString("species", child.getSpecies().toString());
                childNBT.putString("genes", child.getGenome().genesToString());
                childNBT.putFloat("mother_size", child.getMotherSize());
                unbornChildrenTag.add(childNBT);
            }
            compound.put("unborn_children", unbornChildrenTag);
        }   
        compound.putFloat("mother_size", this.getMotherSize());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        // Read the main part of the data
        readGeneticData(compound);
        // Ensure the true age matches the age
        if (this.trueAge < 0 != this.age < 0) {
            this.trueAge = this.age;
        }

        // Set any genes that were specified in a human-readable format
        readExtraGenes(compound);

        this.useGeneticAttributes();
        this.updateContainerEquipment();

        if (this instanceof HorseGeneticEntity) {
            int spawndata = compound.getInt("VillageSpawn");
            if (spawndata != 0) {
                this.initFromVillageSpawn();
            }
        }

        this.updatePersistentData();

        if (compound.contains("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.of(compound.getCompound("ArmorItem"));
            if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
                this.inventory.setItem(1, itemstack);
            }
        }
        this.updateContainerEquipment();
    }



    private void updatePersistentData() {
        // Tell Ride Along how much this horse weighs
        CompoundTag rideAlongTag = new CompoundTag();
        rideAlongTag.putDouble("WeightKg", this.getGenome().getGeneticWeightKg());
        this.getPersistentData().put("RideAlong", rideAlongTag);
    }


    // A helper function for reading the data
    private void readGeneticData(CompoundTag compound) {
        // Set genes if they exist
        if (compound.contains("Genes")) {
            String genes = compound.getString("Genes");
            if (genes.length() > 0 && genes.charAt(0) < 48) {
                // Read genes using the old 1:1 conversion of chars to nums
                this.setGeneData(compound.getString("Genes"));
            }
            else {
                this.getGenome().setFromBase64(compound.getString("Genes"));
            }
        }
        // Otherwise, use a breed for a base if given one
        else if (compound.contains("Breed")) {
            this.randomize(getBreed(compound.getString("Breed")));
        }
        else {
            randomize(getRandomBreed());
        }

        // Replace saddle reading functionality from AbstractHorseEntity with
        // one that accepts alternate saddles
        if (compound.contains("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.of(compound.getCompound("SaddleItem"));
            if (isSaddle(itemstack)) {
                this.inventory.setItem(0, itemstack);
            }
        }

        if (compound.contains("Random")) {
            this.setSeed(compound.getInt("Random"));
        }
        if (compound.contains("true_age")) {
            this.trueAge = compound.getInt("true_age");
        }
        if (compound.contains("gender")) {
            this.setMale(compound.getBoolean("gender"));
        }
        else {
            this.setMale(this.random.nextBoolean());
        }

        int pregnantSince = -1;
        if (compound.contains("pregnant_since")) {
            pregnantSince = compound.getInt("pregnant_since");
        }
        this.entityData.set(PREGNANT_SINCE, pregnantSince);
        if (compound.contains("unborn_children")) {
            Tag nbt = compound.get("unborn_children");
            if (nbt instanceof ListTag) {
                ListTag childListTag = (ListTag)nbt;
                for (int i = 0; i < childListTag.size(); ++i) {
                    Tag cnbt = childListTag.get(i);
                    if (!(cnbt instanceof CompoundTag)) {
                        continue;
                    }
                    CompoundTag childNBT = (CompoundTag)cnbt;
                    Species species = Species.valueOf(childNBT.getString("species"));
                    AbstractHorseGenetic child = null;
                    switch(species) {
                        case HORSE:
                            child = ModEntities.HORSE_GENETIC.get().create(this.level);
                            break;
                        case DONKEY:
                            child = ModEntities.DONKEY_GENETIC.get().create(this.level);
                            break;
                        case MULE:
                        case HINNY:
                            child = ModEntities.MULE_GENETIC.get().create(this.level);
                            ((MuleGeneticEntity)child).setSpecies(species);
                            break;
                    }
                    if (child != null) {
                        EquineGenome genome = new EquineGenome(child.getSpecies(), child);
                        genome.genesFromString(childNBT.getString("genes"));
                        if (childNBT.contains("mother_size")) {
                            child.setMotherSize(childNBT.getFloat("mother_size"));
                        }
                        else {
                            child.setMotherSize(this.getGenome().getAdultScale());
                        }
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
    }

    protected void readExtraGenes(CompoundTag compound) {
        boolean changed = false;
        for (Enum gene : this.getGenome().listGenes()) {
            if (compound.contains(gene.toString())) {
                int alleles[] = compound.getIntArray(gene.toString());
                List<Integer> allowedAlleles = getGenome().getAllowedAlleles(gene, getDefaultBreed());
                for (int i = 0; i < 2; ++i) {
                    if (allowedAlleles.contains(alleles[i])) {
                        getGenome().setAllele(gene, i, alleles[i]);
                    }
                }
                changed = true;
            }
        }
        if (changed) {
            getGenome().finalizeGenes();
        }
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

    public void copyAbstractHorse(AbstractHorse horse)
    {
        // Copy NBT data (initialize from horse's NBT)
        CompoundTag vanilla = horse.saveWithoutId(new CompoundTag());
        // Don't try to read Minecraft's variant as legacy gene data
        if (vanilla.contains("Variant")) {
            vanilla.remove("Variant");
        }
        this.load(vanilla);
        this.useGeneticAttributes();
    }

    public int getDisplayAge() {
        return this.entityData.get(DISPLAY_AGE);
    }

    public void setDisplayAge(int age) {
        this.entityData.set(DISPLAY_AGE, age);
    }

    @Override
    public int getTrueAge() {
        if (isBaby() && getDisplayAge() >= 0) {
            return getBirthAge();
        }
        return getDisplayAge();
    }

    public void setGeneData(String genes) {
        this.entityData.set(GENES, genes);
    }

    public String getGeneData() {
        return (String)this.entityData.get(GENES);
    }

    public void setMotherSize(float size) {
        this.entityData.set(MOTHER_SIZE, size);
    }

    public float getMotherSize() {
        return ((Float)this.entityData.get(MOTHER_SIZE)).floatValue();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (GENES.equals(key)) {
            this.getGenome().resetTexture();
            this.useGeneticAttributes();
            this.refreshDimensions();
        }
        else if (HORSE_RANDOM.equals(key)
            || GENDER.equals(key)
            || MOTHER_SIZE.equals(key)) {
            this.refreshDimensions();
        }
        else if (DISPLAY_AGE.equals(key)) {
            this.getGenome().resetTexture();
        }

        super.onSyncedDataUpdated(key);
    }

    @Override
    public boolean isMale() {
        return ((Boolean)this.entityData.get(GENDER)).booleanValue();
    }

    @Override
    public void setMale(boolean gender) {
        if (gender) {
            // Prepare to become male
            this.unbornChildren = new ArrayList<>();
            this.entityData.set(PREGNANT_SINCE, -1);
        }
        this.entityData.set(GENDER, gender);
    }

    public boolean isPregnant() {
        return this.getPregnancyStart() >= 0;
    }

    public int getPregnancyStart() {
        return this.entityData.get(PREGNANT_SINCE);
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

    // Since miniaure horses are too small to ride, they can't be tamed the usual way
    @Override
    public boolean isTamed() {
        return isTooSmallForPlayerToRide() || super.isTamed();
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
    public void setBaby(boolean isBaby) {
        this.setAge(isBaby ? this.getBirthAge() : 0);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    // This should err on the side of too exclusive, while canAddPassenger
    // should err towards more inclusive. Other mods can bypass this by simply
    // instructing the entity to mount the horse.
    private boolean canFitRider(Player rider) {
        return canAddPassenger(rider) 
            && !isTooSmallForPlayerToRide()
            && (this.getGenome().isLarge() || this.getPassengers().size() < 1);
    }

    @Override
    protected void doPlayerRide(Player player) {
        if (!canFitRider(player)) {
            return;
        }
        super.doPlayerRide(player);
    }

    public SimpleContainer getHorseChest() {
        return this.inventory;
    }

    protected boolean canTestGenetics() {
        return true;
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean isArmor(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem
                    && ((BlockItem)stack.getItem()).getBlock() instanceof WoolCarpetBlock) {
            return true;
        }
        if (stack.getItem() instanceof HorseArmorItem) {
            HorseArmorItem armor = (HorseArmorItem)(stack.getItem());
            return armor.getProtection() == 0;
        }
        return false;
    }

    private boolean itemInteract(Player player, ItemStack itemstack, InteractionHand hand) {
        // Enter genetic test results
        if (itemstack.getItem() == Items.BOOK
                && (HorseConfig.GENETICS.bookShowsGenes.get()
                    || HorseConfig.GENETICS.bookShowsTraits.get())
                && (this.isTamed() || player.getAbilities().instabuild)
                && this.canTestGenetics()) {
            ItemStack book = new ItemStack(ModItems.geneBookItem.get());
            if (book.getTag() == null) {
                book.setTag(new CompoundTag());
            }
            book.getTag().putString("species", this.getSpecies().name());
            book.getTag().putString("genes", this.getGenome().genesToString());
            book.getTag().putUUID("EntityUUID", this.getUUID());
            if (this.hasCustomName()) {
                book.setHoverName(this.getCustomName());
            }
            if (!player.addItem(book)) {
                this.spawnAtLocation(book);
            }
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return true;
        }
        // Unequip a chest
        if (this.hasChest() && itemstack.getItem() instanceof AxeItem) {
            this.dropEquipment();
            this.setChest(false);
            this.createInventory();
            return true;
        }

        // Only allow taming with an empty hand
        if (!this.isTamed()) {
            this.makeMad();
            return true;
        }
        // If tame, equip chest
        if (!this.hasChest() && itemstack.getItem() == Blocks.CHEST.asItem()
                && this.canEquipChest()) {
            this.setChest(true);
            this.playChestEquipsSound();
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            this.createInventory();
            return true;
        }
        // If tame, equip saddle
        if (!this.isSaddled() && isSaddle(itemstack) && this.isSaddleable()) {
            if (!this.level.isClientSide) {
                ItemStack saddle = itemstack.split(1);
                this.inventory.setItem(0, saddle);
            }
            return true;
        }
        // If tame, equip armor
        if (this.isArmor(itemstack)) {
             if (this.inventory.getItem(1).isEmpty()) {
                if (!this.level.isClientSide) {
                    ItemStack armor = itemstack.split(1);
                    this.inventory.setItem(1, armor);
                }
            }
            else {
                this.openInventory(player);
            }
            return true;
        }
        // Nothing left
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.isSecondaryUseActive()) {
                this.openInventory(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        // Only interact items with horses that aren't being ridden by another player
        if (!itemstack.isEmpty() && !this.isVehicle()) {
            // Try to eat it
            if (this.isFood(itemstack)) {
                // Eat the item
                return this.fedFood(player, itemstack);
            }
            // See if the item interacts with us
            InteractionResult actionresulttype = itemstack.interactLivingEntity(player, this, hand);
            if (actionresulttype.consumesAction()) {
                return actionresulttype;
            }
            // See if we interact with the item
            if (itemInteract(player, itemstack, hand)) {
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }

        if (!this.isBaby() && canFitRider(player)) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        // else
        return super.mobInteract(player, hand);
    }

    protected void useGeneticAttributes()
    {
        if (HorseConfig.GENETICS.useGeneticStats.get())
        {
            EquineGenome genes = this.getGenome();
            float maxHealth = this.getGenome().getHealth();
            float athletics = genes.sumGenes(Gene.class, "athletics", 0, 4) / 2f
                                + genes.sumGenes(Gene.class, "athletics", 4, 8) / 2f;
            // Vanilla horse speed ranges from 0.1125 to 0.3375, as does ours
            float speedStat = genes.sumGenes(Gene.class, "speed", 0, 4)
                                + genes.sumGenes(Gene.class, "speed", 4, 8)
                                + genes.sumGenes(Gene.class, "speed", 8, 12)
                                + athletics;
            double movementSpeed = 0.1125D + speedStat * (0.225D / 32.0D);
            // Vanilla horse jump strength ranges from 0.4 to 1.0, as does ours
            float jumpStat = genes.sumGenes(Gene.class, "jump", 0, 4)
                                + genes.sumGenes(Gene.class, "jump", 4, 8)
                                + genes.sumGenes(Gene.class, "jump", 8, 12)
                                + athletics;
            double jumpStrength = 0.4D + jumpStat * (0.6D / 32.0D);

            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(jumpStrength);
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel world, Animal mate) {
        // If vanilla mate, handle the vanilla way
        if (!(mate instanceof IGeneticEntity)) {
            super.spawnChildFromBreeding(world, mate);
            return;
        }
        // If two animals go for the same mate at the same time this function
        // can be called twice. This check makes sure it only works the first time.
        if (!(this.isInLove() && mate.isInLove())) {
            return;
        }

        IGeneticEntity geneticMate = (IGeneticEntity)mate;

        // Call this on the female's side, if possible
        if (this.isMale() && !geneticMate.isMale()) {
            mate.spawnChildFromBreeding(world, this);
            return;
        }

        // For use later in triggering the achievement
        ServerPlayer serverplayerentity = this.getLoveCause();
        if (serverplayerentity == null && mate.getLoveCause() != null) {
            serverplayerentity = mate.getLoveCause();
        }

        int numFoals = this.getRandomLitterSize();
        List<AgeableMob> foals = new ArrayList<>();
        for (int i = 0; i < numFoals; ++i) {

            AgeableMob ageableentity = this.getBreedOffspring(world, mate);
            // If ageableentity is null, leave this and the mate in love mode to try again
            // Note posting an event with a null child could cause crashes with other
            // mods that do not check their input carefully, so we don't do that
            if (ageableentity == null) {
                continue;
            }
            final BabyEntitySpawnEvent event = new BabyEntitySpawnEvent(this, mate, ageableentity);
            final boolean cancelled = MinecraftForge.EVENT_BUS.post(event);
            ageableentity = event.getChild();
            // Don't spawn if cancelled or a null entity
            if (cancelled || ageableentity == null) {
                continue;
            }

            foals.add(ageableentity);

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this, mate, ageableentity);
            }
        }
        // Reset love state
        this.setAge(this.getRebreedTicks());
        mate.setAge(geneticMate.getRebreedTicks());
        this.resetLove();
        mate.resetLove();

        
        if (foals.size() <= 0) {
            // Spawn smoke particles to indicate failure
            this.level.broadcastEntityEvent(this, (byte)6);
            // Only spawn XP and grant the achievement for successful births
            return;
        }
        // Make twins and triplets smaller as there is less space for them in the womb
        float multiplier = (float)Math.pow(1./foals.size(), 1./3.);
        for (AgeableMob foal : foals) {
            if (foal instanceof IGeneticEntity) {
                IGeneticEntity gFoal = (IGeneticEntity)foal;
                gFoal.setMotherSize(gFoal.getMotherSize() * multiplier);
            }

            // Set pregnant or spawn into world directly
            if (!HorseConfig.isPregnancyEnabled() || !setPregnantWith(foal, mate)) {
                spawnChild(foal, world);
            }
        }

        if (HorseConfig.isPregnancyEnabled()) {
            // Spawn heart particles
            this.level.broadcastEntityEvent(this, (byte)18);
        }

        // Spawn XP orbs
        if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            int xp = this.getRandom().nextInt(7) + 1;
            world.addFreshEntity(new ExperienceOrb(world, this.getX(), this.getY(), this.getZ(), xp));
        }
    }

    private void spawnChild(AgeableMob child, ServerLevel world) {
        child.setBaby(true);
        child.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
        world.addFreshEntity(child);
        // Spawn heart particles
        world.broadcastEntityEvent(this, (byte)18);
    }

    // Helper function for createChild that creates and spawns an entity of the 
    // correct species
    abstract AbstractHorse getChild(ServerLevel world, AgeableMob otherparent);

    // Returns the number of conceptions that survive pregnancy
    // This can return different numbers when called on the same animal at
    // different times.
    protected int getRandomLitterSize() {
        // Since this is Minecraft, skip twin births where one or both twins die,
        // and make twin births much more rare
        // If the frequency of double_ovulation is 0.2, the probability of triplets
        // works out to within an order of magnitude of the expected 1 in 300,000.
        double chance = 1 / 10000;
        if (getGenome().countAlleles(Gene.double_ovulation, 1) == 1) {
            chance = 1 / 5000;
        }
        else if (getGenome().isHomozygous(Gene.double_ovulation, 1)) {
            chance = 1 / 1000;
        }

        int litterSize = 1;
        if (getRandom().nextDouble() < chance) {
            litterSize += 1;
        }
        if (getRandom().nextDouble() < chance) {
            litterSize += 1;
        }
        return litterSize;
    }

    public boolean isOppositeGender(AbstractHorseGenetic other) {
        if (!HorseConfig.isGenderEnabled()) {
            return true;
        }
        return this.isMale() != other.isMale();
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob ageable)
    {
        if (!(ageable instanceof Animal)) {
            return null;
        }
        Animal otherAnimal = (Animal)ageable;
        // Have the female create the child if possible
        if (this.isMale() 
                && ageable instanceof AbstractHorseGenetic
                && !((AbstractHorseGenetic)ageable).isMale()) {
            return ageable.getBreedOffspring(world, this);
        }
        AbstractHorse child = this.getChild(world, ageable);
        if (child != null) {
            this.setOffspringAttributes(ageable, child);
        }
        if (child instanceof AbstractHorseGenetic) {
            AbstractHorseGenetic foal = (AbstractHorseGenetic)child;
            if (ageable instanceof AbstractHorseGenetic) {
                AbstractHorseGenetic other = (AbstractHorseGenetic)ageable;
                foal.getGenome().inheritGenes(this.getGenome(), other.getGenome());
                // No child is born for genotypes strongly suspected to be embryonic lethal, or re-roll for those
                // that we're not sure about
                while (foal.getGenome().isEmbryonicLethal() || foal.getGenome().isMaybeEmbryonicLethal())
                {
                    if (foal.getGenome().isEmbryonicLethal()) {
                        return null;
                    }
                    else if (foal.getGenome().isMaybeEmbryonicLethal()) {
                        foal.getGenome().inheritGenes(this.getGenome(), other.getGenome());
                    }
                }
            }
            foal.setMotherSize(this.getGenome().getAdultScale());
            foal.setMale(this.random.nextBoolean());
            foal.useGeneticAttributes();
            foal.setAge(HorseConfig.GROWTH.getMinAge());
        }
        return child;
    }

    @Override
    public boolean setPregnantWith(AgeableMob child, AgeableMob otherParent) {
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
            if (!this.level.isClientSide) {
                // Can't be a child
                this.trueAge = Math.max(0, this.trueAge);
                this.entityData.set(PREGNANT_SINCE, this.trueAge);
            }
            return true;
        }
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick()
    {
        super.tick();
        // Keep track of age
        if (!this.level.isClientSide) {
            // For children, align with growing age in case they have been fed
            if (this.age < 0) {
                this.trueAge = this.age;
            }
            else {
                this.trueAge = Math.max(0, Math.max(trueAge, trueAge + 1));
            }
            // Allow imprecision
            final int c = 400;
            if (this.trueAge / c != this.getDisplayAge() / c
                    || (this.trueAge < 0 != this.getDisplayAge() < 0)) {
                this.setDisplayAge(this.trueAge);
            }
        }

        // Pregnancy
        if (!this.level.isClientSide && this.isPregnant()) {
            // Check pregnancy
            if (this.unbornChildren == null
                    || this.unbornChildren.size() == 0) {
                this.entityData.set(PREGNANT_SINCE, -1);
            }
            // Handle birth
            int totalLength = HorseConfig.getHorsePregnancyLength();
            int currentLength = this.trueAge - this.getPregnancyStart();
            if (currentLength >= totalLength) {
                for (AbstractHorseGenetic child : unbornChildren) {
                    if (this.level instanceof ServerLevel) {
                        this.spawnChild(child, (ServerLevel)this.level);
                    }
                }
                this.unbornChildren = new ArrayList<>();
                this.entityData.set(PREGNANT_SINCE, -1);
            }
        }

        // Overo lethal white syndrome
        if (this.getGenome().isLethalWhite()
            && this.tickCount > 80)
        {
            if (!this.hasEffect(MobEffects.WITHER))
            {
                this.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 3));
            }
        }

        ItemStack stack = this.inventory.getItem(1);
        if (isArmor(stack)) stack.onHorseArmorTick(this.level, this);
    }

    public void aiStep() {
        if (this.unbornChildren != null && this.unbornChildren.size() > 0
                && this.getPregnancyStart() < 0) {
            this.entityData.set(PREGNANT_SINCE, 0);
        }

        if (this.getGenome().isHomozygous(Gene.leopard, HorseAlleles.LEOPARD) && !this.level.isClientSide()) {
            AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeInstance jumpAttribute = this.getAttribute(Attributes.JUMP_STRENGTH);
            float brightness = this.level.getMaxLocalRawBrightness(this.getOnPos());
            if (brightness > 0.5f) {
                if (speedAttribute.getModifier(CSNB_SPEED_UUID) != null) {
                    speedAttribute.removeModifier(CSNB_SPEED_MODIFIER);
                }
                if (jumpAttribute.getModifier(CSNB_JUMP_UUID) != null) {
                    jumpAttribute.removeModifier(CSNB_JUMP_MODIFIER);
                }
            }
            else {
                if (speedAttribute.getModifier(CSNB_SPEED_UUID) == null) {
                    speedAttribute.addTransientModifier(CSNB_SPEED_MODIFIER);
                }
                if (jumpAttribute.getModifier(CSNB_JUMP_UUID) == null) {
                    jumpAttribute.addTransientModifier(CSNB_JUMP_MODIFIER);
                }
            }
        }

        super.aiStep();
    }

    // Returns the Y offset from the entity's position for any entity riding this one.
    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight() * 0.833 - 0.295;
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof LivingEntity) {
                LivingEntity rider = (LivingEntity)entity;
                if (!(rider instanceof Animal) && (rider instanceof Player || !this.isLeashed())) {
                    return rider;
                }
            }
        }

        return null;
    }

    @Override
    // Overriden so passenger position while rearing depends on the horse's size,
    // also to support multiple passengers.
    public void positionRider(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        // Do not call super.positionRider. AbstractHorseEntity's implementation
        // sets this's yBodyRot to that of the passenger without
        // checking that the passenger can steer.
        // Setting yRot happens in AbstractHorseEntity.travel

        float xzOffset = -0.1f;
        if (this.getPassengers().size() > 1) {
            int i = this.getPassengers().indexOf(passenger);
            if (i == 0) {
                xzOffset = 0.1f;
            } else {
                xzOffset = -0.5f;
            }
        }
        xzOffset *= this.getGenome().getAdultScale();

        double yOffset = this.getPassengersRidingOffset() + passenger.getMyRidingOffset();
        // Compensate for saddle for players
        if (passenger instanceof Player && this.isSaddled()) {
            yOffset += 0.04 * this.getGenome().getAdultScale();
        }
        float standAnim0 = 0;
        try {
            standAnim0 = (Float)rearingAmountField.get(this);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (standAnim0 > 0.0F) {
            float xLoc = this.getBbWidth() + xzOffset;
            float facingX = Mth.sin(this.yBodyRot * ((float)Math.PI / 180F));
            float facingZ = Mth.cos(this.yBodyRot * ((float)Math.PI / 180F));
            // A rearing amount of 1 corresponds to 45 degrees up
            float rearAngle = standAnim0 * (float)Math.PI / 4F;
            // The y distance from the top of the back to the bottom of the belly (10 pixels)
            float bodyHeight = 10F / 16F * this.getGenome().getAdultScale();
            float rearXZ = xLoc * (Mth.cos(rearAngle) - 1F) - bodyHeight * Mth.sin(rearAngle);
            float rearY = Mth.sin(rearAngle) * xLoc / 2F;
            xzOffset += rearXZ;
            yOffset += rearY;
            if (passenger instanceof LivingEntity) {
                ((LivingEntity)passenger).yBodyRot = this.yBodyRot;
            }
        }

        // Here boats use this.yRot, but we use this.yBodyRot,
        // because this.yRot doesn't change when the unsaddled horse moves around
        Vec3 vector3d = new Vec3((double)xzOffset, 0.0D, 0.0D);
        vector3d = vector3d.yRot(-this.yBodyRot * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
        passenger.setPos(this.getX() + vector3d.x, this.getY() + yOffset, this.getZ() + vector3d.z);
        this.applyYaw(passenger);
        if (passenger instanceof Animal && this.getPassengers().size() > 1) {
            int degrees = passenger.getId() % 2 == 0 ? 90 : 270;
            passenger.setYBodyRot(((Animal)passenger).yBodyRot + (float)degrees);
            passenger.setYHeadRot(passenger.getYHeadRot() + (float)degrees);
        }
    }

    private void applyYaw(Entity entity) {
        if (!(entity instanceof Player)) {
            entity.setYBodyRot(this.yBodyRot);
            entity.setYRot(this.yBodyRot);
            entity.setYHeadRot(this.yBodyRot);
        }
    }

    @Override
    protected Component getTypeName() {
        String species = this.getSpecies().toString().toLowerCase();
        String s = "entity." + HorseColors.MODID + "." + species + ".";
        if (this.isBaby()) {
            // Foal
            if (!HorseConfig.BREEDING.enableGenders.get()) {
                return new TranslatableComponent(s + "foal");
            }
            // Colt
            if (this.isMale()) {
                return new TranslatableComponent(s + "colt");
            }
            // Filly
            return new TranslatableComponent(s + "filly");
        }

        // Horse
        if (!HorseConfig.BREEDING.enableGenders.get()) {
            return super.getTypeName();
        }
        // Stallion
        if (this.isMale()) {
            return new TranslatableComponent(s + "male");
        }
        // Mare
        return new TranslatableComponent(s + "female");
    }

    public boolean isSaddle(ItemStack stack) {
        return stack.isEmpty() || stack.is(Items.SADDLE);
    }

    // Override to allow alternate saddles to be equipped
    @Override
    public SlotAccess getSlot(int slot) {
        int num = slot - 400;
        if (num == 0) {
            return new SlotAccess() {
                public ItemStack get() {
                    return AbstractHorseGenetic.this.inventory.getItem(slot);
                }

                public boolean set(ItemStack stack) {
                    if (!isSaddle(stack)) {
                        return false;
                    }
                    else {
                        AbstractHorseGenetic.this.inventory.setItem(slot, stack);
                        AbstractHorseGenetic.this.updateContainerEquipment();
                        return true;
                    }
                }
            };
        }
        return super.getSlot(slot);
    }

    @Override
    // This is needed so when the mutation chance is high, mules bred
    // with spawn eggs do not produce all splashed white foals.
    public Breed getDefaultBreed() {
        return BaseEquine.breed;
    }

    // Randomize only health, for mules and donkeys
    @Override
    protected void randomizeAttributes() {
        // Set stats for vanilla-like breeding
        if (!HorseConfig.GENETICS.useGeneticStats.get()) {
            float maxHealth = this.generateRandomMaxHealth() + this.getGenome().getBaseHealth();
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)maxHealth);
        }
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, 
                                            DifficultyInstance difficultyIn, 
                                            MobSpawnType reason, 
                                            @Nullable SpawnGroupData spawnDataIn, 
                                            @Nullable CompoundTag dataTag)
    {
        if (!(spawnDataIn instanceof GeneticData)) {
            Breed breed = this.getRandomBreed();
            spawnDataIn = new GeneticData(breed);
        }
        Breed breed = ((GeneticData)spawnDataIn).breed;
        this.randomize(breed);
        // super.finalizeSpawn will call randomizeAttributes
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.useGeneticAttributes();
        this.updatePersistentData();
        return data;
    }

    protected void randomize(Breed breed) {
        this.getGenome().randomize(breed);
        // Choose a random age
        this.trueAge = this.random.nextInt(HorseConfig.GROWTH.getMaxAge());
        // This preserves the ratio of child/adult
        if (this.random.nextInt(5) == 0) {
            // Foals pick a random age within the younger half
            this.trueAge = this.getBirthAge() + this.random.nextInt(-this.getBirthAge() / 2);
        }
        this.setMale(this.random.nextBoolean());
        // Don't set the growing age to a positive value, that would be bad
        this.setAge(Math.min(0, this.trueAge));
        this.useGeneticAttributes();
        // Assume mother was the same size
        this.setMotherSize(this.getGenome().getGeneticScale());
        // Size depends on mother size so call again to stabilize somewhat
        this.setMotherSize(this.getGenome().getGeneticScale());
    }

    public void initFromVillageSpawn() {
        this.randomize(getRandomBreed());
        // All village horses are easier to tame
        this.modifyTemper(this.getMaxTemper() / 2);
        if (!this.isBaby() && this.random.nextInt(16) == 0) {
            // Tame and saddle
            this.setTamed(true);
            ItemStack saddle = new ItemStack(Items.SADDLE);
            this.inventory.setItem(0, saddle);
        }
    }

    // Total size change based on age that does not change proportions
    public float getProportionalAgeScale() {
        return getGenome().getCurrentScale() / getGangliness();
    }

    // The horse model uses this number to decide how foal-shaped to make the
    // horse. 0.5 is the most foal-shaped and 1 is the most adult-shaped.
    public float getGangliness() {
        return 0.5f + 0.5f * getFractionGrown() * getFractionGrown();
    }

    // Affects hitbox size.
    @Override
    public float getScale() {
        // This is different from LivingEntity.getScale which uses
        // 0.5 for children
        float base = isBaby()? 0.6f : 1.0f;
        return this.getGenome().getAdultScale() * base;
    }

    public boolean isTooSmallForPlayerToRide() {
        return HorseConfig.COMMON.enableSizes.get() && getGenome().isMiniature() 
            && !HorseConfig.COMMON.rideSmallEquines.get();
    }

    @Override
    public boolean isSaddleable() {
        return !isTooSmallForPlayerToRide() && super.isSaddleable();
    }

    @Override
    public boolean isPushable() {
        return !(this.isVehicle() 
            && this.getControllingPassenger() instanceof Player);
    }

    // For holding spawn data
    public static class GeneticData extends AgeableMob.AgeableMobGroupData {
        public final Breed breed;

        public GeneticData(Breed breed) {
            super(true);
            this.breed = breed;
        }
    }
}
