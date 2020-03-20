package sekelsta.horse_colors.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;

import sekelsta.horse_colors.util.HorseAlleles;
import sekelsta.horse_colors.config.HorseConfig;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.renderer.ComplexLayeredTexture;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;
import sekelsta.horse_colors.util.HorseAlleles;
import sekelsta.horse_colors.util.HorseColorCalculator;


public abstract class AbstractHorseGenetic extends AbstractHorseEntity {

    protected AbstractHorseGenetic(EntityType<? extends AbstractHorseGenetic> type, World worldIn) {
        super(type, worldIn);
    }

    protected String texturePrefix;

    protected static final DataParameter<Integer> HORSE_VARIANT = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT2 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_VARIANT3 = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_SPEED = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_JUMP = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> HORSE_HEALTH = EntityDataManager.<Integer>createKey(AbstractHorseGenetic.class, DataSerializers.VARINT);

    // See the function that sets this to find what each of  the layers are for
    protected final Layer[] horseTexturesArray = new Layer[19];

    @Override
    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(HORSE_VARIANT, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT2, Integer.valueOf(0));
        this.dataManager.register(HORSE_VARIANT3, Integer.valueOf(0));
        this.dataManager.register(HORSE_SPEED, Integer.valueOf(0));
        this.dataManager.register(HORSE_HEALTH, Integer.valueOf(0));
        this.dataManager.register(HORSE_JUMP, Integer.valueOf(0));
    }

    /**
     *  Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getHorseVariant("0"));
        compound.putInt("Variant2", this.getHorseVariant("1"));
        compound.putInt("Variant3", this.getHorseVariant("2"));
        compound.putInt("SpeedGenes", this.getHorseVariant("speed"));
        compound.putInt("JumpGenes", this.getHorseVariant("jump"));
        compound.putInt("HealthGenes", this.getHorseVariant("health"));
    }
    /**
     *  Protected helper method to read subclass entity data from NBT.
     */

    @Override
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setHorseVariant(compound.getInt("Variant"), "0");
        this.setHorseVariant(compound.getInt("Variant2"), "1");
        this.setHorseVariant(compound.getInt("Variant3"), "2");
        this.setHorseVariant(compound.getInt("SpeedGenes"), "speed");
        this.setHorseVariant(compound.getInt("JumpGenes"), "jump");
        this.setHorseVariant(compound.getInt("HealthGenes"), "health");
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
            default:
                System.out.print("Unrecognized horse data for getting: " 
                                + type + "\n");
                return 0;
        }
        
    }

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
    undersides. It's a polygenetic trait. */
    public static final String[] genes = new String[] {
        "extension", 
        "agouti", 
        "dun", 
        "gray", 
        "cream", 
        "silver", 
        "liver", 
        "flaxen1", 
        "flaxen2", 
        "dapple", 
        "sooty1", 
        "sooty2", 
        "sooty3", 
        "mealy1", 
        "mealy2", 
        "mealy3", 
        "white_suppression", 
        "KIT", 
        "frame", 
        "MITF", 
        "PAX3", 
        "leopard",
        "PATN1", 
        "PATN2", 
        "PATN3", 
        "gray_suppression",
        "gray_mane", 
        "slow_gray1", 
        "slow_gray2",
        "white_star",
        "white_forelegs",
        "white_hindlegs"
        // TODO
        // mealy switch (no light points, in donkeys)
        // gaitkeeper
        // unicorn
        // ivory (for donkeys)
        // some gait decider genes
        // some unicorn trait genes
    };



    public static int getGenePos(String name)
    {
        int i = 0;
        for (String gene : genes)
        {
            int next = (i + (2 * getGeneSize(gene)));
            // Special case to keep each gene completely on the same int
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

            case "extension":
            case "agouti": return 3;

            case "MITF":
            case "PAX3":
            case "cream":
            case "dun": return 2;

            case "gray":
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
            case "leopard":
            case "PATN1":
            case "PATN2":
            case "PATN3":
            case "gray_suppression":
            case "gray_mane":
            case "slow_gray1":
            case "slow_gray2":
            case "white_star":
            case "white_forelegs":
            case "white_hindlegs": return 1;
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

    public void setAllele(String name, int n, int v)
    {
        int other = getAllele(name, 1 - n);
        int size = getGeneSize(name);
        setGene(name, (other << ((1 - n) * size)) | (v << (n * size)));
    }

    // Replace the given allele with a random one.
    // It may be the same as before.
    public void mutateAllele(String gene, int n) {
        int size = getGeneSize(gene);
        int v = this.rand.nextInt((int)Math.pow(2, size));
        setAllele(gene, n, v);
    }

    // Will mutate with p probability
    public void mutateAlleleChance(String gene, int n, double p) {
        if (this.rand.nextDouble() < p) {
            mutateAllele(gene, n);
        }
    }

    // Get a number where each binary digit has p
    // probability of being a 1. 
    public int mutateIntMask(double p) {
        int mask = 0;
        if (this.rand.nextDouble() < p) {
            mask++;
        }
        for (int i = 1; i < Integer.SIZE; ++i) {
            mask <<= 1;
            if (this.rand.nextDouble() < p) {
                mask++;
            }
        }
        return mask;
    }


    public void mutateStat(String name, double p) {
        // xor with an int where each digit has a p / 2 chance of being 1
        // This is equivalent to picking a random replacement with p probability
        // because half the time that would pick the same value as before
        setHorseVariant(getHorseVariant(name) ^ mutateIntMask(p / 2), name);
    }

    public void mutate() {
        double p = HorseConfig.Common.mutationChance.get();
        for (String gene : genes) {
            mutateAlleleChance(gene, 0, p);
            mutateAlleleChance(gene, 1, p);
        }
        mutateStat("health", p);
        mutateStat("speed", p);
        mutateStat("jump", p);
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


    public boolean hasAllele(String name, int allele)
    {
        return getAllele(name, 0) == allele || getAllele(name, 1) == allele;
    }

    public int getMaxAllele(String name)
    {
        return Math.max(getAllele(name, 0), getAllele(name, 1));
    }

    public boolean isHomozygous(String name, int allele)
    {
        return  getAllele(name, 0) == allele && getAllele(name, 1) == allele;
    }

    public int countAlleles(String gene, int allele) {
        int count = 0;
        if (getAllele(gene, 0) == allele) {
            count++;
        }
        if (getAllele(gene, 1) == allele) {
            count++;
        }
        return count;
    }

    public boolean isChestnut()
    {
        int e = getMaxAllele("extension");
        return e == HorseAlleles.E_RED 
                || e == HorseAlleles.E_RED2
                || e == HorseAlleles.E_RED3
                || e == HorseAlleles.E_RED4;
    }

    public boolean hasCream() {
        return this.hasAllele("cream", HorseAlleles.CREAM);
    }

    public boolean isDoubleCream() {
        return this.isHomozygous("cream", HorseAlleles.CREAM);
    }

    public boolean isSilver() {
        return this.hasAllele("silver", HorseAlleles.SILVER);
    }

    public boolean isGray() {
        return this.hasAllele("gray", HorseAlleles.GRAY);
    }

    public boolean isDun() {
        return this.hasAllele("dun", HorseAlleles.DUN)
            || this.hasAllele("dun", HorseAlleles.DUN_IBERIAN);
    }

    public abstract boolean fluffyTail();
    public abstract boolean longEars();
    public abstract boolean thinMane();

    // The MC1R ("extension") gene seems to be associated with white
    // patterning. For now I assume this is caused by MC1R itself,
    // but if it turns out to be a different gene that's just very
    // closely linked, I can change this.
    public boolean hasMC1RWhiteBoost() {
        return isChestnut();
    }

    public String getAbv(String s) {
        int i = s.lastIndexOf("/");
        if (i > -1) {
            s = s.substring(i + 1);
        }
        if (s.endsWith(".png")) {
            s = s.substring(0, s.length() - 4);
        }
        return s;
    }

    public String getAbv(Layer layer) {
        if (layer == null || layer.name == null) {
            return "";
        }        
        String abv = getAbv(layer.name);
        if (layer.mask != null) {
            abv += "-" + getAbv(layer.mask);
        }
        abv += "-" + Integer.toHexString(layer.alpha);
        abv += Integer.toHexString(layer.red);
        abv += Integer.toHexString(layer.green);
        abv += Integer.toHexString(layer.blue) + "_";
        if (layer.next != null) {
            abv += ".-" + getAbv(layer.next) + "-.";
        }
        return abv;
    }

    protected void resetTexturePrefix()
    {
        this.texturePrefix = null;
    }

    @OnlyIn(Dist.CLIENT)
    private void setHorseTexturePaths()
    {
        Layer red = HorseColorCalculator.getRedBody(this);
        HorseColorCalculator.setDun(this, red);
        Layer black = HorseColorCalculator.getBlackBody(this);
        HorseColorCalculator.setDun(this, black);
        this.horseTexturesArray[0] = red;
        this.horseTexturesArray[1] = HorseColorCalculator.getRedManeTail(this);
        this.horseTexturesArray[2] = HorseColorCalculator.getNose(this);
        this.horseTexturesArray[3] = black;
        this.horseTexturesArray[4] = HorseColorCalculator.getBlackManeTail(this);
        this.horseTexturesArray[5] = HorseColorCalculator.getGray(this);



        this.horseTexturesArray[6] = new Layer();
        this.horseTexturesArray[7] = new Layer();
        this.horseTexturesArray[8] = new Layer();
        this.horseTexturesArray[9] = new Layer();
        this.horseTexturesArray[10] = new Layer();
        this.horseTexturesArray[11] = new Layer();
        this.horseTexturesArray[12] = new Layer();
        this.horseTexturesArray[13] = new Layer();
        this.horseTexturesArray[14] = new Layer();
        this.horseTexturesArray[15] = new Layer();
        this.horseTexturesArray[16] = new Layer();
        this.horseTexturesArray[17] = new Layer();
        this.horseTexturesArray[18] = new Layer();

        String roan = hasAllele("KIT", HorseAlleles.KIT_ROAN)? "roan" : null;
        String face_marking = HorseColorCalculator.getFaceMarking(this);
        String sooty = HorseColorCalculator.getSooty(this);
        String legs = HorseColorCalculator.getLegs(this);
        String gray_mane = HorseColorCalculator.getGrayMane(this);
        String[] leg_markings = new String[4];
        
        String pinto = HorseColorCalculator.getPinto(this);
        if (showsLegMarkings())
        {
            leg_markings = HorseColorCalculator.getLegMarkings(this);
        }
/*
        this.horseTexturesArray[8].name = HorseColorCalculator.fixPath("sooty", sooty);
        this.horseTexturesArray[9].name = HorseColorCalculator.fixPath("legs", legs);
        this.horseTexturesArray[10].name = HorseColorCalculator.fixPath("roan", roan);
        this.horseTexturesArray[11].name = HorseColorCalculator.fixPath("roan", gray_mane);
        this.horseTexturesArray[12].name = HorseColorCalculator.fixPath("face", face_marking);
        this.horseTexturesArray[13].name = HorseColorCalculator.fixPath("socks", leg_markings[0]);
        this.horseTexturesArray[14].name = HorseColorCalculator.fixPath("socks", leg_markings[1]);
        this.horseTexturesArray[15].name = HorseColorCalculator.fixPath("socks", leg_markings[2]);
        this.horseTexturesArray[16].name = HorseColorCalculator.fixPath("socks", leg_markings[3]);
        this.horseTexturesArray[17].name = HorseColorCalculator.fixPath("pinto", pinto);
*/

        Layer common = new Layer();
        common.name = HorseColorCalculator.fixPath("", "common");
        this.horseTexturesArray[18] = common;

        this.texturePrefix = "horse/cache_";

        for (int i = 0; i < 18; ++i) {
            this.texturePrefix += getAbv(this.horseTexturesArray[i]);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public String getHorseTexture()
    {
        if (this.texturePrefix == null)
        {
            this.setHorseTexturePaths();
        }

        return this.texturePrefix;
    }

    @OnlyIn(Dist.CLIENT)
    public ComplexLayeredTexture.Layer[] getVariantTexturePaths()
    {
        if (this.texturePrefix == null)
        {
            this.setHorseTexturePaths();
        }

        return this.horseTexturesArray;
    }
    /* Argument is the number of genewidths to the left each gene should be
    shifted. */
    protected int getRandomGenes(int n, int type)
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

    protected int getRandomGenericGenes(int n, int data)
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

    public boolean otherCanMate(AbstractHorseEntity other) {
        // This is the same as calling other.canMate() but doesn't require
        // reflection
        return !other.isBeingRidden() && !other.isPassenger() && other.isTame() && !other.isChild() && other.getHealth() >= other.getMaxHealth() && other.isInLove();
    }


    public int getPhenotype(String name)
    {
        switch(name)
        {
            /* Simple dominant or recessive genes. */
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
                return getMaxAllele(name);

            /* Incomplete dominant. */
            case "leopard":
            case "gray":
            case "cream":
            case "frame":
            case "PATN1":
            case "gray_mane":
            case "slow_gray2":
            case "white_star":
            case "white_forelegs":
            case "white_hindlegs":
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
            case "splash":
                // TODO
                return countAlleles("MITF", HorseAlleles.MITF_SW1);

            /* Genes with multiple alleles. */
            case "extension":
                return Math.max(getAllele(name, 0), getAllele(name, 1));
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
               1 to 6: contribute to white markings
               7: W20 (strip and socks typical as heterozygous, 
                    when homozygous, irregular draft sabino with some belly white)
               8: reserved in case I add rabicano to KIT
               9: flashy white (tends towards stockings and blaze)
               10: wildtype for now
               11: tobiano
               12: sabino1
               13: tobiano + W20
               14: roan
               15: white
            */
            // W20 is incomplete dominant
            case "W20":
                return countAlleles("KIT", HorseAlleles.KIT_W20) 
                    + countAlleles("KIT", HorseAlleles.KIT_TOBIANO_W20);
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
                return hasAllele("KIT", HorseAlleles.KIT_ROAN)? 1 : 0;
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
                return base == 0? 0 : base + getPhenotype("W20");
            case "slow_gray":
                // Larger numbers make a darker horse.
                int val = getPhenotype("slow_gray1") + getPhenotype("slow_gray2")
                        + (getPhenotype("gray") == 2? -2 : 0)
                        + (getPhenotype("gray_mane") == 0? 0 : 1);
                return Math.min(Math.max(val, 0), 3);
            case "MITF": return -1;
            case "PAX3": return -1;         
        }
        System.out.println("[horse_colors]: Phenotype for " + name + " not found.");
        return -1;
    }

    public boolean showsLegMarkings()
    {
        return getPhenotype("tobiano") == 0 && getPhenotype("splash") != 2
                && getPhenotype("white") == 0;
    }
}
