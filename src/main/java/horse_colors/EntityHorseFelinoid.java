package felinoid.horse_colors;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.ai.*;
import java.util.Arrays;

public class EntityHorseFelinoid extends AbstractHorse
{
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_ARMOR = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<ItemStack> HORSE_ARMOR_STACK = EntityDataManager.<ItemStack>createKey(EntityHorseFelinoid.class, DataSerializers.ITEM_STACK);


    /* Extension is the gene that determines whether black pigment can extend
    into the hair, or only reach the skin. 0 is red, 1 can have black. */

    /* Agouti controls where black hairs are placed. 0 is for black, 1 for seal 
    brown, 2 for bay, and, if I ever draw the pictures, 3 for wild bay. They're
    in order of least dominant to most. */

    /* Dun dilutes pigment (by restricting it to a certain part of each hair 
    shaft) and also adds primative markings such as a dorsal stripe. It's
    dominant, so non-dun is 0 and dun (wildtype) is 1. */

    /* Gray causes rapid graying with age. Here, it will simply mean the
    horse is gray. It is epistatic to every color except white. Gray is 
    dominat, so 0 is for non-gray, and 1 is for gray. */

    /* Cream makes red pigment a lot lighter and also dilutes black a 
    little. It's incomplete dominant, so here 0 is wildtype and 1 is cream. */

    /* Silver makes black manes and tails silvery, while lightening a black
    body color to a more chocolatey one, sometimes with dapples. Silver
    is dominant, so 0 for wildtype, 1 for silver. */

    /* Liver recessively makes chestnut darker. 0 for liver, 1 for non-liver. */

    /* Either flaxen gene makes the mane lighter; both in combination
    make the mane almost white. They're both recessive, so 0 for flaxen,
    1 for non-flaxen. */

    /* Sooty makes a horse darker, sometimes smoothly or sometimes in a 
    dapple pattern. Like flaxen, there's two recessive genes, and having just
    one will have some effect, but having both will have more. */

    /* Mealy turns some red hairs to white, generally on the belly or
    undersides. It's recessive, and, like flaxen, is a polygenetic trait. */
    private static final String[] genes = new String[] {"extension", "agouti", "dun", "gray", "cream", "silver", "liver", "flaxen1", "flaxen2", "sooty1", "sooty2", "mealy1", "mealy2"};

    private static final String[] HORSE_MARKING_TEXTURES = new String[] {null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] HORSE_MARKING_TEXTURES_ABBR = new String[] {"", "wo_", "wmo", "bdo"};
    private String texturePrefix;
    private final String[] horseTexturesArray = new String[6];

    
    private static final int NUM_MARKINGS = HORSE_MARKING_TEXTURES.length;

    public EntityHorseFelinoid(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.2D));
        this.tasks.addTask(1, new EntityAIRunAroundLikeCrazy(this, 1.2D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D, AbstractHorse.class));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.7D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(HORSE_VARIANT, Integer.valueOf(0));
        this.dataManager.register(HORSE_ARMOR, Integer.valueOf(HorseArmorType.NONE.getOrdinal()));
        this.dataManager.register(HORSE_ARMOR_STACK, ItemStack.EMPTY);
    }

    public static void registerFixesHorse(DataFixer fixer)
    {
        AbstractHorse.registerFixesAbstractHorse(fixer, EntityHorseFelinoid.class);
        fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityHorseFelinoid.class, new String[] {"ArmorItem"}));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("Variant", this.getHorseVariant());

        if (!this.horseChest.getStackInSlot(1).isEmpty())
        {
            compound.setTag("ArmorItem", this.horseChest.getStackInSlot(1).writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.setHorseVariant(compound.getInteger("Variant"));

        if (compound.hasKey("ArmorItem", 10))
        {
            ItemStack itemstack = new ItemStack(compound.getCompoundTag("ArmorItem"));

            if (!itemstack.isEmpty() && isArmor(itemstack))
            {
                this.horseChest.setInventorySlotContents(1, itemstack);
            }
        }

        this.updateHorseSlots();
    }

    public void setHorseVariant(int variant)
    {
        this.dataManager.set(HORSE_VARIANT, Integer.valueOf(variant));
        this.resetTexturePrefix();
    }

    public int getHorseVariant()
    {
        return ((Integer)this.dataManager.get(HORSE_VARIANT)).intValue();
    }

    /* For calling when debugging. */
    public static void test()
    {
        System.out.println("");
        for (String gene : genes)
        {
            System.out.print(gene + ": " + Integer.toBinaryString(getGeneLoci(gene)) + "\n");
        }
    }

    public static int getGenePos(String name)
    {
        int i = 0;
        for (String gene : genes)
        {
            if (gene == name)
            {
                return i;
            }
            // Special case to keep each gene completely on the same int
            if ((i + (2 * getGeneSize(gene))) / 32 == i / 32)
            {
                i += (2 * getGeneSize(gene));
            }
            else
            {
                i = (i / 32 + 1) * 32;
            }
        }

        // Return statement needed to compile
        System.out.println("Gene not recognized: " + name);
        return -1;
    }

    /* This returns the number of bits needed to store one allele. */
    public static int getGeneSize(String gene)
    {
        switch(gene) 
        {
            case "agouti": return 2;

            case "extension":
            case "dun":
            case "gray":
            case "cream":
            case "silver":
            case "liver":
            case "flaxen1":
            case "flaxen2":
            case "sooty1":
            case "sooty2":
            case "mealy1":
            case "mealy2": return 1;
        }
        System.out.println("Gene size not found: " + gene);
        return -1;
    }

    /* This returns a bitmask which is 1 where the gene is stored and 0 everywhere else. */
    public static int getGeneLoci(String gene)
    {
        return ((1 << (2 * getGeneSize(gene))) - 1) << getGenePos(gene);
    }

    public void setGene(String name, int val)
    {
        setHorseVariant((getHorseVariant() & (~getGeneLoci(name))) 
            | (val << getGenePos(name)));
    }

    public int getGene(String name)
    {
        return (getHorseVariant() & getGeneLoci(name)) >> getGenePos(name);
    }

    public int getPhenotype(String name)
    {
        switch(name)
        {
            /* Simple dominant  or recessive genes. */
            case "extension":
            case "gray":
            case "dun":
            case "silver":
            case "liver":
            case "flaxen1":
            case "flaxen2":
            case "sooty1":
            case "sooty2":
            case "mealy1":
            case "mealy2":
                return getGene(name) == 0? 0 : 1;

            /* Incomplete dominance. */
            case "cream":
                /* Low bit plus high bit. */
                return (getGene(name) & 1) + (getGene(name) >> 1);
            
            /* Polygenetic traits. */
            case "flaxen":
                return 2 - getPhenotype("flaxen1") - getPhenotype("flaxen2");
            case "sooty":
                return 2 - getPhenotype("sooty1") - getPhenotype("sooty2");
            case "mealy":
                return 2 - getPhenotype("mealy1") - getPhenotype("mealy2");

            /* Genes with multiple alleles. */
            case "agouti":
                return Math.max(getGene("agouti") & 3, getGene("agouti") >> 2);
                
        }
        System.out.println("[horse_colors]: Phenotype for " + name + " not found.");
        return -1;
    }

    private void resetTexturePrefix()
    {
        this.texturePrefix = null;
    }

    @SideOnly(Side.CLIENT)
    private static String fixPath(String inStr) {
        if (inStr == null || inStr.contains(".png")) {
            return inStr;
        }
        else {
            return "horse_colors:textures/entity/horse/" + inStr +".png";
        }
    }

    private String getBaseTexture()
    {
        // First handle double cream dilutes
        if (getPhenotype("cream") == 2)
        {
            // Gray could change the hairs to properly white
            if (getPhenotype("gray") != 0)
            {
                return "white";
            }
            // Not gray, so check if base color is chestnut
            if (getPhenotype("extension") == 0)
            {
                return "cremello";
            }
            // Base color is black or bay. Check for silver.
            if (getPhenotype("silver") != 0)
            {
                switch(getPhenotype("agouti"))
                {
                    case 0: return "silver_smoky_cream";
                    case 1: return "silver_brown_cream";
                    case 2:
                    case 3: return "silver_perlino";
                }
            }
            // Just a regular double cream. 
            switch(getPhenotype("agouti"))
            {
                case 0: return "smoky_cream";
                case 1: return "brown_cream";
                case 2:
                case 3: return "perlino";
            }
            
            
        }
        // Single cream dilutes
        else if (getPhenotype("cream") == 1)
        {
            // Check for gray
            if (getPhenotype("gray") != 0)
            {
                return "light_gray";
            }
            // Single cream, no gray. Check for dun.
            if (getPhenotype("dun") != 0)
            {
                // Dun + single cream.
                // Check for chestnut before looking for silver.
                if (getPhenotype("extension") == 0)
                {
                    return "dunalino";
                }
                // Black-based, so check for silver.
                if (getPhenotype("silver") != 0)
                {
                    switch(getPhenotype("agouti"))
                    {
                        case 0: return (getPhenotype("liver") == 0? "dark_" : "") + "silver_smoky_grullo";
                        case 1: return "silver_smoky_brown_dun";
                        case 2:
                        case 3: return "silver_dunskin";
                    }
                }
                switch(getPhenotype("agouti"))
                {
                    case 0: return "smoky_grullo";
                    case 1: return "smoky_brown_dun";
                    case 2:
                    case 3: return "dunskin";
                }
            }
            // Single cream, no gray, no dun. Check for chestnut base.
            if (getPhenotype("extension") == 0)
            {
                return "palomino";
            }
            // Non-chestnut, so check for silver.
            if (getPhenotype("silver") != 0)
            {
                switch(getPhenotype("agouti"))
                {
                    case 0: return (getPhenotype("liver") == 0? "dark_" : "") + "silver_grullo";
                    case 1: return "silver_smoky_brown";
                    case 2:
                    case 3: return "silver_buckskin";
                }
            }
            // Single cream, non-chestnut, with nothing else.
            switch(getPhenotype("agouti"))
            {
                case 0: return "smoky_black";
                case 1: return "smoky_brown";
                case 2:
                case 3: return "buckskin";
            }
        }
        // No cream, check for gray
        if (getPhenotype("gray") != 0)
        {
            // TODO: I have more than one gray and need to decide which to use.
            return "light_gray";
        }

        // No cream, no gray. Check for dun.
        if (getPhenotype("dun") != 0)
        {
            // Dun. Check for chestnut.
            if (getPhenotype("extension") == 0)
            {
                // Red dun. Check for liver.
                if (getPhenotype("liver") == 0)
                {
                    // Check for flaxen.
                    switch(getPhenotype("flaxen"))
                    {
                        case 0: return "liver_dun";
                        case 1: return "partly_flaxen_liver_dun";
                        case 2: return "flaxen_liver_dun";
                    }
                }
                // Not liver. Check for flaxen.
                switch(getPhenotype("flaxen"))
                {
                    case 0: return "red_dun";
                    case 1: return "partly_flaxen_dun";
                    case 2: return "flaxen_dun";
                }
            }

            // Dun, non-chestnut. Check for silver.
            if (getPhenotype("silver") != 0)
            {
                switch(getPhenotype("agouti"))
                {
                    case 0: return "silver_grullo";
                    case 1: return "silver_brown_dun";
                    case 2:
                    case 3: return "silver_dun";
                }
            }

            // Dun, non-chestnut, no other dilutions.
            switch(getPhenotype("agouti"))
            {
                case 0: return "grullo";
                case 1: return "brown_dun";
                case 2:
                case 3: return "dun";
            }
        }

        // No cream, gray, or dun. Check for chestnut.
        if (getPhenotype("extension") == 0)
        {
            String result = "chestnut";
            // So far just chestnut. Check for liver.
            if (getPhenotype("liver") == 0)
            {
                result = "liver_" + result;
            }
            // Check for flaxen.
            switch(getPhenotype("flaxen"))
            {
                case 1:
                    result = "partly_flaxen_" + result;
                    break;
                case 2:
                    result = "flaxen_" + result;
            }


            return result;
        }

        // Non-chestnut with no cream, gray, or dun. check for silver.
        if (getPhenotype("silver") != 0)
        {
            switch(getPhenotype("agouti"))
            {
                case 0: return "chocolate";
                case 1: return "silver_brown";
                case 2:
                case 3: return "silver_bay";
            }
        }

        // Non-chestnut with your basic, undiluted, unmodified coat.
        switch(getPhenotype("agouti"))
        {
            case 0: return "black";
            case 1: return "seal_brown";
            case 2:
            case 3: return "bay";
        }

        // This point should not be reached, but java wants a return to compile.
        System.out.println("[horse_colors]: Texture not found for horse with variant "
            + getHorseVariant() + ".");
        return "no texture found";
    }

    @SideOnly(Side.CLIENT)
    private void setHorseTexturePaths()
    {
        String baseTexture = getBaseTexture();

        int i = this.getHorseVariant();
        int m = ((i & 65280) >> 8) % NUM_MARKINGS;

        // Todo: make this follow the gene, once the gene exists
        String roan = null;

        String sooty1 = null;
        String sooty2 = null;
        if (getPhenotype("gray") == 0 && getPhenotype("cream") != 2)
        {
            if (getPhenotype("sooty1") == 0)
            {
                sooty1 = getPhenotype("dun") == 0? "sooty1" : "sooty1_dun";
            }
            if (getPhenotype("sooty2") == 0)
            {
                sooty1 = getPhenotype("dun") == 0? "sooty2" : "sooty2_dun";
            }
        }

        ItemStack armorStack = this.dataManager.get(HORSE_ARMOR_STACK);
        String texture = !armorStack.isEmpty() ? armorStack.getItem().getHorseArmorTexture(this, armorStack) : HorseArmorType.getByOrdinal(this.dataManager.get(HORSE_ARMOR)).getTextureName(); //If armorStack is empty, the server is vanilla so the texture should be determined the vanilla way

        this.horseTexturesArray[0] = fixPath(baseTexture);
        this.horseTexturesArray[1] = fixPath(sooty1);
        this.horseTexturesArray[2] = fixPath(sooty2);
        this.horseTexturesArray[3] = fixPath(roan);
        this.horseTexturesArray[4] = null; // TODO
        this.horseTexturesArray[5] = texture;

        String sooty1abv = sooty1 == null? "" : sooty1;
        String sooty2abv = sooty2 == null? "" : sooty2;
        String roanabv = roan == null? "" : roan;
        this.texturePrefix = "horse/cache_" + baseTexture + sooty1abv + sooty2abv + roanabv + "" + texture;
    }

    @SideOnly(Side.CLIENT)
    public String getHorseTexture()
    {
        if (this.texturePrefix == null)
        {
            this.setHorseTexturePaths();
        }

        return this.texturePrefix;
    }

    @SideOnly(Side.CLIENT)
    public String[] getVariantTexturePaths()
    {
        if (this.texturePrefix == null)
        {
            this.setHorseTexturePaths();
        }

        return this.horseTexturesArray;
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
        HorseArmorType horsearmortype = HorseArmorType.getByItemStack(itemStackIn);
        this.dataManager.set(HORSE_ARMOR, Integer.valueOf(horsearmortype.getOrdinal()));
        this.dataManager.set(HORSE_ARMOR_STACK, itemStackIn);
        this.resetTexturePrefix();

        if (!this.world.isRemote)
        {
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
            int i = horsearmortype.getProtection();

            if (i != 0)
            {
                this.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, 0)).setSaved(false));
            }
        }
    }

    public HorseArmorType getHorseArmorType()
    {
        HorseArmorType armor = HorseArmorType.getByItemStack(this.dataManager.get(HORSE_ARMOR_STACK)); //First check the Forge armor DataParameter
        if (armor == HorseArmorType.NONE) armor = HorseArmorType.getByOrdinal(this.dataManager.get(HORSE_ARMOR)); //If the Forge armor DataParameter returns NONE, fallback to the vanilla armor DataParameter. This is necessary to prevent issues with Forge clients connected to vanilla servers.
        return armor;
    }

    /**
     * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
     */
    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        HorseArmorType horsearmortype = this.getHorseArmorType();
        super.onInventoryChanged(invBasic);
        HorseArmorType horsearmortype1 = this.getHorseArmorType();

        if (this.ticksExisted > 20 && horsearmortype != horsearmortype1 && horsearmortype1 != HorseArmorType.NONE)
        {
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

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getModifiedMovementSpeed());
        this.getEntityAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (this.world.isRemote && this.dataManager.isDirty())
        {
            this.dataManager.setClean();
            this.resetTexturePrefix();
        }
        ItemStack armor = this.horseChest.getStackInSlot(1);
        if (isArmor(armor)) armor.getItem().onHorseArmorTick(world, this, armor);
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

    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_HORSE;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() == Items.SPAWN_EGG)
        {
            return super.processInteract(player, hand);
        }
        else
        {
            if (!this.isChild())
            {
                if (this.isTame() && player.isSneaking())
                {
                    this.openGUI(player);
                    return true;
                }

                if (this.isBeingRidden())
                {
                    return super.processInteract(player, hand);
                }
            }

            if (flag)
            {
                if (this.handleEating(player, itemstack))
                {
                    if (!player.capabilities.isCreativeMode)
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

                boolean flag1 = HorseArmorType.getByItemStack(itemstack) != HorseArmorType.NONE;
                boolean flag2 = !this.isChild() && !this.isHorseSaddled() && itemstack.getItem() == Items.SADDLE;

                if (flag1 || flag2)
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
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        // If I make my own donkey I should change this so they can make mules
        else if (!(otherAnimal instanceof EntityHorseFelinoid))
        {
            return false;
        }
        else
        {
            return this.canMate() && ((EntityHorseFelinoid)otherAnimal).canMate();
        }
    }

    /* Argument is the number of genewidths to the left each gene should be
    shifted. */
    private int getRandomGenes(int n)
    {
        int result = 0;
        int random = 0;
        String out = "";
        for (String gene : genes)
        {
            if (getGenePos(gene) % 32 == 0)
            {
                random = this.rand.nextInt();
            }

            int next = getGene(gene);
            // Randomly take the low bits or the high bits
            if (random % 2 == 0)
            {
                // Keep high bits, put them in low bit position
                next >>= getGeneSize(gene);
            }
            else
            {
                // Keep low bits
                next &= (1 << getGeneSize(gene)) - 1;
            }
            out = out + gene + ": " + getGene(gene) + " -> " + next + "\n";
            random >>= 1;

            // Add the allele we've selected to the final result
            result |= next << getGenePos(gene) << (n * getGeneSize(gene));
        }

        System.out.print(out);
        return result;
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable)
    {
        AbstractHorse abstracthorse;

        if (ageable instanceof EntityDonkey)
        {
            abstracthorse = new EntityMule(this.world);
        }
        else
        {
            EntityHorseFelinoid entityhorse = (EntityHorseFelinoid)ageable;
            abstracthorse = new EntityHorseFelinoid(this.world);

            int mother = this.getRandomGenes(1);
            int father = entityhorse.getRandomGenes(0);

            int i = mother | father;

            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i);

            /* Print debugging information. */
            System.out.print("Mating:\n    Variant: " + Integer.toBinaryString(this.getHorseVariant()) + "\n    Color: " + this.getBaseTexture() + "\n    Genes passed: " + Integer.toBinaryString(mother) + "\n\n    Variant: " + Integer.toBinaryString(entityhorse.getHorseVariant()) + "\n    Color: " + entityhorse.getBaseTexture() + "\n    Genes passed: " + Integer.toBinaryString(father) + "\n");
        }

        this.setOffspringAttributes(ageable, abstracthorse);
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
        return HorseArmorType.isHorseArmor(stack);
    }

    /* This function changes the variant and then puts it back to what it was
    before. */
    // TODO: clean up
    private int getRandomVariant(int n)
    {
        int startVariant = getHorseVariant();

        int i = this.rand.nextInt();
        setGene("extension", (i & 1) << (n * getGeneSize("extension")));
        i >>= 1;
        setGene("gray", (i % 20 == 0? 1 : 0) << (n * getGeneSize("gray")));
        i >>= 5;
        setGene("dun", (i % 4 == 0? 1 : 0) << (n * getGeneSize("dun")));
        i >>= 2;

        int ag = i % 32;
        int agouti = ag == 0? 3 : (ag < 18? 2 : (ag < 20? 1: 0));
        setGene("agouti", agouti << (n * getGeneSize("agouti")));
        i >>= 5;

        setGene("silver", (i % 32 == 0? 1 : 0) << (n * getGeneSize("silver")));
        i >>= 5;

        setGene("cream", (i % 32 == 0? 1 : 0) << (n * getGeneSize("cream")));
        i >>= 5;

        setGene("liver", (i % 3 == 0? 0 : 1) << (n * getGeneSize("liver")));
        i >>= 2;

        setGene("flaxen1", (i % 4 == 0? 0 : 1) << (n * getGeneSize("flaxen1")));
        i >>= 2;

        setGene("flaxen2", (i % 4 == 0? 0 : 1) << (n * getGeneSize("flaxen2")));
        i >>= 2;

        setGene("sooty1", (i % 2 == 0? 0 : 1) << (n * getGeneSize("sooty1")));
        // Out of random digits; time for more!
        i = Math.abs(this.rand.nextInt());

        setGene("sooty2", (i % 2 == 0? 0 : 1) << (n * getGeneSize("sooty2")));
        i >>= 2;

        setGene("mealy1", (i % 4 == 0? 0 : 1) << (n * getGeneSize("mealy1")));
        i >>= 2;

        setGene("mealy2", (i % 4 == 0? 0 : 1) << (n * getGeneSize("mealy2")));

        int answer = getHorseVariant();
        setHorseVariant(startVariant);
        return answer;
    }

    /* Make the horse have random genetics. */
    public void randomize()
    {
        int i = getRandomVariant(0);
        int j = getRandomVariant(1);
        setHorseVariant(i | j);
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);

        // TODO
        /*
        if (livingdata instanceof EntityHorseFelinoid.GroupData)
        {
            int i = ((EntityHorseFelinoid.GroupData)livingdata).variant;
            this.setHorseVariant(i);
        }
        else
        {
            this.randomize();
            livingdata = new EntityHorseFelinoid.GroupData(getHorseVariant());
        }
        */

        this.randomize();
        System.out.println("New horse with variant " + getHorseVariant());
        return livingdata;
    }

    public static class GroupData implements IEntityLivingData
        {
            public int variant;

            public GroupData(int variantIn)
            {
                this.variant = variantIn;
            }
        }
}
