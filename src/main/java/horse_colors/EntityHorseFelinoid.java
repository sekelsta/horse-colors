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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

import java.util.Arrays;

public class EntityHorseFelinoid extends AbstractHorse
{
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_SPEED = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_JUMP = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_HEALTH = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_RANDOM = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HORSE_ARMOR = EntityDataManager.<Integer>createKey(EntityHorseFelinoid.class, DataSerializers.VARINT);
    private static final DataParameter<ItemStack> HORSE_ARMOR_STACK = EntityDataManager.<ItemStack>createKey(EntityHorseFelinoid.class, DataSerializers.ITEM_STACK);


    /* Extension is the gene that determines whether black pigment can extend
    into the hair, or only reach the skin. 0 is red, 1 can have black. */

    /* Agouti controls where black hairs are placed. 0 is for black, 1 for seal 
    brown, 2 for bay, and, if I ever draw the pictures, 3 for wild bay. They're
    in order of least dominant to most. */

    /* Dun dilutes pigment (by restricting it to a certain part of each hair 
    shaft) and also adds primative markings such as a dorsal stripe. It's
    dominant, so dun (wildtype) is 11 or 10, non-dun1 (with dorsal stripe) is
    01, and non-dun2 (without dorsal stripe) is 00. ND1 and ND2 are
    codominate: a horse with both will have a fainter dorsal stripe. */

    /* Gray causes rapid graying with age. Here, it will simply mean the
    horse is gray. It is epistatic to every color except white. Gray is 
    dominant, so 0 is for non-gray, and 1 is for gray. */

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
    dapple pattern. */

    /* Mealy turns some red hairs to white, generally on the belly or
    undersides. It's recessive, and, like flaxen, is a polygenetic trait. */
    public static final String[] genes = new String[] {"extension", "agouti", "dun", "gray", "cream", "silver", "liver", "flaxen1", "flaxen2", "dapple", "sooty1", "sooty2", "sooty3", "mealy1", 
        "mealy2", "mealy3", "white_suppression", "KIT", "frame", "splash", "leopard", "PATN1", "PATN2", "PATN3", "gray_suppression", "gray_mane", "slow_gray1", "slow_gray2"};

    private String texturePrefix;

    // See the function that sets this to find what each of  the layers are for
    private final String[] horseTexturesArray = new String[15];

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
        this.dataManager.register(HORSE_VARIANT2, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT3, Integer.valueOf(0));
        this.dataManager.register(HORSE_SPEED, Integer.valueOf(0));
        this.dataManager.register(HORSE_HEALTH, Integer.valueOf(0));
        this.dataManager.register(HORSE_JUMP, Integer.valueOf(0));
        this.dataManager.register(HORSE_RANDOM, Integer.valueOf(0));
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
        compound.setInteger("Variant", this.getHorseVariant("0"));
        compound.setInteger("Variant2", this.getHorseVariant("1"));
        compound.setInteger("Variant3", this.getHorseVariant("2"));
        compound.setInteger("SpeedGenes", this.getHorseVariant("speed"));
        compound.setInteger("JumpGenes", this.getHorseVariant("jump"));
        compound.setInteger("HealthGenes", this.getHorseVariant("health"));
        compound.setInteger("Random", this.getHorseVariant("random"));

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
        this.setHorseVariant(compound.getInteger("Variant"), "0");
        this.setHorseVariant(compound.getInteger("Variant2"), "1");
        this.setHorseVariant(compound.getInteger("Variant3"), "2");
        this.setHorseVariant(compound.getInteger("SpeedGenes"), "speed");
        this.setHorseVariant(compound.getInteger("JumpGenes"), "jump");
        this.setHorseVariant(compound.getInteger("HealthGenes"), "health");
        this.setHorseVariant(compound.getInteger("Random"), "random");

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

    public void setHorseVariant(int variant, String type)
    {
        switch(type) {
            case "0":
                this.dataManager.set(HORSE_VARIANT, Integer.valueOf(variant));
                break;
            case "1":
                this.dataManager.set(HORSE_VARIANT2, Integer.valueOf(variant));
                break;
            case "2":
                this.dataManager.set(HORSE_VARIANT3, Integer.valueOf(variant));
                break;
            case "speed":
                this.dataManager.set(HORSE_SPEED, Integer.valueOf(variant));
                return;
            case "jump":
                this.dataManager.set(HORSE_JUMP, Integer.valueOf(variant));
                return;
            case "health":
                this.dataManager.set(HORSE_HEALTH, Integer.valueOf(variant));
                return;
            case "random":
                this.dataManager.set(HORSE_RANDOM, Integer.valueOf(variant));
                break;
            default:
                System.out.print("Unrecognized horse data for setting: "
                                 + type + "\n");
        }
        this.resetTexturePrefix();
    }

    public int getHorseVariant(String type)
    {
        switch(type) {
            case "0":
                return ((Integer)this.dataManager.get(HORSE_VARIANT)).intValue();
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
                                + type + "\n");
                return 0;
        }
        
    }

    /* For calling when debugging. */
    public static void test()
    {
        System.out.println("");
        for (String gene : genes)
        {
            //System.out.print(gene + ": " + Integer.toBinaryString(getGeneLoci(gene)) + "\n");
            System.out.print(gene + ": " + getGenePos(gene) + "\n");
        }
    }

    public static int getGenePos(String name)
    {
        int i = 0;
        for (String gene : genes)
        {
            // Special case to keep each gene completely on the same int
            int next = (i + (2 * getGeneSize(gene)));
            if (next / 32 != i / 32 && next % 32 != 0)
            {
                i = (i / 32 + 1) * 32;
            }

            if (gene == name)
            {
                return i;
            }
            i += (2 * getGeneSize(gene));
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
            case "KIT": return 4;

            case "agouti":
            case "dun": return 2;

            case "extension":
            case "gray":
            case "cream":
            case "silver":
            case "liver":
            case "flaxen1":
            case "flaxen2":
            case "dapple":
            case "sooty1":
            case "sooty2":
            case "sooty3":
            case "mealy1":
            case "mealy2":
            case "mealy3":
            case "white_suppression":
            case "frame":
            case "splash":
            case "leopard":
            case "PATN1":
            case "PATN2":
            case "PATN3":
            case "gray_suppression":
            case "gray_mane":
            case "slow_gray1":
            case "slow_gray2": return 1;
        }
        System.out.println("Gene size not found: " + gene);
        return -1;
    }

    /* This returns a bitmask which is 1 where the gene is stored and 0 everywhere else. */
    public static int getGeneLoci(String gene)
    {
        return ((1 << (2 * getGeneSize(gene))) - 1) << (getGenePos(gene) % 32);
    }

    public static String getGeneChromosome(String gene)
    {
        // Which of the ints full of genes ours is on
        return Integer.toString(getGenePos(gene) / 32);
    }

    public void setGene(String name, int val)
    {
        String chr = getGeneChromosome(name);
        setHorseVariant((getHorseVariant(chr) & (~getGeneLoci(name))) 
            | (val << (getGenePos(name) % 32)), chr);
    }

    public int getGene(String name)
    {
        String chr = getGeneChromosome(name);
        // Use unsigned right shift to avoid returning negative numbers
        return (getHorseVariant(chr) & getGeneLoci(name)) >>> getGenePos(name);
    }

    public int getAllele(String name, int n)
    {
        int gene = getGene(name);
        gene >>= n * getGeneSize(name);
        gene %= 1 << getGeneSize(name);
        return gene;
    }

    public int getStat(String name)
    {
        int val = getHorseVariant(name);
        int count = 0;
        for (int i = 0; i < 32; ++i)
        {
            count += ((val % 2) + 2) % 2;
            val >>= 1;
        }
        return count;
    }

    public int getPhenotype(String name)
    {
        switch(name)
        {
            /* Simple dominant  or recessive genes. */
            case "extension":
            case "silver":
            case "liver":
            case "flaxen1":
            case "flaxen2":
            case "dapple":
            case "sooty1":
            case "sooty2":
            case "sooty3":
            case "mealy1":
            case "mealy2":
            case "mealy3":
            case "white_suppression":
            case "PATN2":
            case "PATN3":
            case "gray_suppression":
            case "slow_gray1":
                return getGene(name) == 0? 0 : 1;

            /* Incomplete dominant. */
            case "gray":
            case "cream":
            case "frame":
            case "splash":
            case "leopard":
            case "PATN1":
            case "gray_mane":
            case "slow_gray2":
                /* Low bit plus high bit. */
                return (getGene(name) & 1) + (getGene(name) >> 1);
                
            
            /* Polygenetic traits. */
            case "flaxen":
                return 2 - getPhenotype("flaxen1") - getPhenotype("flaxen2");
            case "sooty":
                // sooty1 and 2 dominant, 3 recessive
                return 1 + getPhenotype("sooty1") + getPhenotype("sooty2") 
                        - getPhenotype("sooty3");
            case "mealy":
                return 2 - getPhenotype("mealy1") - getPhenotype("mealy2");

            /* Genes with multiple alleles. */
            case "agouti":
                if (getGene("agouti") == 1 || getGene("agouti") == 4)
                {
                    return 1;
                }
                int allele = Math.max(getGene("agouti") & 3, getGene("agouti") >> 2);
                return allele == 0? 0 : allele + 1;

            case "dun":
                if (getGene(name) <= 1) 
                {
                    // 0 for ND2 (no dorsal stripe), 1 for ND1/+ (faint 
                    // dorsal stripe), 2 for ND1/ND1 (dorsal stripe), 3 for dun
                    return getGene(name);
                }
                else if (getGene(name) == 4)
                {
                    return 1;
                }
                else if (getGene(name) == 5)
                {
                    return 2;
                }
                else
                {
                    return 3;
                }
            // Don't give useful info when asked for KIT, but also don't
            // give an error
            case "KIT":
                return -1;
            /* KIT mappings:
               0: wildtype
               1: no markings
               2: white boost: small white boost (star sometimes)
               3: star (on average)
               4: strip (on average)
               5: half-socks (on average)
               6: markings (strip and half-socks typical)
               7: W20 (strip and socks typical as heterozygous, 
                    when homozygous, irregular draft sabino with some belly white)
               8: flashy white: stockings and blaze (on average)
               9: draft sabino (four stockings and blaze)
               10: wildtype for now
               11: tobiano
               12: sabino1
               13: tobiano + W20
               14: roan
               15: white
            */
            case "white_boost":
                return ((getGene("KIT") & 15) == 2
                        || (getGene("KIT") >> 4) == 2)? 1 : 0;
            case "star":
                return ((getGene("KIT") & 15) == 3
                        || (getGene("KIT") >> 4) == 3)? 1 : 0;
            case "strip":
                return ((getGene("KIT") & 15) == 4
                        || (getGene("KIT") >> 4) == 4)? 1 : 0;
            case "half-socks":
                return ((getGene("KIT") & 15) == 5
                        || (getGene("KIT") >> 4) == 5)? 1 : 0;
            case "markings":
                return ((getGene("KIT") & 15) == 6
                        || (getGene("KIT") >> 4) == 6)? 1 : 0;
            // W20 is incomplete dominant
            case "W20":
                boolean w1 = getAllele("KIT", 0) == 13 
                                || getAllele("KIT", 0) == 7;
                boolean w2 = getAllele("KIT", 1) == 13 
                                || getAllele("KIT", 1) == 7;
                if (w1 && w2)
                {
                    return 2;
                }
                else
                {
                    return (w1 || w2)? 1 : 0;
                }
            case "flashy_white":
                return ((getGene("KIT") & 15) == 8
                        || (getGene("KIT") >> 4) == 8)? 1 : 0;
            case "draft_sabino":
                return ((getGene("KIT") & 15) == 9
                        || (getGene("KIT") >> 4) == 9)? 1 : 0;
            // Sabino1 and tobiano are also incomplete dominant
            case "sabino1":
                if (getGene("KIT") == (12 << 4) + 12)
                {
                    return 2;
                }
                else
                {
                    return ((getGene("KIT") & 15) == 12) 
                            || ((getGene("KIT") >> 4) == 12)? 1 : 0;
                }
            case "tobiano":
                boolean tob1 = getAllele("KIT", 0) == 13 
                                || getAllele("KIT", 0) == 11;
                boolean tob2 = getAllele("KIT", 1) == 13 
                                || getAllele("KIT", 1) == 11;
                if (tob1 && tob2)
                {
                    return 2;
                }
                else
                {
                    return (tob1 || tob2)? 1 : 0;
                }
            case "roan":
                return ((getGene("KIT") & 15) == 14 
                        || (getGene("KIT") >> 4) == 14)? 1 : 0;
            case "dominant_white":
                if (getGene("KIT") == (15 << 4) + 15)
                {
                    return 2;
                }
                return ((getGene("KIT") & 15) == 15
                        || (getGene("KIT") >> 4) == 15)? 1 : 0;
            case "white":
                return (getPhenotype("dominant_white") != 0 // dominant white
                        || getPhenotype("frame") == 2  // lethal white overo
                        || getPhenotype("sabino1") == 2 // sabino white
                        || (getPhenotype("sabino1") != 0 
                            && getPhenotype("frame") != 0
                            && getPhenotype("tobiano") != 0))
                                ? 1 : 0;
            // other KIT: TODO
            case "PATN":
                int base = 5 * getPhenotype("PATN1") + getPhenotype("PATN2")
                           + getPhenotype("PATN3");
                return base == 0? 0 : base + getPhenotype("W20") 
                                        + getPhenotype("white_boost");
            case "slow_gray":
                int val = getPhenotype("slow_gray1") + getPhenotype("slow_gray2")
                        + (getPhenotype("gray") == 2? -2 : 0);
                return Math.max(val, 0);
                
        }
        System.out.println("[horse_colors]: Phenotype for " + name + " not found.");
        return -1;
    }

    private void resetTexturePrefix()
    {
        this.texturePrefix = null;
    }

    @SideOnly(Side.CLIENT)
    private static String fixPath(String folder, String inStr) {
        if (inStr == null || inStr.contains(".png")) {
            return inStr;
        }
        else if (inStr == "")
        {
            return null;
        }
        else {
            return "horse_colors:textures/entity/horse/" + folder + "/" + inStr +".png";
        }
    }

    public String getBaseTexture()
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
                    case 1:
                    case 2: return "silver_brown_cream";
                    case 3:
                    case 4: return "silver_perlino";
                }
            }
            // Just a regular double cream. 
            switch(getPhenotype("agouti"))
            {
                case 0: return "smoky_cream";
                case 1:
                case 2: return "brown_cream";
                case 3:
                case 4: return "perlino";
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
            if (getPhenotype("dun") == 3)
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
                        case 1:
                        case 2: return "silver_smoky_brown_dun";
                        case 3:
                        case 4: return "silver_dunskin";
                    }
                }
                switch(getPhenotype("agouti"))
                {
                    case 0: return "smoky_grullo";
                    case 1:
                    case 2: return "smoky_brown_dun";
                    case 3:
                    case 4: return "dunskin";
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
                    case 1:
                    case 2: return "silver_smoky_brown";
                    case 3:
                    case 4: return "silver_buckskin";
                }
            }
            // Single cream, non-chestnut, with nothing else.
            switch(getPhenotype("agouti"))
            {
                case 0: return "smoky_black";
                case 1:
                case 2: return "smoky_brown";
                case 3:
                case 4: return "buckskin";
            }
        }
        // No cream, check for gray
        if (getPhenotype("gray") != 0)
        {
            // TODO: I have more than one gray and need to decide which to use.
            return "light_gray";
        }

        // No cream, no gray. Check for dun.
        if (getPhenotype("dun") == 3)
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
                    case 1:
                    case 2: return "silver_brown_dun";
                    case 3:
                    case 4: return "silver_dun";
                }
            }

            // Dun, non-chestnut, no other dilutions.
            switch(getPhenotype("agouti"))
            {
                case 0: return "grullo";
                case 1:
                case 2: return "brown_dun";
                case 3:
                case 4: return "dun";
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
                case 1:
                case 2: return "silver_brown";
                case 3:
                case 4: return "silver_bay";
            }
        }

        // Non-chestnut with your basic, undiluted, unmodified coat.
        switch(getPhenotype("agouti"))
        {
            case 0: return "black";
            case 1:
            case 2: return "seal_brown";
            case 3:
            case 4: return "bay";
        }

        // This point should not be reached, but java wants a return to compile.
        System.out.println("[horse_colors]: Texture not found for horse with variant "
            + getHorseVariant("0") + ".");
        return "no texture found";
    }

    public String getSooty(String base_color)
    {
        if (getPhenotype("cream") == 2)
        {
            return null;
        }
        int sooty_level = 0;
        if (getPhenotype("gray") == 0)
        {
            sooty_level = getPhenotype("sooty");
        }
        else
        {
            sooty_level = getPhenotype("slow_gray");
        }
        if (sooty_level == 0)
        {
            return null;
        }

        boolean is_dun = getPhenotype("dun") == 3 && getPhenotype("gray") == 0;
        String suffix = "";
        if (is_dun)
        {
            if (sooty_level == 0)
            {
                return "sooty_dun";
            }
            sooty_level -= 1;
            suffix = "_dun";
        }

        if (sooty_level == 3)
        {
            suffix += "_dark";
        }
        else if (sooty_level == 1)
        {
            suffix += "_light";
        }

        String type = "countershaded";
        boolean is_chestnut = getPhenotype("extension") == 0
            && getPhenotype("cream") == 0
            && getPhenotype("liver") != 0;
        if (getPhenotype("gray") != 0)
        {
            type = "dappled";
        }
        else if (getPhenotype("dapple") != 0)
        {
            type = is_chestnut? "even" : "dappled";
        }

        return "sooty_" + type + suffix;
    }

    public String getMealy()
    {
        // TODO
        return null;
    }

    public String getLegs()
    {
        if ((getPhenotype("extension") == 0 && getPhenotype("dun") == 0)
            || getPhenotype("gray") != 0)
        {
            return null;
        }

        String legs = null;
        if (getPhenotype("extension") != 0 
            && (getPhenotype("agouti") >= 3 || getPhenotype("dun") != 0))
        {
            if (getPhenotype("cream") == 2)
            {
                legs = "perlino_legs";
            }
            else if (getPhenotype("silver") != 0)
            {
                legs = "silver_legs";
            }
            else
            {
                legs = "bay_legs";
            }
        }
        else
        {
            // TODO: red-based dun legs
        }
        return legs;
    }

    public String getGray()
    {
        // TODO
        return null;
    }

    public String getGrayMane(String gray)
    {
        // TODO
        return null;
    }

    public String getFaceMarking()
    {
        if (getPhenotype("white_suppression") != 0)
        {
            return null;
        }

        if (getPhenotype("draft_sabino") != 0
            || getPhenotype("W20") == 2)
        {
            return "blaze";
        }

        int face_marking = 0;
        int random = Math.abs(getHorseVariant("random"));
        if (getPhenotype("tobiano") == 2)
        {
            // 1/4 chance none, 1/2 chance star, 1/8 chance strip, 1/8 for blaze
            if (random % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 2 + ((random >> 2) % 2);
            }
            random >>= 3;
        }
        else if (getPhenotype("tobiano") != 0)
        {
            // 1/4 chance star, 1/8 chance strip, the rest none
            if (random % 4 == 0)
            {
                face_marking += 1 + ((random >> 2) % 2);
            }
            random >>= 3;
        }

        if (getPhenotype("flashy_white") != 0)
        {
            // 1/2 chance blaze, 1/4 chance strip, 1/4 chance star
            if (random % 2 == 0)
            {
                face_marking += 3;
            }
            else
            {
                face_marking += 1 + ((random >> 1) % 2);
            }
            random >>= 2;
        }

        assert getPhenotype("W20") != 2;
        if (getPhenotype("W20") == 1)
        {
            // 1/2 chance strip, 1/4 chance star, 1/8 chance blaze or none
            if (random % 2 == 0)
            {
                face_marking += 2;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 2) % 2 == 0)
            {
                face_marking += 3;
            }
            random >>= 3;
        }

        if (getPhenotype("markings") != 0)
        {
            // 1/2 chance strip, 1/4 chance star, 1/4 chance blaze
            if (random % 2 == 0)
            {
                face_marking += 2;
            }
            else
            {
                face_marking += 1 + 2 * ((random >> 1) % 2);
            }
            random >>= 2;
        }

        if (getPhenotype("half-socks") != 0)
        {
            face_marking += random % 8 == 0? 1 : 0;
            random >>= 3;
        }

        if (getPhenotype("strip") != 0)
        {
            // 1/2 chance strip, 1/4 chance star, 1/8 chance blaze or none
            if (random % 2 == 0)
            {
                face_marking += 2;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 2) % 2 == 0)
            {
                face_marking += 3;
            }
            random >>= 3;
        }
        else if (getPhenotype("star") != 0)
        {
            // 1/4 chance strip, 1/2 chance star, 1/4 chance none
            if (random % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 2;
            }
            random >>= 2;
        }

        if (getPhenotype("white_boost") != 0)
        {
            // 1/2 chance star, 1/2 chance none
            face_marking += random % 2;
            random >>= 1;
        }
        switch (face_marking)
        {
            case 0:
                return null;
            case 1:
                return "star";
            case 2:
                return "strip";
            case 3:
                return "blaze";
            default:
                return "blaze";
        }
    }

    public String getPinto()
    {
        if (getPhenotype("white") == 1)
        {
            return "white";
        }
        String pinto = null;
        int tobiano = getPhenotype("tobiano");
        int sabino = getPhenotype("sabino1");
        int splash = getPhenotype("splash");
        int frame = getPhenotype("frame");

        if (tobiano != 0)
        {
            if (frame == 1)
            {
                if (splash == 2)
                {
                    pinto = "medicine_hat";
                }
                else
                {
                    pinto = "war_shield";
                }
            }
            else
            {
                pinto = "tobiano";
            }
        }
        else if (frame == 1)
        {
            pinto = "frame";
        }
        else if (splash == 2)
        {
            pinto = "splash";
        }
        else if (sabino == 1)
        {
            pinto = "sabino";
        }
        else if (getPhenotype("W20") == 2 || getPhenotype("draft_sabino") != 0)
        {
            pinto = "stockings";
        }
        return pinto;
    }

    public String getLeopard()
    {
        if (getPhenotype("leopard") == 0)
        {
            return null;
        }
        int patn = getPhenotype("PATN");
        if (patn == 0)
        {
            return "varnish_roan";
        }
        if (getPhenotype("leopard") == 1)
        {
            // TODO: different coverage based on the value of patn
            return "leopard";
        }
        else
        {
            // TODO: different coverage based on the value of patn
            return "fewspot";
        }
    }

    @SideOnly(Side.CLIENT)
    private void setLegMarkings()
    {
        // Skip when the legs are going to be white anyway
        if (getPhenotype("tobiano") != 0 
            || getPhenotype("splash") ==2)
        {
            return;
        }
        // TODO
        String left_front_leg = null;
        String right_front_leg = null;
        String left_back_leg = null;
        String right_back_leg = null;
        this.horseTexturesArray[8] = fixPath("pinto", left_front_leg);
        this.horseTexturesArray[9] = fixPath("pinto", right_front_leg);
        this.horseTexturesArray[10] = fixPath("pinto", left_back_leg);
        this.horseTexturesArray[11] = fixPath("pinto", right_back_leg);
    }

    @SideOnly(Side.CLIENT)
    private void setHorseTexturePaths()
    {
        String base_texture = getBaseTexture();

        String roan = getPhenotype("roan") != 0? "roan" : null;
        String face_marking = getFaceMarking();
        String sooty = getSooty(base_texture);
        String leopard = getLeopard();
        String mealy = getMealy();
        String legs = getLegs();
        String gray = getGray();
        String gray_mane = getGrayMane(gray);
        
        String pinto = getPinto();
        if (pinto == "white")
        {
            sooty = null;
            roan = null;
            face_marking = null;
            base_texture = null;
            leopard = null;
            mealy = null;
        }
        else
        {
            setLegMarkings();
        }

        ItemStack armorStack = this.dataManager.get(HORSE_ARMOR_STACK);
        String texture = !armorStack.isEmpty() ? armorStack.getItem().getHorseArmorTexture(this, armorStack) : HorseArmorType.getByOrdinal(this.dataManager.get(HORSE_ARMOR)).getTextureName(); //If armorStack is empty, the server is vanilla so the texture should be determined the vanilla way

        /*
        // For testing
        gray_mane = rand.nextInt() % 2 == 0? "gray_mane" : "black_mane";
        base_texture = "light_gray";
        sooty = rand.nextInt() % 2 == 0? "sooty_dappled_dark" : "sooty_dappled";
        */
        this.horseTexturesArray[0] = fixPath("base", base_texture);
        this.horseTexturesArray[1] = fixPath("sooty",  sooty);
        this.horseTexturesArray[2] = fixPath("mealy", mealy);
        this.horseTexturesArray[3] = fixPath("legs", legs);
        this.horseTexturesArray[4] = fixPath("roan", roan);
        this.horseTexturesArray[5] = fixPath("roan", gray);
        this.horseTexturesArray[6] = fixPath("roan", gray_mane);
        this.horseTexturesArray[7] = fixPath("pinto", face_marking);
        this.horseTexturesArray[12] = fixPath("leopard", leopard);
        this.horseTexturesArray[13] = fixPath("pinto", pinto);
        this.horseTexturesArray[14] = texture;

        String base_abv = base_texture == null? "" : base_texture;
        String sooty_abv = sooty == null? "" : sooty;
        String mealy_abv = mealy == null? "" : mealy;
        String legs_abv = legs == null? "" : legs;
        String roan_abv = roan == null? "" : roan;
        String gray_abv = gray == null? "" : gray;
        String gray_mane_abv = gray_mane == null? "" : gray_mane;
        String face_marking_abv = face_marking == null? "" : face_marking;
        String leg_marking_abv = 
            (horseTexturesArray[5] == null? "-" : horseTexturesArray[5]) 
            + (horseTexturesArray[6] == null? "-" : horseTexturesArray[6]) 
            + (horseTexturesArray[7] == null? "-" : horseTexturesArray[7]) 
            + (horseTexturesArray[8] == null? "-" : horseTexturesArray[8]);
        String pinto_abv = pinto == null? "" : pinto;
        String leopard_abv = leopard == null? "" : leopard;
        this.texturePrefix = "horse/cache_" + base_abv + sooty_abv + mealy_abv 
            + roan_abv + gray_abv + gray_mane_abv + face_marking_abv 
            + leg_marking_abv + leopard_abv + pinto_abv + texture;
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

    private void useGeneticAttributes()
    {
        if (HorseConfig.useGeneticStats)
        {
            // Default horse health ranges from 15 to 30, but ours goes from
            // 15 to 31
            float maxHealth = 15.0F + getStat("health") * 0.5F;
            // Vanilla horse speed ranges from 0.1125 to 0.3375, as does ours
            double movementSpeed = 0.1125D + getStat("speed") * (0.225D / 32.0D);
            // Vanilla horse jump strength ranges from 0.4 to 1.0, as does ours
            double jumpStrength = 0.4D + getStat("jump") * (0.6D / 32.0D);

            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(movementSpeed);
            this.getEntityAttribute(JUMP_STRENGTH).setBaseValue(jumpStrength);
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
        if ((!this.world.isRemote || true)
            && this.getPhenotype("frame") == 2
            && this.ticksExisted > 80)
        {
            if (!this.isPotionActive(MobEffects.POISON))
            {
                this.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 3));
            }
            if (this.getHealth() < 2)
            {
                this.addPotionEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 3));
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
    private int getRandomGenes(int n, int type)
    {
        int result = 0;
        int random = 0;
        String out = "";
        for (String gene : genes)
        {
            if (getGenePos(gene) / 32 != type)
            {
                continue;
            }

            random = this.rand.nextInt();
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
            result |= next << (getGenePos(gene) % 32) << (n * getGeneSize(gene));
        }

        return result;
    }

    private int getRandomGenericGenes(int n, int data)
    {
        int rand = this.rand.nextInt();
        int answer = 0;
        for (int i = 0; i < 16; i++)
        {
            if (rand % 2 == 0)
            {
                answer += (data & (1 << (2 * i))) << n;
            }
            else 
            {
                answer += (data & (1 << (2 * i + 1))) >> 1 - n;
            }
            rand >>= 1;
        }
        return answer;
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

            int mother = this.getRandomGenes(1, 0);
            int father = entityhorse.getRandomGenes(0, 0);
            int i = mother | father;
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "0");

            mother = this.getRandomGenes(1, 1);
            father = entityhorse.getRandomGenes(0, 1);
            i = mother | father;
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "1");


            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(rand.nextInt(), "2");
            mother = this.getRandomGenes(1, 2);
            father = entityhorse.getRandomGenes(0, 2);
            i = mother | father;
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "2");

            // speed, health, and jump
            mother = getRandomGenericGenes(1, getHorseVariant("speed"));
            father = entityhorse.getRandomGenericGenes(0, entityhorse.getHorseVariant("speed"));
            i = mother | father;
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "speed");

            mother = getRandomGenericGenes(1, getHorseVariant("health"));
            father = entityhorse.getRandomGenericGenes(0, entityhorse.getHorseVariant("health"));
            i = mother | father;
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "health");

            mother = getRandomGenericGenes(1, getHorseVariant("jump"));
            father = entityhorse.getRandomGenericGenes(0, entityhorse.getHorseVariant("jump"));
            i = mother | father;
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "jump");

            i =  this.rand.nextInt();
            ((EntityHorseFelinoid)abstracthorse).setHorseVariant(i, "random");

            /* Print debugging information. */
            System.out.print("Mating:\n    Variant: " 
                + Integer.toBinaryString(this.getHorseVariant("0")) + ", " 
                + Integer.toBinaryString(this.getHorseVariant("1")) 
                + "\n    Color: " + this.getBaseTexture() 
                + "\n\n    Variant: " 
                + Integer.toBinaryString(entityhorse.getHorseVariant("0")) 
                + ", " + Integer.toBinaryString(entityhorse.getHorseVariant("1")) 
                + "\n    Color: " + entityhorse.getBaseTexture() + "\n\n    "
                + "Baby Variant: " 
                + Integer.toBinaryString(((EntityHorseFelinoid)abstracthorse).getHorseVariant("0"))
                + ", " + Integer.toBinaryString(((EntityHorseFelinoid)abstracthorse).getHorseVariant("1"))
                + "\n    Color: " 
                + ((EntityHorseFelinoid)abstracthorse).getBaseTexture() 
                + "\n" + Integer.toString(((EntityHorseFelinoid)abstracthorse).getPhenotype("dun")) 
                + "\n");
                //test();
        }
        // Dominant white is homozygous lethal early in pregnancy. No child
        // is born.
        if (((EntityHorseFelinoid)abstracthorse).getPhenotype("dominant_white")
                == 2)
        {
            return null;
        }

        this.setOffspringAttributes(ageable, abstracthorse);
        ((EntityHorseFelinoid)abstracthorse).useGeneticAttributes();
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
            setGene("extension", (i & 1) << (n * getGeneSize("extension")));
            i >>= 1;
            setGeneRandom("gray", n, 20, 0);
            int dun = (this.rand.nextInt() % 7 == 0? 2 : 0) + (i % 4 == 0? 1: 0);
            setGene("dun", dun << (n * getGeneSize("dun")));
            i >>= 2;

            int ag = i % 8;
            System.out.println(i);
            int agouti = ag == 0? 3 : (ag < 4? 2 : (ag == 4? 1: 0));
            setGene("agouti", agouti << (n * getGeneSize("agouti")));
            i >>= 3;

            setGeneRandom("silver", n, 32, 0);
            setGeneRandom("cream", n, 32, 0);
            setGeneRandom("liver", n, 3, 1);
            setGeneRandom("flaxen1", n, 6, 1);
            setGeneRandom("flaxen2", n, 6, 1);

            setGene("dapple", (i % 2) << (n * getGeneSize("dapple")));
            i >>= 1;

            setGeneRandom("sooty1", n, 8, 1);
            setGeneRandom("sooty2", n, 8, 1);
            setGeneRandom("sooty3", n, 4, 1);
            setGeneRandom("mealy1", n, 4, 1);
        }
        else if (type == "1")
        {
            int i = this.rand.nextInt();

            setGeneRandom("mealy2", n, 4, 1);
            setGeneRandom("mealy3", n, 4, 1);
            setGeneRandom("white_suppression", n, 32, 0);

            int kit = i % 4 == 0? (((i >> 2) % 8) + 8) % 8 
                                : (((i >> 2) % 16) + 16) % 16;
            setGene("KIT", kit << (n * getGeneSize("KIT")));
            // Homozygote dominant whites will be replaced with heterozygotes
            if (getPhenotype("dominant_white") == 2)
            {
                setGene("KIT", 15);
            }
            i >>= 6;

            setGeneRandom("frame", n, 32, 0);
            // Replace lethal white overos with heterozygotes
            if (getPhenotype("frame") == 2)
            {
                setGene("frame", 1);
            }
            setGeneRandom("splash", n, 16, 0);
            setGeneRandom("leopard", n, 32, 0);
            setGeneRandom("PATN1", n, 16, 0);
            setGeneRandom("PATN2", n, 16, 0);
            setGeneRandom("PATN3", n, 16, 0);
            setGeneRandom("gray_suppression", n, 40, 0);
            setGeneRandom("gray_mane", n, 16, 0);
            setGeneRandom("slow_gray1", n, 16, 0);
        }
        else if (type == "2")
        {
            // Initialize any bits currently unused to random values
            setHorseVariant(this.rand.nextInt(), "2");
            int i = this.rand.nextInt();
            setGeneRandom("slow_gray2", n, 16, 0);
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
