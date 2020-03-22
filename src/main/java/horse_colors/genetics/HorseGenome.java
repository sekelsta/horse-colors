package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class HorseGenome extends Genome {

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
    public static final ImmutableList<String> genes = ImmutableList.of(
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
    );

    public static final ImmutableList<String> stats = ImmutableList.of(
        "speed",
        "health",
        "jump"
    );

    public HorseGenome(IGeneticEntity entityIn) {
        super(entityIn);
    }

    @Override
    public ImmutableList<String> listGenes() {
        return genes;
    }

    @Override
    public ImmutableList<String> listStats() {
        return stats;
    }

    /* This returns the number of bits needed to store one allele. */
    @Override
    public int getGeneSize(String gene)
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

    // The MC1R ("extension") gene seems to be associated with white
    // patterning. For now I assume this is caused by MC1R itself,
    // but if it turns out to be a different gene that's just very
    // closely linked, I can change this.
    public boolean hasMC1RWhiteBoost() {
        return isChestnut();
    }

    public boolean isTobiano() {
        return this.hasAllele("KIT", HorseAlleles.KIT_TOBIANO)
            || this.hasAllele("KIT", HorseAlleles.KIT_TOBIANO_W20);
    }

    public boolean isWhite() {
        return this.hasAllele("KIT", HorseAlleles.KIT_DOMINANT_WHITE)
            || this.isLethalWhite()
            || this.isHomozygous("KIT", HorseAlleles.KIT_SABINO1)
            || (this.hasAllele("KIT", HorseAlleles.KIT_SABINO1)
                && this.hasAllele("frame", HorseAlleles.FRAME)
                && this.isTobiano());
    }

    public boolean isLethalWhite() {
        return this.isHomozygous("frame", HorseAlleles.FRAME);
    }

    public boolean isEmbryonicLethal() {
        return this.isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE);
    }

    // with 1/odds probability gets the gene to 0 or 1, whichever common isn't
    public void setGeneRandom(String name, int n, int odds, int common)
    {
            int i = this.entity.getRand().nextInt();
            int rare = common == 0? 1 : 0;
            setNamedGene(name, (i % odds == 0? rare : common) 
                            << (n * getGeneSize(name)));
    }

    public int inheritStats(HorseGenome other, String chromosome) {
            int mother = this.getRandomGenericGenes(1, this.getChromosome(chromosome));
            int father = other.getRandomGenericGenes(0, other.getChromosome(chromosome));
            return mother | father;
    }

    /* This function changes the variant and then puts it back to what it was
    before. */
    private int getRandomVariant(int n, String type)
    {
        int answer = 0;
        int startVariant = getChromosome(type);
/*
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
*/
        answer = getChromosome(type);
        entity.setChromosome(type, startVariant);
        return answer;
    }

    private void randomizeSingleVariant(String variant)
    {
        int i = getRandomVariant(0, variant);
        int j = getRandomVariant(1, variant);
        entity.setChromosome(variant, i | j);
    }

    /* Make the horse have random genetics. */
    public void randomize()
    {
        randomizeSingleVariant("0");
        randomizeSingleVariant("1");
        randomizeSingleVariant("2");

        // Replace lethal white overos with heterozygotes
        if (isHomozygous("frame", HorseAlleles.FRAME))
        {
            setNamedGene("frame", 1);
        }

        // Homozygote dominant whites will be replaced with heterozygotes
        if (isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE))
        {
            setNamedGene("KIT", 15);
        }

        for (String stat : this.listStats()) {
            entity.setChromosome(stat, this.entity.getRand().nextInt());
        }
        entity.setChromosome("random", this.entity.getRand().nextInt());
    }

    private String getAbv(String s) {
        int i = s.lastIndexOf("/");
        if (i > -1) {
            s = s.substring(i + 1);
        }
        if (s.endsWith(".png")) {
            s = s.substring(0, s.length() - 4);
        }
        return s;
    }

    private String getAbv(Layer layer) {
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
    public String humanReadableNamedGenes() {
        return "to do";
    }
    public String humanReadableStats() {
        return "to do";
    }

    @OnlyIn(Dist.CLIENT)
    public void setTexturePaths()
    {
        this.textureLayers = new ArrayList();
        Layer red = HorseColorCalculator.getRedBody(this);
        HorseColorCalculator.setDun(this, red);
        Layer black = HorseColorCalculator.getBlackBody(this);
        HorseColorCalculator.setDun(this, black);
        this.textureLayers.add(red);
        this.textureLayers.add(HorseColorCalculator.getRedManeTail(this));
        this.textureLayers.add(black);
        this.textureLayers.add(HorseColorCalculator.getBlackManeTail(this));
        this.textureLayers.add(HorseColorCalculator.getNose(this));
        this.textureLayers.add(HorseColorCalculator.getGray(this));


/*
        this.textureLayers[6] = new Layer();
        this.textureLayers[7] = new Layer();
        this.textureLayers[8] = new Layer();
        this.textureLayers[9] = new Layer();
        this.textureLayers[10] = new Layer();
        this.textureLayers[11] = new Layer();
        this.textureLayers[12] = new Layer();
        this.textureLayers[13] = new Layer();
        this.textureLayers[14] = new Layer();
        this.textureLayers[15] = new Layer();
        this.textureLayers[16] = new Layer();
        this.textureLayers[17] = new Layer();

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

        this.textureLayers[8].name = HorseColorCalculator.fixPath("sooty", sooty);
        this.textureLayers[9].name = HorseColorCalculator.fixPath("legs", legs);
        this.textureLayers[10].name = HorseColorCalculator.fixPath("roan", roan);
        this.textureLayers[11].name = HorseColorCalculator.fixPath("roan", gray_mane);
        this.textureLayers[12].name = HorseColorCalculator.fixPath("face", face_marking);
        this.textureLayers[13].name = HorseColorCalculator.fixPath("socks", leg_markings[0]);
        this.textureLayers[14].name = HorseColorCalculator.fixPath("socks", leg_markings[1]);
        this.textureLayers[15].name = HorseColorCalculator.fixPath("socks", leg_markings[2]);
        this.textureLayers[16].name = HorseColorCalculator.fixPath("socks", leg_markings[3]);
        this.textureLayers[17].name = HorseColorCalculator.fixPath("pinto", pinto);

*/
        Layer common = new Layer();
        common.name = HorseColorCalculator.fixPath("", "common");
        this.textureLayers.add(common);

        this.textureCacheName = "horse/cache_";

        for (int i = 0; i < textureLayers.size(); ++i) {
            this.textureCacheName += getAbv(this.textureLayers.get(i));
        }
    }


    public void setChildGenes(HorseGenome other, IGeneticEntity childEntity) {

        int mother = this.getRandomGenes(1, 0);
        int father = other.getRandomGenes(0, 0);
        int i = mother | father;
        childEntity.setChromosome("0", i);

        mother = this.getRandomGenes(1, 1);
        father = other.getRandomGenes(0, 1);
        i = mother | father;
        childEntity.setChromosome("1", i);


        childEntity.setChromosome("2", rand.nextInt());
        mother = this.getRandomGenes(1, 2);
        father = other.getRandomGenes(0, 2);
        i = mother | father;
        childEntity.setChromosome("2", i);

        for (String stat : this.listStats()) {
            int val = inheritStats(other, stat);
            childEntity.setChromosome(stat, val);
        }
        childEntity.getGenes().mutate();
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
                return (getNamedGene(name) & 1) + (getNamedGene(name) >> 1);
                
            
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
                if (getNamedGene("agouti") == 1 || getNamedGene("agouti") == 4)
                {
                    return 1;
                }
                int allele = Math.max(getNamedGene("agouti") & 3, getNamedGene("agouti") >> 2);
                return allele == 0? 0 : allele + 1;

            case "dun":
                if (getNamedGene(name) <= 1) 
                {
                    // 0 for ND2 (no dorsal stripe), 1 for ND1/+ (faint 
                    // dorsal stripe), 2 for ND1/ND1 (dorsal stripe), 3 for dun
                    return getNamedGene(name);
                }
                else if (getNamedGene(name) == 4)
                {
                    return 1;
                }
                else if (getNamedGene(name) == 5)
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
                if (getNamedGene("KIT") == (12 << 4) + 12)
                {
                    return 2;
                }
                else
                {
                    return ((getNamedGene("KIT") & 15) == 12) 
                            || ((getNamedGene("KIT") >> 4) == 12)? 1 : 0;
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
                if (getNamedGene("KIT") == (15 << 4) + 15)
                {
                    return 2;
                }
                return ((getNamedGene("KIT") & 15) == 15
                        || (getNamedGene("KIT") >> 4) == 15)? 1 : 0;
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
}
