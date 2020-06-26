package sekelsta.horse_colors.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.renderer.TextureLayer;
import sekelsta.horse_colors.util.Util;

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
        // I'm treating this as the agouti promoter region responsible for 
        // white bellied agouti in mice
        "light_belly",
        "mealy1", 
        "mealy2", 
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
        "slow_gray1", 
        "slow_gray2", 
        "slow_gray3",
        "white_star",
        "white_forelegs",
        "white_hindlegs",
        "gray_melanoma",
        "gray_mane1",
        "gray_mane2",
        "rufous",
        "dense",
        "champagne", // TODO
        "cameo",
        "ivory",
        "donkey_dark",
        "cross",
        "reduced_points",
        "light_legs",
        "less_light_legs",
        "leg_stripes",   // TODO
        "stripe_spacing" // TODO
    );

    public static final ImmutableList<String> genericChromosomes = ImmutableList.of(
        "speed",
        "jump",
        "health",   
        "mhc1", 
        "mhc2",
        "immune" 
    );

    public static final ImmutableList<String> stats = ImmutableList.of(
        "speed1",
        "speed2",
        "speed3",
        "athletics1",
        "athletics2",
        "jump1",
        "jump2",
        "jump3",
        "health1",
        "health2",
        "health3",
        "stamina"
    );

    public static final ImmutableList<String> chromosomes = ImmutableList.of("0", "1", "2", "3", "speed", "jump", "health", "mhc1", "mhc2", "immune", "random");

    public HorseGenome(IGeneticEntity entityIn) {
        super(entityIn);
    }

    public HorseGenome() {
        super();
    }

    @Override
    public ImmutableList<String> listGenes() {
        return genes;
    }

    @Override
    public ImmutableList<String> listGenericChromosomes() {
        return genericChromosomes;
    }

    @Override
    public ImmutableList<String> listStats() {
        return stats;
    }

    @Override
    public List<Genome.Linkage> listLinkages() {
        List<Genome.Linkage> linkages = super.listLinkages();
        // It doesn't matter if some appear twice, the last will be used
        linkages.add(new Genome.Linkage("extension", 0.015f));
        linkages.add(new Genome.Linkage("KIT"));

        linkages.add(new Genome.Linkage("agouti", 0.0f));
        linkages.add(new Genome.Linkage("light_belly"));
        return linkages;
    }

    /* For named genes, this returns the number of bits needed to store one allele. 
    For stats, this returns the number of genes that contribute to the stat. */
    @Override
    public int getGeneSize(String gene)
    {
        switch(gene) 
        {
            case "KIT":
            case "speed1":
            case "speed2":
            case "speed3":
            case "athletics1":
            case "athletics2":
            case "jump1":
            case "jump2":
            case "jump3":
            case "health1":
            case "health2":
            case "health3":
            case "stamina": return 4;

            case "extension":
            case "agouti": return 3;

            case "MITF":
            case "PAX3":
            case "cream":
            case "dun": return 2;

            default: return 1;
        }
    }

    public void printGeneLocations() {
        for (String gene : genes) {
            System.out.println(gene + ": size=" + getGeneSize(gene) + ", pos=" + getGenePos(gene) + ", chr=" + getGeneChromosome(gene));
        }
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

    public boolean isPearl() {
        return this.isHomozygous("cream", HorseAlleles.PEARL);
    }

    public boolean isDoubleCream() {
        return this.isHomozygous("cream", HorseAlleles.CREAM) 
            || this.isHomozygous("cream", HorseAlleles.SNOWDROP)
            || (this.hasAllele("cream", HorseAlleles.CREAM)
                && this.hasAllele("cream", HorseAlleles.SNOWDROP));
    }

    public boolean isCreamPearl() {
        return (this.hasAllele("cream", HorseAlleles.CREAM)
                || this.hasAllele("cream", HorseAlleles.SNOWDROP))
            && this.hasAllele("cream", HorseAlleles.PEARL);
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

    // Whether the horse shows primitive markings such as the dorsal stripe.
    public boolean hasStripe() {
        return this.isDun() || this.hasAllele("dun", HorseAlleles.NONDUN1);
    }

    public boolean isMealy() {
        return (this.getAllele("light_belly", 0) == HorseAlleles.MEALY 
                    && this.getAllele("agouti", 0) != HorseAlleles.A_BLACK)
                || (this.getAllele("light_belly", 1) == HorseAlleles.MEALY 
                    && this.getAllele("agouti", 1) != HorseAlleles.A_BLACK);
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

    // Number of years to turn fully gray
    public float getGrayRate() {
        // Starting age should vary from around 1 to 5 years
        // Ending age from 3 to 20
        int gray = countAlleles("gray", HorseAlleles.GRAY);
        float rate = 3f * (3 - gray);
        if (this.isHomozygous("slow_gray1", 1)) {
            rate *= 1.5f;
        }
        else if (this.hasAllele("slow_gray1", 1)) {
            rate *= 1.2f;
        }

        if (this.hasAllele("slow_gray2", 1)) {
            rate *= 1.3f;
        }

        if (this.isHomozygous("slow_gray3", 1)) {
            rate *= 1.2f;
        }

        if (this.hasAllele("gray_mane1", 1)) {
            rate *= 1.2f;
        }
        return rate;
    }

    // Number of years for the mane and tail to turn fully gray
    public float getGrayManeRate() {
        float rate = getGrayRate();
        if (this.hasAllele("gray_mane1", 0)) {
            rate *= 0.9f;
        }

        if (this.isHomozygous("gray_mane2", 0)) {
            rate *= 0.9f;
        }
        // Adjust so mane grays slightly before the body finishes
        return rate * 17f / 19f;
    }

    public float getImmuneHealth() {
        float scale = 8f;
        int diffs = this.countDiffs(this.getChromosome("mhc1"));
        diffs += this.countDiffs(this.getChromosome("mhc2"));
        diffs += this.countDiffs(this.getChromosome("immune"));
        // 3 ints, 16 genes each, half homozygous, makes total expected heterozygosity 24
        float heterozygosity = diffs / 24f;
        // Adjust so super outbreeding gives 1.25 advantage, not double advantage
        if (heterozygosity > 1f) {
            heterozygosity = 0.25f * (heterozygosity - 1) + 1;
        }
        return scale * heterozygosity;
    }

    public float getGrayHealthLoss() {
        // Count zygosity, mitigate from protective gene
        // Agouti may also have an effect on prevalence/severity,
        // but I'm not sufficiently convinced
        float base = countAlleles("gray", HorseAlleles.GRAY);
        if (isHomozygous("gray_melanoma", 0)) {
            base -= 1f;
        }
        // Horses without melanocytes in the skin should be much
        // less likely to get melanomas
        if (isWhite()) {
            base -= 1.5f;
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
        if (HorseConfig.GENETICS.enableHealthEffects.get()) {
            return -getGrayHealthLoss() - getSilverHealthLoss() - getDeafHealthLoss();
        }
        else {
            return 0;
        }
    }

    public float getHealth() {
        // Default horse health ranges from 15 to 30, but ours goes from
        // 15 to 31
        float healthStat = this.getStatValue("health1")
                            + this.getStatValue("health2")
                            + this.getStatValue("health3")
                            + this.getImmuneHealth();
        float maxHealth = 15.0F + healthStat * 0.5F;
        maxHealth += this.getBaseHealth();
        return maxHealth;
    }

    // A special case because it has two different alleles
    public int countW20() {
        return countAlleles("KIT", HorseAlleles.KIT_W20) 
                + countAlleles("KIT", HorseAlleles.KIT_TOBIANO_W20);
    }

    // Return true if the client needs to know the age to render properly,
    // aside from just whether the animal is a child
    public boolean clientNeedsAge() {
        return isGray();
    }

    public int getAge() {
        if (entity instanceof HorseGeneticEntity) {
            return ((HorseGeneticEntity)entity).getDisplayAge();
        }
        else {
            return 0;
        }
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

    public void randomizeNamedGenes(Map<String, List<Float>> map) {
        for (String gene : genes) {
            if (map.containsKey(gene)) {
                setNamedGene(gene, chooseRandom(map.get(gene)));
            }
            else {
                HorseColors.logger.debug(gene + " is not in the given map");
                setNamedGene(gene, 0);
            }
        }
    }

    /* Make the horse have random genetics. */
    public void randomize(Map<String, List<Float>> map)
    {
        randomizeNamedGenes(map);

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

        for (String stat : this.listGenericChromosomes()) {
            entity.setChromosome(stat, this.entity.getRand().nextInt());
        }
        entity.setChromosome("random", this.entity.getRand().nextInt());
    }

    private String getAbv(TextureLayer layer) {
        if (layer == null || layer.name == null) {
            return "";
        }        
        String abv = layer.toString() + "_";
        if (layer.next != null) {
            abv += ".-" + getAbv(layer.next) + "-.";
        }
        // Upper case characters will cause a crash
        return abv.toLowerCase();
    }

    public String judgeStatRaw(int val) {
        if (val <= 0) {
            return "worst";
        }
        else if (val <= 2) {
            return "bad";
        }
        else if (val <= 5) {
            return "avg";
        }
        else if (val <= 7) {
            return "good";
        }
        else {
            return "best";
        }
    }

    public String judgeStat(int val, String loc) {
        return Util.translate(loc + judgeStatRaw(val));
    }

    public String judgeStat(String stat) {
        return Util.translate("stats." + judgeStatRaw(getStatValue(stat)));
    }

    public List<List<String>> getBookContents() {
        List<List<String>> contents = new ArrayList<List<String>>();
        List<String> physical = new ArrayList<String>();
        physical.add(Util.translate("book.physical"));
        String health = Util.translate("stats.health") + "\n";
        health += "  " + Util.translate("stats.health1") + ": " + judgeStat("health1") + "\n";
        health += "  " + Util.translate("stats.health2") + ": " + judgeStat("health2") + "\n";
        health += "  " + Util.translate("stats.health3") + ": " + judgeStat("health3") + "\n";
        health += "  " + Util.translate("stats.immune") + ": " + judgeStat((int)getImmuneHealth(), "stats.immune.");
        if (HorseConfig.GENETICS.enableHealthEffects.get()) {
            if (getDeafHealthLoss() > 0.5f) {
                health += "\n" + Util.translate("stats.health.deaf");
            }
            float h = getHealth() + getSilverHealthLoss();
            if ((int)getHealth() != (int)h) {
                health += "\n" + Util.translate("stats.health.MCOA");
            }
            if ((int)h != (int)(h + getGrayHealthLoss())) {
                health += "\n" + Util.translate("stats.health.melanoma");
            }
        }
        physical.add(health);
        String athletics = Util.translate("stats.athletics") + "\n";
        athletics += "  " + Util.translate("stats.athletics1") + ": " + judgeStat("athletics1") + "\n";
        athletics += "  " + Util.translate("stats.athletics2") + ": " + judgeStat("athletics2");
        physical.add(athletics);
        String speed = Util.translate("stats.speed") + "\n";
        speed += "  " + Util.translate("stats.speed1") + ": " + judgeStat("speed1") + "\n";
        speed += "  " + Util.translate("stats.speed2") + ": " + judgeStat("speed2") + "\n";
        speed += "  " + Util.translate("stats.speed3") + ": " + judgeStat("speed3");
        physical.add(speed);
        String jump = Util.translate("stats.jump") + "\n";
        jump += "  " + Util.translate("stats.jump1") + ": " + judgeStat("jump1") + "\n";
        jump += "  " + Util.translate("stats.jump2") + ": " + judgeStat("jump2") + "\n";
        jump += "  " + Util.translate("stats.jump3") + ": " + judgeStat("jump3");
        physical.add(jump);
        if (HorseConfig.GENETICS.useGeneticStats.get() 
            && HorseConfig.GENETICS.bookShowsTraits.get()) {
            contents.add(physical);
        }

        List<String> genelist = ImmutableList.of("extension", "agouti", "dun", "gray", "cream", "silver", "KIT", "frame", "MITF");
        List<String> genetic = new ArrayList<String>();
        genetic.add(Util.translate("book.genetic"));
        for (String gene : genelist) {
            String translationLocation = "genes." + gene;
            String s = Util.translate(translationLocation + ".name") + ": ";
            s += Util.translate(translationLocation + ".allele" + getAllele(gene, 0)) + "/";
            s += Util.translate(translationLocation + ".allele" + getAllele(gene, 1));
            genetic.add(s);
        }
        if (HorseConfig.GENETICS.bookShowsGenes.get()) {
            contents.add(genetic);
        }
        return contents;
    }

    @OnlyIn(Dist.CLIENT)
    public void setTexturePaths()
    {
        this.textureLayers = HorseColorCalculator.getTexturePaths(this);
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
            int val = 0;
            try {
                String c = s.substring(8 * i, 8 * (i + 1));
                val = (int)Long.parseLong(c, 16);
            }
            catch (IndexOutOfBoundsException e) {}
            catch (NumberFormatException e) {}
            entity.setChromosome(chromosomes.get(i), val);
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

    @Override
    public void inheritGenes(Genome parent1, Genome parent2) {
        super.inheritGenes(parent1, parent2);
        this.entity.setChromosome("random", this.rand.nextInt());
    }
}
