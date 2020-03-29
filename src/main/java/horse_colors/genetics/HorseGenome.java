package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

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
        "white_hindlegs",
        "gray_melanoma"
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
            case "white_hindlegs":
            case "gray_melanoma": return 1;
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
            || this.hasAllele("dun", HorseAlleles.DUN_UNUSED);
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

    public boolean showsLegMarkings() {
        return !isWhite() && !isTobiano();
    }

    public boolean isDappleInclined() {
        return this.hasAllele("dapple", 1);
    }

    public boolean isLethalWhite() {
        return this.isHomozygous("frame", HorseAlleles.FRAME);
    }

    public boolean isEmbryonicLethal() {
        return this.isHomozygous("KIT", HorseAlleles.KIT_DOMINANT_WHITE);
    }

    public int getSootyLevel() {
        // sooty1 and 2 dominant, 3 recessive
        return 1 + getMaxAllele("sooty1") + getMaxAllele("sooty2") 
                        - getMaxAllele("sooty3");
    }

    public int getSlowGrayLevel() {
        int base = isHomozygous("gray", HorseAlleles.GRAY)? -2 : 0;
        return base + countAlleles("slow_gray1", 1)
                + getAllele("slow_gray2", 1) + getMaxAllele("gray_mane");
    }

    public float getGrayHealthLoss() {
        int base = countAlleles("gray", HorseAlleles.GRAY);
        if (isHomozygous("gray_melanoma", 0)) {
            base -= 1;
        }
        return Math.max(0f, base);
    }

    public float getSilverHealthLoss() {
        if (isHomozygous("silver", HorseAlleles.SILVER)) {
            return 1f;
        }
        else if (hasAllele("silver", HorseAlleles.SILVER)) {
            return 0.5f;
        }
        else {
            return 0;
        }
    }

    public float getDeafHealthLoss() {
        int white = HorseColorCalculator.getFaceWhiteLevel(this);
        if (white > 18) {
            return 1f;
        }
        else {
            return 0f;
        }
    }

    public float getBaseHealth() {
        System.out.println(HorseConfig.COMMON);
        System.out.println(HorseConfig.COMMON.enableHealthEffects);
        System.out.println(HorseConfig.COMMON.enableHealthEffects.get());
        if (HorseConfig.COMMON.enableHealthEffects.get()) {
            return -getGrayHealthLoss() - getSilverHealthLoss() - getDeafHealthLoss();
        }
        else {
            return 0;
        }
    }

    // A special case because it has two different alleles
    public int countW20() {
        return countAlleles("KIT", HorseAlleles.KIT_W20) 
                + countAlleles("KIT", HorseAlleles.KIT_TOBIANO_W20);
    }

    public int inheritStats(HorseGenome other, String chromosome) {
            int mother = this.getRandomGenericGenes(1, this.getChromosome(chromosome));
            int father = other.getRandomGenericGenes(0, other.getChromosome(chromosome));
            return mother | father;
    }

    // Distribution should be a series of floats increasing from
    // 0.0 to 1.0, where the probability of choosing allele i is
    // the chance that a random uniform number between 0 and 1
    // is greater than distribution[i-1] but less than distribution[i].
    public int chooseRandomAllele(List<Float> distribution) {
        float n = this.entity.getRand().nextFloat();
        for (int i = 0; i < distribution.size(); ++i) {
            if (n < distribution.get(i)) {
                return i;
            }
        }
        // In case of floating point rounding errors
        return distribution.size() - 1;
    }

    public int chooseRandom(List<Float> distribution) {
        int left = chooseRandomAllele(distribution);
        int right = chooseRandomAllele(distribution);
        // Log 2
        int size = 8 * Integer.BYTES - 1 - Integer.numberOfLeadingZeros(distribution.size());
        // Round up
        if (distribution.size() != 1 << size) {
            size += 1;
        }
        return (left << size) | right;
    }

    public void randomizeNamedGenes() {
        ImmutableList<Float> extension = ImmutableList.of(
            0.125f, 0.25f, 0.375f, 0.5f, // Red
            0.625f, 0.75f, 0.875f, 1.0f  // Black
        );
        setNamedGene("extension", chooseRandom(extension));

        ImmutableList<Float> gray = ImmutableList.of(
            0.95f, // Non-gray
            1.0f   // Gray
        );
        setNamedGene("gray", chooseRandom(gray));

        ImmutableList<Float> dun = ImmutableList.of(
            0.75f,  // Non-dun 2
            0.875f, // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        );
        setNamedGene("dun", chooseRandom(dun));

        ImmutableList<Float> agouti = ImmutableList.of(
            0.375f,     // Black
            0.4375f,    // Seal
            0.5f,       // Brown - same as seal
            0.625f,     // Bay_dark - same as bay
            0.75f,      // Bay
            0.875f,     // Bay_light - same as bay
            0.9375f,    // Bay_wild
            1.0f        // Bay_mealy - same as bay_wild
        );
        setNamedGene("agouti", chooseRandom(agouti));

        ImmutableList<Float> silver = ImmutableList.of(
            31.0f / 32.0f,  // Non-silver
            1.0f            // Silver
        );
        setNamedGene("silver", chooseRandom(silver));

        ImmutableList<Float> cream = ImmutableList.of(
            30f / 32f,  // Non-cream
            0f,         // Non-cream unused
            31f / 32f,  // Pearl
            1f          // Cream
        );
        setNamedGene("cream", chooseRandom(cream));

        ImmutableList<Float> liver = ImmutableList.of(
            0.25f,  // Liver
            1f      // Non-liver
        );
        setNamedGene("liver", chooseRandom(liver));

        ImmutableList<Float> flaxen = ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        );
        setNamedGene("flaxen1", chooseRandom(flaxen));
        setNamedGene("flaxen2", chooseRandom(flaxen));

        ImmutableList<Float> dapple = ImmutableList.of(
            0.5f,   // Non-dapple
            1f      // Dapple
        );
        setNamedGene("dapple", chooseRandom(dapple));

        ImmutableList<Float> sooty = ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        );
        setNamedGene("sooty1", chooseRandom(sooty));
        setNamedGene("sooty2", chooseRandom(sooty));

        ImmutableList<Float> sooty3 = ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        );
        setNamedGene("sooty3", chooseRandom(sooty3));

        ImmutableList<Float> mealy = ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        );
        setNamedGene("mealy1", chooseRandom(mealy));
        setNamedGene("mealy2", chooseRandom(mealy));
        setNamedGene("mealy3", chooseRandom(mealy));

        ImmutableList<Float> white_suppression = ImmutableList.of(
            31f / 32f,  // Non white-suppression
            1f          // White suppression
        );
        setNamedGene("white_suppression", chooseRandom(white_suppression));

        ImmutableList<Float> kit = ImmutableList.of(
            0.4f,   // Wildtype
            0.5f,   // White boost
            0.55f,  // Markings1
            0.6f,   // Markings2
            0.65f,  // Markings3
            0.7f,   // Markings4
            0.75f,  // Markings5
            0.82f,  // W20
            0f,     // Rabicano / Unused
            0.86f,  // Flashy white
            0f,     // Unused
            0.90f,  // Tobiano
            0.94f,  // Sabino1
            0.96f,  // Tobiano + W20
            0.97f,  // Roan
            1.0f    // Dominant white
        );
        setNamedGene("KIT", chooseRandom(kit));

        ImmutableList<Float> frame = ImmutableList.of(
            31f / 32f,  // Non-frame
            1f          // Frame
        );
        setNamedGene("frame", chooseRandom(frame));

        ImmutableList<Float> mitf = ImmutableList.of(
            0.15f,  // SW1
            0.18f,  // SW3
            0.20f,  // SW5
            1.0f    // Wildtype
        );
        setNamedGene("MITF", chooseRandom(mitf));

        ImmutableList<Float> pax3 = ImmutableList.of(
            0.9f,   // Wildtype
            0.94f,  // SW2
            0.98f,  // SW4
            1.0f    // Unused
        );
        setNamedGene("PAX3", chooseRandom(pax3));

        ImmutableList<Float> leopard = ImmutableList.of(
            31f / 32f,  // Non-leopard
            1f          // Leopard
        );
        setNamedGene("leopard", chooseRandom(leopard));

        ImmutableList<Float> patn = ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        );
        setNamedGene("PATN1", chooseRandom(patn));
        setNamedGene("PATN2", chooseRandom(patn));
        setNamedGene("PATN3", chooseRandom(patn));

        ImmutableList<Float> gray_suppression = ImmutableList.of(
            0.975f, // Non gray-suppression
            1f      // Gray suppression
        );
        setNamedGene("gray_suppression", chooseRandom(gray_suppression));

        ImmutableList<Float> gray_mane = ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        );
        setNamedGene("gray_mane", chooseRandom(gray_mane));

        ImmutableList<Float> slow_gray1 = ImmutableList.of(
            0.875f, // Lighter
            1f      // Darker
        );
        setNamedGene("slow_gray1", chooseRandom(slow_gray1));

        ImmutableList<Float> slow_gray2 = ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        );
        setNamedGene("slow_gray2", chooseRandom(slow_gray2));

        ImmutableList<Float> white_star = ImmutableList.of(
            0.75f,  // Less white
            1f      // More white
        );
        setNamedGene("white_star", chooseRandom(white_star));

        ImmutableList<Float> white_legs = ImmutableList.of(
            0.75f,  // Less white
            1f      // More white
        );
        setNamedGene("white_forelegs", chooseRandom(white_legs));
        setNamedGene("white_hindlegs", chooseRandom(white_legs));
    }

    /* Make the horse have random genetics. */
    public void randomize()
    {
        randomizeNamedGenes();

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
    public ArrayList<String> humanReadableNamedGenes() {
        ArrayList<String> list = new ArrayList<String>();
        for (String gene : genes) {
            TranslationTextComponent translation = new TranslationTextComponent(HorseColors.MODID + ".genes.equus." + gene + ".name");
            String s = translation.getFormattedText() + ": ";
            s += getAllele(gene, 0) + ", ";
            s += getAllele(gene, 1);
            list.add(s);
        }
        return list;
    }
    public ArrayList<String> humanReadableStats() {
        ArrayList<String> list = new ArrayList<String>();
        for (String stat : stats) {
            TranslationTextComponent translation = new TranslationTextComponent(HorseColors.MODID + ".stats." + stat);
            String s = translation.getFormattedText();
            s += ": " + this.getStat(stat);
            s += " (";
            int val = this.getChromosome(stat);
            for (int i = 16; i >0; i--) {
                s += (val >>> (2 * i - 1)) & 1;
                s += (val >>> (2 * i - 2)) & 1;
                if (i > 1) {
                    s += " ";
                }
            }
            s += ")";
            list.add(s);
        }
        return list;
    }

    @OnlyIn(Dist.CLIENT)
    public void setTexturePaths()
    {
        this.textureLayers = new ArrayList();
        Layer red = HorseColorCalculator.getRedBody(this);
        Layer black = HorseColorCalculator.getBlackBody(this);
        this.textureLayers.add(red);
        this.textureLayers.add(HorseColorCalculator.getRedManeTail(this));
        this.textureLayers.add(black);
        this.textureLayers.add(HorseColorCalculator.getBlackManeTail(this));
        this.textureLayers.add(HorseColorCalculator.getSooty(this));
        this.textureLayers.add(HorseColorCalculator.getGray(this));
        this.textureLayers.add(HorseColorCalculator.getNose(this));

        if (this.hasAllele("KIT", HorseAlleles.KIT_ROAN)) {
            Layer roan = new Layer();
            roan.name = HorseColorCalculator.fixPath("roan/roan");
            this.textureLayers.add(roan);
        }

        this.textureLayers.add(HorseColorCalculator.getFaceMarking(this));
        if (showsLegMarkings())
        {
            String[] leg_markings = HorseColorCalculator.getLegMarkings(this);
            for (String marking : leg_markings) {
                Layer layer = new Layer();
                layer.name = marking;
                this.textureLayers.add(layer);
            }
        }

        this.textureLayers.add(HorseColorCalculator.getPinto(this));
/*
        String legs = HorseColorCalculator.getLegs(this);
        String gray_mane = HorseColorCalculator.getGrayMane(this);
      
        this.textureLayers[9].name = HorseColorCalculator.fixPath("legs", legs);
        this.textureLayers[11].name = HorseColorCalculator.fixPath("roan", gray_mane);
*/
        Layer common = new Layer();
        common.name = HorseColorCalculator.fixPath("common");
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
}
