package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static final ImmutableList<String> chromosomes = ImmutableList.of("0", "1", "2", "speed", "jump", "health", "random");

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
        HashMap<String, ImmutableList<Float>> map = HorseBreeds.DEFAULT;
        for (String gene : genes) {
            setNamedGene(gene, chooseRandom(map.get(gene)));
        }
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
        abv += "-" + layer.type.toString();
        abv += "-" + Integer.toHexString(layer.alpha);
        abv += Integer.toHexString(layer.red);
        abv += Integer.toHexString(layer.green);
        abv += Integer.toHexString(layer.blue) + "_";
        if (layer.next != null) {
            abv += ".-" + getAbv(layer.next) + "-.";
        }
        // Upper case characters will cause a crash
        return abv.toLowerCase();
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
        HorseColorCalculator.addRedManeTail(this, this.textureLayers);
        this.textureLayers.add(black);
        this.textureLayers.add(HorseColorCalculator.getBlackManeTail(this));
        this.textureLayers.add(HorseColorCalculator.getSooty(this));
        this.textureLayers.add(HorseColorCalculator.getGray(this));
        this.textureLayers.add(HorseColorCalculator.getNose(this));
        this.textureLayers.add(HorseColorCalculator.getHooves(this));

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
        Layer shading = new Layer();
        shading.name = HorseColorCalculator.fixPath("shading");
        shading.type = Layer.Type.SHADE;
        this.textureLayers.add(shading);

        Layer common = new Layer();
        common.name = HorseColorCalculator.fixPath("common");
        this.textureLayers.add(common);

        this.textureCacheName = "horse/cache_";

        for (int i = 0; i < textureLayers.size(); ++i) {
            this.textureCacheName += getAbv(this.textureLayers.get(i));
        }
    }

    public String genesToString() {
        String answer = "";
        for (String chr : chromosomes) {
            answer += String.format("%1$08X", getChromosome(chr));
        }
        return answer;
    }

    public void genesFromString(String s) {
        for (int i = 0; i < chromosomes.size(); ++i) {
            String c = s.substring(8 * i, 8 * (i + 1));
            entity.setChromosome(chromosomes.get(i), (int)Long.parseLong(c, 16));
        }
    }

    public boolean isValidGeneString(String s) {
        if (s.length() != 8 * chromosomes.size()) {
            return false;
        }
        if (!s.matches("[0-9a-fA-F]*")) {
            return false;
        }
        return true;
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
