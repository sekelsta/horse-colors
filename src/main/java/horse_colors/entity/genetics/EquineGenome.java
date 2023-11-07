package sekelsta.horse_colors.entity.genetics;

import java.util.*;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.util.RandomSupplier;
import sekelsta.horse_colors.util.Util;

public class EquineGenome extends Genome {
    // The improved readability from snake case is worth breaking the enum
    // convention of all caps for genes
    // This also makes the difference between the gene KIT and the word kit more
    // apparent.
    public enum Gene {
        extension,    // 0 for e (red), 1 for E (can have black)
        agouti,       // 0 for black, 1 for seal brown, 4 for bay
        dun,          // 0 for nd2, 1 for nd1, 2 for dun, 3 for donkeys
        gray,         // 0 for g (non-gray), 1 for G (gray)
        cream,        // 0 for wildtype, 1 for snowdrop, 2 for pearl, 3 for cream, 4 for lighter shade
        liver,        // 0 for liver, 1 for non-liver
        flaxen1,      // 0 for flaxen, 1 for non-flaxen
        flaxen2,
        dapple,       // 0 for non-dappled, 1 for dappled
        sooty1,       // 0 for non-sooty, 1 for sooty
        sooty2,       // same as above
        sooty3,       // 0 for sooty, 1 for non-sooty
        // I'm treating this as the agouti promoter region responsible for 
        // white bellied agouti in mice
        light_belly,
        mealy1,
        mealy2,
        KIT,
        MITF,
        leopard,
        PATN1,
        PATN2,
        PATN3,
        gray_suppression,
        slow_gray1,
        slow_gray2,
        slow_gray3,
        white_star,
        white_forelegs,
        white_hindlegs,
        gray_melanoma,
        gray_mane1,
        gray_mane2,
        rufous,
        dense,
        champagne,
        cameo,
        ivory,
        donkey_dark,
        cross,
        reduced_points,
        light_legs,
        tyger,
        DEPRECATED_donkey_dun,
        flaxen_boost,
        light_dun,
        marble,
        leopard_suppression,
        leopard_suppression2,
        PATN_boost1,
        PATN_boost2,
        PAX3, 
        white_suppression, 
        frame, 
        silver, 
        dark_red,
        liver_boost,
        LCORL,
        HMGA2,
        mushroom,
        speed0,
        speed1,
        speed2,
        speed3,
        speed4,
        speed5,
        speed6,
        speed7,
        speed8,
        speed9,
        speed10,
        speed11,
        athletics0,
        athletics1,
        athletics2,
        athletics3,
        athletics4,
        athletics5,
        athletics6,
        athletics7,
        jump0,
        jump1,
        jump2,
        jump3,
        jump4,
        jump5,
        jump6,
        jump7,
        jump8,
        jump9,
        jump10,
        jump11,
        health0,
        health1,
        health2,
        health3,
        health4,
        health5,
        health6,
        health7,
        health8,
        health9,
        health10,
        health11,
        immune0,
        immune1,
        immune2,
        immune3,
        immune4,
        immune5,
        immune6,
        immune7,
        mhc0,
        mhc1,
        mhc2,
        mhc3,
        mhc4,
        mhc5,
        mhc6,
        mhc7,
        size_minor0,
        size_minor1,
        size_minor2,
        size_minor3,
        size_minor4,
        size_minor5,
        size_minor6,
        size_minor7,
        size0,
        size1,
        size2,
        size3,
        size4,
        size_subtle0,
        size_subtle1,
        size_subtle2,
        size_subtle3,
        size_subtle4,
        size_subtle5,
        size_subtle6,
        size_subtle7,
        double_ovulation,
        donkey_size0,
        donkey_size1,
        donkey_size2,
        donkey_size3,
        donkey_size4,
        donkey_size5,
        donkey_size6,
        color,              // TYR, albino donkeys
        rabicano,
        blue_eye_shade1,      // 0 pale, 1 deep
        blue_eye_shade2,      // 0 deep, 1 pale
        blue_eye_shade3,      // 0 deep, 1 pale
        tiger_eye,
        brown_eye_shade1,     // 0 deep, 1 pale recessive
        brown_eye_shade2,     // 0 deep, 1 pale
        brown_eye_shade3,     // 0 pale, 1 deep
        stripe_width        // 0 wide, 1 narrow dominant
    }

    public static final double MINIATURE_CUTOFF = 317.5;

    public EquineGenome(Species species, IGeneticEntity entityIn) {
        super(species, entityIn, new RandomSupplier(ImmutableList.of("leg_white",
                "face_white", "star_choice", "roan_density", "liver_darkness", 
                "shade", "size", "cat_tracks", "pinto")));
    }

    public EquineGenome(Species species) {
        this(species, new FakeGeneticEntity());
    }

    @Override
    public List<Enum> listGenes() {
        return Arrays.asList(Gene.values());
    }

    @Override
    public List<Genome.Linkage> listLinkages() {
        List<Genome.Linkage> linkages = super.listLinkages();
        // It doesn't matter if some appear twice, the last will be used
        // Extension and KIT are about 20 cM apart, based on the map in
        // https://www.researchgate.net/publication/7666109_International_Equine_Gene_Mapping_Workshop_Report_A_comprehensive_linkage_map_constructed_with_data_from_new_markers_and_by_merging_four_mapping_resources
        // Note that because tobiano is an inversion in between extension and
        // KIT, a heterozygous tobiano horse would have much closer linkage.
        // See https://www.mun.ca/biology/scarr/iGen3_16-08.html for a 
        // diagram of the effect an inversion has on crossover and linkage
        // Going to arbitrarily put them 3 cM apart instead, because I heard that number somewhere and donkey spotting
        // appears to be very tightly linked to extension.
        linkages.add(new Genome.Linkage(Gene.extension, 0.03f));
        linkages.add(new Genome.Linkage(Gene.KIT));

        linkages.add(new Genome.Linkage(Gene.agouti, 0.0f));
        linkages.add(new Genome.Linkage(Gene.light_belly));

        for (int i = 0; i < 7; ++i) {
            linkages.add(new Genome.Linkage(Gene.valueOf("mhc" + i), 0.2f));
        }
        linkages.add(new Genome.Linkage(Gene.mhc7));

        return linkages;
    }

    public void printGeneData() {
        String g = entity.getGeneData();
        String gene_debug = "";
        for (int i = 0; i < g.length(); ++i) {
            gene_debug += (short)g.charAt(i) + " ";
        }
        System.out.println(gene_debug);
    }

    public boolean isChestnut()
    {
        return this.isHomozygous(Gene.extension, HorseAlleles.E_RED);
    }

    public boolean hasCream() {
        return this.hasAllele(Gene.cream, HorseAlleles.CREAM);
    }

    public boolean isPearl() {
        return this.isHomozygous(Gene.cream, HorseAlleles.PEARL);
    }

    public boolean isDoubleCream() {
        return this.isHomozygous(Gene.cream, HorseAlleles.CREAM) 
            || this.isHomozygous(Gene.cream, HorseAlleles.SNOWDROP);
    }

    public boolean isCreamPearl() {
        return this.hasAllele(Gene.cream, HorseAlleles.CREAM)
            && this.hasAllele(Gene.cream, HorseAlleles.PEARL);
    }

    public boolean isMushroom() {
        return this.isHomozygous(Gene.mushroom, 1);
    }

    public boolean isSilver() {
        return this.hasAllele(Gene.silver, HorseAlleles.SILVER);
    }

    public boolean isGray() {
        return this.hasAllele(Gene.gray, HorseAlleles.GRAY);
    }

    public boolean isDun() {
        return hasAllele(Gene.dun, HorseAlleles.DUN) || hasAllele(Gene.dun, HorseAlleles.DONKEY_DUN);
    }

    // Returns a number from 0 to 1, with 0 being less dun effect and 1 being more
    public float dunStrength() {
        int m = getAllele(Gene.dun, 0);
        int p = getAllele(Gene.dun, 1);

        if ((m == HorseAlleles.DUN || m == HorseAlleles.DONKEY_DUN) 
                && (p == HorseAlleles.DUN || p == HorseAlleles.DONKEY_DUN)) {
            return 0.4f;
        }
        else if ((m == HorseAlleles.DUN || m == HorseAlleles.DONKEY_DUN) 
                || (p == HorseAlleles.DUN || p == HorseAlleles.DONKEY_DUN)) {
            return 0.3f;
        }
        else if ((m == HorseAlleles.NONDUN1 || m == HorseAlleles.DONKEY_NONDUN_CROSS) 
                && (p == HorseAlleles.NONDUN1 || p == HorseAlleles.DONKEY_NONDUN_CROSS)) {
            return 0.16f;
        }
        else if ((m == HorseAlleles.NONDUN1 || m == HorseAlleles.DONKEY_NONDUN_CROSS) 
                || (p == HorseAlleles.NONDUN1 || p == HorseAlleles.DONKEY_NONDUN_CROSS)) {
            return 0.08f;
        }

        return 0;
    }

    // Whether the horse shows primitive markings such as the dorsal stripe.
    public boolean hasStripe() {
        for (int parent = 0; parent < 2; ++parent) {
            int d = getAllele(Gene.dun, parent);
            if (d == HorseAlleles.DUN 
                || d == HorseAlleles.NONDUN1 
                || d == HorseAlleles.DONKEY_DUN
                || d == HorseAlleles.DONKEY_NONDUN_CROSS)
            {
                return true;
            }
        }

        return false;
    }

    public boolean isFrostedDun() {
        return isDun() && !isHomozygous(Gene.stripe_width, 0);
    }

    public boolean hasNarrowStripe() {
        return hasStripe() && !isHomozygous(Gene.stripe_width, 0);
    }

    public boolean isMealy() {
        return (this.getAllele(Gene.light_belly, 0) == HorseAlleles.MEALY 
                    && this.getAllele(Gene.agouti, 0) != HorseAlleles.A_BLACK)
                || (this.getAllele(Gene.light_belly, 1) == HorseAlleles.MEALY 
                    && this.getAllele(Gene.agouti, 1) != HorseAlleles.A_BLACK);
    }

    // The MC1R ("extension") gene seems to be associated with white
    // patterning. For now I assume this is caused by MC1R itself,
    // but if it turns out to be a different gene that's just very
    // closely linked, I can change this.
    public boolean hasMC1RWhiteBoost() {
        return isChestnut();
    }

    public boolean isTobiano() {
        return HorseAlleles.isTobianoAllele(getAllele(Gene.KIT, 0))
            || HorseAlleles.isTobianoAllele(getAllele(Gene.KIT, 1));
    }

    public boolean isHomozygousTobiano() {
        return HorseAlleles.isTobianoAllele(getAllele(Gene.KIT, 0))
            && HorseAlleles.isTobianoAllele(getAllele(Gene.KIT, 1));
    }

    public int getSabinoFactor() {
        int white = 0;
        white += 200 * countAlleles(Gene.KIT, HorseAlleles.KIT_DONKEY_WHITE);
        white += 90 * countAlleles(Gene.KIT, HorseAlleles.KIT_DOMINANT_WHITE);
        white += 25 * countAlleles(Gene.KIT, HorseAlleles.KIT_W5);
        white += 5 * countAlleles(Gene.KIT, HorseAlleles.KIT_SABINO1);
        white += 15 * countAlleles(Gene.KIT, HorseAlleles.KIT_FLASHY_WHITE);
        white += 6 * countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS5);
        white += 5 * countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS4);
        white += 4 * countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS3);
        white += 3 * countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS2);
        white += 2 * countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS1);
        white += 2 * countAlleles(Gene.white_forelegs, 1);
        white += 2 * countAlleles(Gene.white_hindlegs, 1);
        white += 2 * countAlleles(Gene.KIT, HorseAlleles.KIT_WHITE_BOOST);

        if (hasMC1RWhiteBoost()) {
            white += 8;
        }

        if (this.species == Species.DONKEY) {
            white -= 10;
        }

        double boost = 0;
        if (hasAllele(Gene.white_suppression, 1))
        {
            white -= 8;
            boost -= 0.5;
        }

        boost += 2.5 * countAlleles(Gene.KIT, HorseAlleles.KIT_SABINO1);
        boost += 2 * countW20();
        boost += 0.5 * countAlleles(Gene.KIT, HorseAlleles.KIT_W5);
        boost += 0.5 * countAlleles(Gene.KIT, HorseAlleles.KIT_WHITE_BOOST);

        white += boost;
        if (white > 0) {
            white += (int)(white * boost);
        }

        return white;
    }

    public boolean isWhite() {
        int sabino = getSabinoFactor();
        return sabino > 85
            || this.isLethalWhite()
            || (sabino > 50
                && (this.hasAllele(Gene.frame, HorseAlleles.FRAME)
                    || this.isTobiano())
                && this.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1));
    }

    public boolean showsLegMarkings() {
        return !isWhite() && !isTobiano();
    }

    public boolean isDappleInclined() {
        // Recessive so that mules are not dappled
        return this.isHomozygous(Gene.dapple, 1);
    }

    public boolean isLethalWhite() {
        return this.isHomozygous(Gene.frame, HorseAlleles.FRAME);
    }

    private boolean isEmbryonicLethalAllele(Gene gene, int allele) {
        if (gene == Gene.KIT) {
            return allele == HorseAlleles.KIT_DOMINANT_WHITE 
                || allele == HorseAlleles.KIT_DONKEY_WHITE
                || allele == HorseAlleles.KIT_W5;
        }
        return false;
    }

    public boolean isEmbryonicLethal() {
        return isEmbryonicLethalAllele(Gene.KIT, getAllele(Gene.KIT, 0))
            && isEmbryonicLethalAllele(Gene.KIT, getAllele(Gene.KIT, 1));
    }

    public boolean isMaybeEmbryonicLethal() {
        int maternal = getAllele(Gene.KIT, 0);
        int paternal = getAllele(Gene.KIT, 1);

        return (isEmbryonicLethalAllele(Gene.KIT, maternal) || maternal == HorseAlleles.KIT_DONKEY_SPOTTING)
            && (isEmbryonicLethalAllele(Gene.KIT, paternal) || paternal == HorseAlleles.KIT_DONKEY_SPOTTING);
    }

    public boolean hasERURiskFactor() {
        return this.getAllele(Gene.mhc1, 0) % 4 == 3 
                && this.getAllele(Gene.mhc1, 1) % 4 == 3;
    }

    public boolean isAlbino() {
        return this.isHomozygous(Gene.color, 1);
    }

    public int getSootyLevel() {
        // sooty1 and 2 dominant, 3 recessive
        int sooty = getMaxAllele(Gene.sooty1) + getMaxAllele(Gene.sooty2);
        sooty += 1 - getMaxAllele(Gene.sooty3);
        if (!this.isChestnut()) {
            // Wild bay tends to come with a clearer, lighter coat
            sooty += 1 - 2 * getMaxAllele(Gene.reduced_points);
            sooty = Math.max(0, sooty);
        }
        return sooty;
    }

    // Number of years to turn fully gray
    public float getGrayRate() {
        // Starting age should vary from around 1 to 5 years
        // Ending age from 3 to 20
        int gray = countAlleles(Gene.gray, HorseAlleles.GRAY);
        float rate = 3f * (3 - gray);
        if (this.isHomozygous(Gene.slow_gray1, 1)) {
            rate *= 1.5f;
        }
        else if (this.hasAllele(Gene.slow_gray1, 1)) {
            rate *= 1.2f;
        }

        if (this.hasAllele(Gene.slow_gray2, 1)) {
            rate *= 1.3f;
        }

        if (this.isHomozygous(Gene.slow_gray3, 1)) {
            rate *= 1.2f;
        }

        if (this.hasAllele(Gene.gray_mane1, 1)) {
            rate *= 1.2f;
        }
        return rate;
    }

    // Number of years for the mane and tail to turn fully gray
    public float getGrayManeRate() {
        float rate = getGrayRate();
        if (this.hasAllele(Gene.gray_mane1, 0)) {
            rate *= 0.9f;
        }

        if (this.isHomozygous(Gene.gray_mane2, 0)) {
            rate *= 0.9f;
        }
        // Adjust so mane grays slightly before the body finishes
        return rate * 17f / 19f;
    }

    public float getImmuneHealth() {
        float scale = 7f;
        // Sum of heterozygosity of the 16 immune diversity genes
        int diffs = 0;
        for (int i = 0; i < 8; ++i) {
            Gene immune_gene = Gene.valueOf("immune" + i);
            if (getAllele(immune_gene, 0) != getAllele(immune_gene, 1)) {
                diffs++;
            }
            Gene mhc_gene = Gene.valueOf("mhc" + i);
            if (getAllele(mhc_gene, 0) != getAllele(mhc_gene, 1)) {
                diffs++;
            }
        }
        // 16 genes each with 16 alleles, makes total expected heterozygosity 15
        // But horses from older versions had fewer, and allow for some bad 
        // luck, so use 12
        float heterozygosity = diffs / 12f;
        // Adjust so super outbreeding gives 1.25 advantage, not double advantage
        if (heterozygosity > 1f) {
            heterozygosity = 0.25f * (heterozygosity - 1) + 1;
        }
        return Math.min(scale, scale * heterozygosity);
    }

    public float getGrayHealthLoss() {
        // Count zygosity, mitigate from protective gene
        // Agouti may also have an effect on prevalence/severity,
        // but I'm not sufficiently convinced
        float base = countAlleles(Gene.gray, HorseAlleles.GRAY);
        if (isHomozygous(Gene.gray_melanoma, 0)) {
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
        if (isHomozygous(Gene.silver, HorseAlleles.SILVER)) {
            return 1f;
        }
        else if (hasAllele(Gene.silver, HorseAlleles.SILVER)) {
            return 0.5f;
        }
        else {
            return 0;
        }
    }

    public float getDeafHealthLoss() {
        if (HorsePatternCalculator.hasPigmentInEars(this)) {
            return 0f;
        }
        else {
            return 1f;
        }
    }

    public float getERUHealthLoss() {
        if (hasERURiskFactor()) {
            return 0.5f * countAlleles(Gene.leopard, HorseAlleles.LEOPARD);
        }
        return 0;
    }

    public float getBaseHealth() {
        if (HorseConfig.GENETICS.enableHealthEffects.get()) {
            return -getGrayHealthLoss() - getSilverHealthLoss() - getDeafHealthLoss() - getERUHealthLoss();
        }
        else {
            return 0;
        }
    }

    public float getHealth() {
        // Default horse health ranges from 15 to 30, but ours goes from
        // 15 to 31
        float healthStat = this.sumGenes(Gene.class, "health", 0, 4)
                            + this.sumGenes(Gene.class, "health", 4, 8)
                            + this.sumGenes(Gene.class, "health", 8, 12)
                            + this.getImmuneHealth();
        float maxHealth = 15.0F + healthStat * 0.5F;
        if (HorseConfig.COMMON.enableSizes.get()) {
            // Adjust so a very small horse can have 10-20 health while a very
            // large horse can have about 20-45 health.
            // On the small end, compare to cows which have 10 health.
            // On the large end, compare to players which have 20 - by the time
            // a horse is big enough to have 40 health, it can carry two players.
            maxHealth *= Math.min(this.getAdultScale() / 1.1f, 1.5f);
        }
        maxHealth += this.getBaseHealth();
        // Horse always have at least 4 health
        return Math.max(maxHealth, 4);
    }

    // A special case because it has two different alleles
    public int countW20() {
        return countAlleles(Gene.KIT, HorseAlleles.KIT_W20) 
                + countAlleles(Gene.KIT, HorseAlleles.KIT_TOBIANO_W20);
    }

    // Helper function for perfectly codominant size genes
    private float getSizeContribution(Enum gene, int allele, float coef) {
        float size = 1f;
        for (int i = 0; i < countAlleles(gene, allele); ++i) {
            size *= coef;
        }
        return size;
    }

    // Genetic-based size, which unlike age-based size should affect the hitbox
    // This is a multiplier for both width and height, so adjust for that when
    // calculating weight.
    public float getGeneticScale() {
        float size = 1f;
        // Donkeys are smaller no matter whether sizes are enabled
        if (this.species == Species.DONKEY) {
            size *= 0.9f;
        } // Likewise for mules and hinnies, but less so
        else if (this.species == Species.MULE || this.species == Species.HINNY) {
            size *= 0.98f;
        }
        if (!HorseConfig.COMMON.enableSizes.get()) {
            // Standard size is around 14.2, 14.3 hands high
            return size * 1.1f;
        }

        size *= this.entity.isMale() ? 1.01f : 0.99f;
        // LCORL is based off of information from the Center for Animal Genetics
        // They list T/T warmbloods as ~159 cm, T/C warmbloods as ~164 cm, and
        // C/C warmbloods as ~169 cm.
        // I've assumed the relationship is multiplicative.
        // 0 is T, 1 is C
        size *= getSizeContribution(Gene.LCORL, 1, 1.03f);
        // HMGA2 is based off of information from the Center for Animal Genetics
        // They list G/G ponies as 104 cm tall at the withers, G/A as 98 cm,
        // and A/A as 84 cm.
        // Again, I'm assuming the relationship is multiplicative.
        // 0 is G, 1 is A
        if (this.isHomozygous(Gene.HMGA2, 1)) {
            size *= 0.81f;
        }
        else if (this.hasAllele(Gene.HMGA2, 1)) {
            size *= 0.94f;
        }
        // Minor size variations
        size *= getSizeContribution(Gene.size_minor0, 1, 1.002f);
        size *= getSizeContribution(Gene.size_minor0, 2, 1f/1.002f);
        size *= getSizeContribution(Gene.size_minor0, 3, 1.009f);
        size *= getSizeContribution(Gene.size_minor0, 4, 1f/1.009f);

        size *= getSizeContribution(Gene.size_minor1, 1, 1.003f);
        size *= getSizeContribution(Gene.size_minor1, 2, 1f/1.003f);
        size *= getSizeContribution(Gene.size_minor1, 3, 1.015f);
        size *= getSizeContribution(Gene.size_minor1, 4, 1f/1.015f);

        size *= getSizeContribution(Gene.size_minor2, 1, 1.001f);
        size *= getSizeContribution(Gene.size_minor2, 2, 1f/1.001f);
        size *= getSizeContribution(Gene.size_minor2, 3, 1.012f);
        size *= getSizeContribution(Gene.size_minor2, 4, 1f/1.012f);

        size *= getSizeContribution(Gene.size_minor3, 1, 1.001f);
        size *= getSizeContribution(Gene.size_minor3, 2, 1f/1.001f);
        size *= getSizeContribution(Gene.size_minor3, 3, 1.01f);
        size *= getSizeContribution(Gene.size_minor3, 4, 1f/1.01f);

        size *= getSizeContribution(Gene.size_minor4, 1, 1.002f);
        size *= getSizeContribution(Gene.size_minor4, 2, 1f/1.002f);
        size *= getSizeContribution(Gene.size_minor4, 3, 1.008f);
        size *= getSizeContribution(Gene.size_minor4, 4, 1f/1.008f);

        size *= getSizeContribution(Gene.size_minor5, 1, 1.001f);
        size *= getSizeContribution(Gene.size_minor5, 2, 1f/1.001f);
        size *= getSizeContribution(Gene.size_minor5, 3, 1.005f);
        size *= getSizeContribution(Gene.size_minor5, 4, 1f/1.005f);

        size *= getSizeContribution(Gene.size_minor6, 1, 1.0025f);
        size *= getSizeContribution(Gene.size_minor6, 2, 1f/1.0025f);
        size *= getSizeContribution(Gene.size_minor6, 3, 1.005f);
        size *= getSizeContribution(Gene.size_minor6, 4, 1f/1.005f);

        size *= getSizeContribution(Gene.size_minor7, 1, 1.0025f);
        size *= getSizeContribution(Gene.size_minor7, 2, 1f/1.0025f);
        size *= getSizeContribution(Gene.size_minor7, 3, 1.005f);
        size *= getSizeContribution(Gene.size_minor7, 4, 1f/1.005f);

        // More small effect genes
        for (int i = 0; i < 8; ++i) {
            for (int n = 1; n < 5; ++n) {
                float scale = 1f + 0.001f * n;
                int large = 2 * n - 1;
                int small = 2 * n;
                Gene gene = Gene.valueOf("size_subtle" + i);
                size *= getSizeContribution(gene, large, scale);
                size *= getSizeContribution(gene, small, 1f/scale);
            }
        }

        // Larger effect size genes
        // Imprinted gene, unmethylated copy inherited from the mother
        if (this.getAllele(Gene.size0, 0) == 1) {
            size *= 1.06f;
        }
        // Mostly dominant
        if (this.isHomozygous(Gene.size1, 1)) {
            size *= 1.1f;
        }
        else if (this.hasAllele(Gene.size1, 1)) {
            size *= 1.08f;
        }
        // Incomplete dominant
        size *= getSizeContribution(Gene.size2, 1, 1.002f);
        size *= getSizeContribution(Gene.size2, 2, 1.03f);
        size *= getSizeContribution(Gene.size2, 3, 1.05f);
        // Imprinted gene, unmethylated copy inherited from the father
        if (this.getAllele(Gene.size3, 1) == 1) {
            size /= 1.08;
        }
        // Larger effects (smaller horse) semi-recessive
        float[] size4 = {1f, 1f};
        for (int n = 0; n < 2; ++n) {
            switch(getAllele(Gene.size4, n)) {
                case 1:
                    size4[n] = 1/1.005f;
                    break;
                case 2:
                    size4[n] = 1/1.02f;
                    break;
                case 3:
                    size4[n] = 1/1.05f;
                    break;
                case 4:
                    size4[n] = 1/1.06f;
            }
        }
        float smaller = Math.min(size4[0], size4[1]);
        float larger = Math.max(size4[0], size4[1]);
        size *= Math.pow(smaller, 0.4) * Math.pow(larger, 1.6);

        // Donkey size genes
        // Incomplete dominant
        size *= getSizeContribution(Gene.donkey_size0, 1, 1.01f);
        size *= getSizeContribution(Gene.donkey_size0, 2, 1.03f);
        size *= getSizeContribution(Gene.donkey_size1, 1, 1.02f);
        size *= getSizeContribution(Gene.donkey_size1, 2, 1.04f);
        size *= getSizeContribution(Gene.donkey_size2, 1, 1f/1.02f);
        size *= getSizeContribution(Gene.donkey_size2, 2, 1f/1.04f);
        size *= getSizeContribution(Gene.donkey_size3, 1, 1f/1.06f);
        // Mostly recessive
        if (this.isHomozygous(Gene.donkey_size4, 1)) {
            size /= 1.1f;
        }
        else if (this.hasAllele(Gene.donkey_size4, 1)) {
            size /= 1.02f;
        }  
        // Incomplete dominant 
        size *= getSizeContribution(Gene.donkey_size5, 1, 1.025f); 
        // Mostly dominant
        if (this.isHomozygous(Gene.donkey_size6, 1)) {
            size /= 1.06f;
        }
        else if (this.hasAllele(Gene.donkey_size6, 1)) {
            size /= 1.04f;
        }
        

        // A little bit of randomness to size
        int r = getRandom("size") >>> 1;
        size *= 1f + 0.01f * (float)(r % 64 - 32)/32f;
        return size;
    }

    // Numbers here are guided by
    // https://royalsocietypublishing.org/doi/pdf/10.1098/rspb.1938.0029
    // (a study of shetland-shire crosses)
    public float getCurrentScale() {
        // Birth size is restricted by the mother's size and by foal's growth
        float birthSize = (float)Math.min(
            // 0.46 scale cubed makes it 10% of the mother's weight
            0.46 * entity.getMotherSize(),
            0.55 * this.getAdultScale()
        );
        // If sizes aren't enabled, the mother's size shouldn't have an effect
        if (!HorseConfig.COMMON.enableSizes.get()) {
            birthSize = 0.46f;
        }
        float f = entity.getFractionGrown();
        f = Math.min(1f, (Math.max(0f, f)));
        return getAdultScale() * f + birthSize * (1f - f);
    }

    // Scale for an adult horse after accounting for genetic and environmental factors
    public float getAdultScale() {
        float size = this.getGeneticScale();
        // Weighted geometric average with mother's size
        if (HorseConfig.COMMON.enableSizes.get()) {
            size = (float)(Math.pow(size, 0.7) * Math.pow(entity.getMotherSize(), 0.3));
        }
        return size;
    }

    // Returns adult weight in kilograms
    public float getGeneticWeightKg() {
        // A full-grown horse with scale 1 is 132 cm tall and weighs ~800 pounds
        float scale = this.getAdultScale();
        return 362.9f * scale * scale * scale;
    }

    // Returns adult height in centimeters
    public float getGeneticHeightCm() {
        float scale = this.getAdultScale();
        return 132f * scale;
    }

    public boolean isMiniature() {
        // Assuming the rider and saddle together weight 140 pounds, a horse
        // needs to weigh at least 700 pounds to carry a rider
        return getGeneticWeightKg() < MINIATURE_CUTOFF;
    }

    public boolean isLarge() {
        // Assuming two riders and a large saddle weigh 280 pounds, a horse
        // needs to be at least 1400 pounds to carry them both
        return getGeneticWeightKg() > 635;
    }

    public int getAge() {
        if (entity instanceof AbstractHorseGenetic) {
            return ((AbstractHorseGenetic)entity).getDisplayAge();
        }
        else {
            return 0;
        }
    }

    // Distribution should be a series of floats increasing from
    // 0.0 to 1.0, where the probability of choosing allele i is
    // the chance that a random uniform number between 0 and 1
    // is greater than distribution[i-1] but less than distribution[i].
    protected int chooseRandomAllele(List<Float> distribution) {
        float n = this.entity.getRand().nextFloat();
        for (int i = 0; i < distribution.size(); ++i) {
            if (n < distribution.get(i)) {
                return i;
            }
        }
        // In case of floating point rounding errors
        return distribution.size() - 1;
    }

    protected void randomizeGenes(Breed breed) {
        for (Enum gene : listGenes()) {
            if (!breed.contains(gene)) {
                HorseColors.logger.debug(gene.toString() + " is not in the given map");
            }
            // If it doesn't contain the gene, it will return a sensible
            // default value
            List<Float> distribution = breed.get(gene);
            int allele0 = chooseRandomAllele(distribution);
            int allele1 = chooseRandomAllele(distribution);
            setAllele(gene, 0, allele0);
            setAllele(gene, 1, allele1);
        }
    }

    /* Make the horse have random genetics. */
    public void randomize(Breed breed)
    {
        randomizeGenes(breed);
        finalizeGenes();

        entity.setSeed(this.entity.getRand().nextInt());
        this.entity.setMale(this.rand.nextBoolean());
    }

    public void finalizeGenes() {
        // Replace lethal white overos with heterozygotes
        if (isHomozygous(Gene.frame, HorseAlleles.FRAME))
        {
            setAllele(Gene.frame, 0, 0);
        }

        // Homozygote dominant whites will be replaced with heterozygotes
        if (isEmbryonicLethal() || isMaybeEmbryonicLethal())
        {
            setAllele(Gene.KIT, 0, 0);
        }

        // Don't spawn donkey white spotting linked to sorrel
        for (int i = 0; i < 2; ++i) {
            if (getAllele(Gene.KIT, i) == HorseAlleles.KIT_DONKEY_SPOTTING 
                    && getAllele(Gene.extension, i) == HorseAlleles.E_RED) {
                setAllele(Gene.extension, i, HorseAlleles.E_BLACK);
            }
        }
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

    private String judgeStatRaw12(int val) {
        if (val <= 1) {
            return "worst";
        }
        else if (val <= 4) {
            return "bad";
        }
        else if (val <= 7) {
            return "avg";
        }
        else if (val <= 10) {
            return "good";
        }
        else {
            return "best";
        }
    }

    public String judgeStat(int val, String loc) {
        return Util.translate(loc + judgeStatRaw(val));
    }

    public String judgeStat(String name, int min, int max) {
        return Util.translate("stats." + judgeStatRaw(sumGenes(Gene.class, name, min, max)));
    }

    private String judgeStat12(String name, int min, int max) {
        return Util.translate("stats." + judgeStatRaw12(sumGenes(Gene.class, name, min, max)));
    }

    private void listGenes(ArrayList<String> list, List<Gene> genelist) {
        for (Gene gene : genelist) {
            if (gene == Gene.KIT && this.species != Species.DONKEY) {
                String tobianoLocation = "genes.tobiano";
                String tobi = Util.translate(tobianoLocation + ".name") + ": ";
                String a1 = HorseAlleles.isTobianoAllele(getAllele(Gene.KIT, 0))? "Tobiano" : "Wildtype";
                String a2 = HorseAlleles.isTobianoAllele(getAllele(Gene.KIT, 1))? "Tobiano" : "Wildtype";
                tobi += Util.translate(tobianoLocation + ".allele" + a1) + "/";
                tobi += Util.translate(tobianoLocation + ".allele" + a2);
                list.add(tobi);
            }
            String translationLocation = "genes." + gene.toString();
            String s = Util.translate(translationLocation + ".name") + ": ";
            s += Util.translate(translationLocation + ".allele" + getAllele(gene, 0)) + "/";
            s += Util.translate(translationLocation + ".allele" + getAllele(gene, 1));
            list.add(s);
        }
    }

    public List<List<String>> getBookContents() {
        List<List<String>> contents = new ArrayList<List<String>>();
        List<String> physical = new ArrayList<String>();
        physical.add(Util.translate("book.physical"));
        String health = Util.translate("stats.health");
        health += "\n";
        health += "  " + Util.translate("stats.health1") + ": " + judgeStat("health", 0, 4) + "\n";
        health += "  " + Util.translate("stats.health2") + ": " + judgeStat("health", 4, 8) + "\n";
        health += "  " + Util.translate("stats.health3") + ": " + judgeStat("health", 8, 12) + "\n  ";
        health += Util.translate("stats.immune") + ": " + judgeStat((int)getImmuneHealth(), "stats.immune.");
        if (HorseConfig.COMMON.enableSizes.get()) {
            health += "\n" + Util.translate("stats.health_size_note");
        }
        String healthEffects = "";
        // Overo lethal white syndrome is not affected by the configuration
        if (this.isLethalWhite()) {
            healthEffects += "\n" + Util.translate("stats.health.lethal_white");
        }
        if (HorseConfig.GENETICS.enableHealthEffects.get()) {
            if (getDeafHealthLoss() > 0.5f) {
                healthEffects += "\n" + Util.translate("stats.health.deaf");
            }
            float h = getHealth() + getSilverHealthLoss();
            if ((int)getHealth() != (int)h) {
                healthEffects += "\n" + Util.translate("stats.health.MCOA");
            }
            float h2 = h + getGrayHealthLoss();
            if ((int)h != (int)h2) {
                healthEffects += "\n" + Util.translate("stats.health.melanoma");
            }
            if ((int)h2 != (int)(h2 + getERUHealthLoss())) {
                healthEffects += "\n" + Util.translate("stats.health.ERU");
            }
            if (isHomozygous(Gene.leopard, HorseAlleles.LEOPARD)) {
                healthEffects += "\n" + Util.translate("stats.health.CSNB");
            }
        }
        physical.add(health);
        String athletics = "";
        if (this.species == Species.DONKEY) {
            athletics += "\n" + Util.translate("stats.athletics1") 
                        + ": " + judgeStat("athletics", 0, 8);
        }
        else {
            athletics += Util.translate("stats.athletics") + "\n";
            athletics += "  " + Util.translate("stats.athletics1") + ": " + judgeStat("athletics", 0, 4) + "\n";
            athletics += "  " + Util.translate("stats.athletics2") + ": " + judgeStat("athletics", 4, 8);
        }
        physical.add(athletics);
        String speed = Util.translate("stats.speed");
        if (this.species == Species.DONKEY) {
            speed += ": " + judgeStat12("speed", 0, 12);
        }
        else {
            speed += "\n";
            speed += "  " + Util.translate("stats.speed1") + ": " + judgeStat("speed", 0, 4) + "\n";
            speed += "  " + Util.translate("stats.speed2") + ": " + judgeStat("speed", 4, 8) + "\n";
            speed += "  " + Util.translate("stats.speed3") + ": " + judgeStat("speed", 8, 12);
        }
        physical.add(speed);
        String jump = Util.translate("stats.jump");
        if (this.species == Species.DONKEY) {
            jump += ": " + judgeStat12("jump", 0, 12);
        }
        else {
            jump += "\n";
            jump += "  " + Util.translate("stats.jump1") + ": " + judgeStat("jump", 0, 4) + "\n";
            jump += "  " + Util.translate("stats.jump2") + ": " + judgeStat("jump", 4, 8) + "\n";
            jump += "  " + Util.translate("stats.jump3") + ": " + judgeStat("jump", 8, 12);
        }
        physical.add(jump);
        physical.add(healthEffects);
        if (HorseConfig.GENETICS.useGeneticStats.get() 
            && HorseConfig.GENETICS.bookShowsTraits.get()) {
            contents.add(physical);
        }

        List<Gene> colorgenelist = ImmutableList.of(Gene.extension, 
            Gene.agouti, Gene.dun, Gene.gray, Gene.cream, Gene.silver, 
            Gene.champagne, Gene.KIT, Gene.frame, Gene.MITF, Gene.leopard, 
            Gene.PATN1, Gene.mushroom, Gene.tiger_eye);
        if (this.species == Species.DONKEY) {
            colorgenelist = ImmutableList.of(Gene.extension, Gene.agouti, Gene.KIT, Gene.color);
        }
        ArrayList<String> test_results = new ArrayList<>();
        if (HorseConfig.GENETICS.bookShowsGenes.get()) {
            test_results.add(Util.translate("book.genetic_color"));
            listGenes(test_results, colorgenelist);
            if (HorseConfig.COMMON.enableSizes.get() && this.species != Species.DONKEY) {
                test_results.add(""); // Blank line
                test_results.add(Util.translate("book.genetic_size"));
                listGenes(test_results, ImmutableList.of(Gene.LCORL, Gene.HMGA2));
                test_results.add(""); // Blank line
                // A note saying many other genes also affect size
                test_results.add(Util.translate("book.size_disclaimer"));
            }
        }
        if (!test_results.isEmpty()) {
            contents.add(test_results);
        }
        return contents;
    }

    @OnlyIn(Dist.CLIENT)
    public void setTexturePaths()
    {
        this.textureLayers = HorseColorCalculator.getTexturePaths(this);
    }

    public String genesToString() {
        String answer = entity.isMale()? "M" : "F";
        String genes = entity.getGeneData();
        for (int i = 0; i < genes.length(); ++i) {
            answer += String.format("%1$02X", (int)genes.charAt(i));
        }
        return answer;
    }

    public void genesFromString(String s) {
        // Before M/F was added, all strings were multiples of 8 long
        if (s.length() % 8 != 0) {
            String g = s.substring(0, 1);
            entity.setMale(g.equals("M"));
            s = s.substring(1);
        }

        String genes = "";
        for (int i = 0; i < s.length() / 4; ++i) {
            for (int n = 0; n < 2; ++n) {
                String c = s.substring(4 * i + 2 * n, 4 * i + 2 * n + 2);
                genes += (char)Short.parseShort(c, 16);
            }
        }
        entity.setGeneData(genes);
    }

    private void setGenericGenes(String name, int len, int val) {
        for (int i = 0; i < len; ++i) {
            Gene gene = Gene.valueOf(name + i);
            setAllele(gene, 0, val & 1);
            val = val >>> 1;
            setAllele(gene, 1, val & 1);
            val = val >>> 1;
        }
    }

    public boolean isValidGeneString(String s) {
        if (s.length() < 2) {
            return false;
        }
        // Genderless from older version
        if (s.length() % 8 == 0) {
            return s.matches("[0-9a-fA-F]*");
        }
        String g = s.substring(0, 1);
        if (!g.equals("M") && !g.equals("F")) {
            return false;
        }
        s = s.substring(1);
        // Two hexadecimal characters per 1-byte allele, two alleles per gene
        if (s.length() % 4 != 0) {
            return false;
        }
        return s.matches("[0-9a-fA-F]*");
    }
}
