package sekelsta.horse_colors.genetics;

import java.util.*;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.renderer.CustomLayeredTexture;
import sekelsta.horse_colors.renderer.TextureLayer;

public abstract class Genome {
    public abstract List<String> listGenes();
    public abstract List<String> listGenericChromosomes();
    public abstract List<String> listStats();
    public List<Linkage> listLinkages() {
        ArrayList linkages = new ArrayList<Genome.Linkage>();
        for (String gene : listGenes()) {
            linkages.add(new Genome.Linkage(gene));
        }
        return linkages;
    }

    protected IGeneticEntity entity;

    protected String textureCacheName;
    protected List<TextureLayer> textureLayers;

    // Make sure to use this.entity.getRand() instead for anything
    // that should be consistent across worlds with the same seed
    public static java.util.Random rand = new java.util.Random();

    public Genome() {
        this.entity = new FakeGeneticEntity();
    }

    public Genome(IGeneticEntity entityIn) {
        this.entity = entityIn;
    }

    public void resetTexture() {
        this.textureCacheName = null;
    }

    public abstract List<List<String>> getBookContents();
    public abstract void setTexturePaths();
    public abstract String genesToString();
    public abstract void genesFromString(String s);
    public abstract boolean isValidGeneString(String s);

    @OnlyIn(Dist.CLIENT)
    public String getTexture()
    {
        if (this.textureCacheName == null)
        {
            this.setTexturePaths();
        }
        return this.textureCacheName;
    }

    @OnlyIn(Dist.CLIENT)
    public List<TextureLayer> getVariantTexturePaths()
    {
        if (this.textureCacheName == null)
        {
            this.setTexturePaths();
        }

        return this.textureLayers;
    }

    public abstract int getGeneSize(String gene);

    public int getChromosome(String name) {
        return entity.getChromosome(name);
    }

    public void setNamedGene(String name, int val)
    {
        String chr = getGeneChromosome(name);
        entity.setChromosome(chr, (entity.getChromosome(chr) & (~getGeneLoci(name))) 
            | (val << (getGenePos(name) % 32)));
    }

    public int getNamedGene(String name)
    {
        String chr = getGeneChromosome(name);
        // Use unsigned right shift to avoid returning negative numbers
        return (entity.getChromosome(chr) & getGeneLoci(name)) >>> getGenePos(name);
    }

    // This returns the chromosome masked to contain only the relevent bits
    public int getRawStat(String name)
    {
        String chr = listGenericChromosomes().get(getStatPos(name) / 32);
        return entity.getChromosome(chr) & getStatLoci(name);
    }

    // This returns the number of '1' bits in the stat's position
    public int getStatValue(String name)
    {
        int val = getRawStat(name);
        return countBits(val);
    }

    public int countBits(int val) {
        int count = 0;
        for (int i = 0; i < 32; ++i)
        {
            count += ((val % 2) + 2) % 2;
            val >>= 1;
        }
        return count;
    }

    public int countDiffs(int val) {
        int count = 0;
        for (int i = 0; i < 16; ++i)
        {
            int one = ((val % 2) + 2) % 2;
            val >>= 1;
            int two = ((val % 2) + 2) % 2;
            val >>= 1;
            count += one ^ two;
        }
        return count;
    }

    public int getGenePos(String name)
    {
        return getPos(name, listGenes());
    }

    public int getStatPos(String name)
    {
        return getPos(name, listStats());
    }

    private int getPos(String name, List<String> genes)
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

    public int getGeneLoci(String gene)
    {
        return getLoci(gene, getGenePos(gene));
    }

    public int getStatLoci(String gene)
    {
        return getLoci(gene, getStatPos(gene));
    }

    /* This returns a bitmask which is 1 where the gene is stored and 0 everywhere else. */
    private int getLoci(String gene, int pos)
    {
        return ((1 << (2 * getGeneSize(gene))) - 1) << (pos % 32);
    }

    public String getGeneChromosome(String gene)
    {
        // Which of the ints full of genes ours is on
        return Integer.toString(getGenePos(gene) / 32);
    }

    public int getAllele(String name, int n)
    {
        int gene = getNamedGene(name);
        gene >>= n * getGeneSize(name);
        gene %= 1 << getGeneSize(name);
        return gene;
    }

    public void setAllele(String name, int n, int v)
    {
        int other = getAllele(name, 1 - n);
        int size = getGeneSize(name);
        setNamedGene(name, (other << ((1 - n) * size)) | (v << (n * size)));
    }

    // Replace the given allele with a random one.
    // It may be the same as before.
    public void mutateAllele(String gene, int n) {
        Map<String, List<Float>> map = entity.getSpawnFrequencies();
        if (!map.containsKey(gene)) {
            return;
        }
        List<Float> frequencies = map.get(gene);
        List<Integer> allowedAlleles = new ArrayList<>();
        float val = 0;
        for (int i = 0; i < frequencies.size(); ++i) {
            if (val >= 1f) {
                break;
            }
            if (val < frequencies.get(i)) {
                allowedAlleles.add(i);
                val = frequencies.get(i);
            }
        }
        int size = allowedAlleles.size();
        int v = allowedAlleles.get(this.rand.nextInt(size));
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


    public void mutateGenericChromosome(String name, double p) {
        // xor with an int where each digit has a p / 2 chance of being 1
        // This is equivalent to picking a random replacement with p probability
        // because half the time that would pick the same value as before
        entity.setChromosome(name, entity.getChromosome(name) ^ mutateIntMask(p / 2));
    }

    public void mutate() {
        double p = HorseConfig.GENETICS.mutationChance.get();
        for (String gene : listGenes()) {
            int a = getAllele(gene, 0);
            int b = getAllele(gene, 1);
            mutateAlleleChance(gene, 0, p);
            mutateAlleleChance(gene, 1, p);
        }
        for (String stat : listGenericChromosomes()) {
            mutateGenericChromosome(stat, p);
        }
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

    public int getRandomGenericGenes(int n, int data, float linkage)
    {
        int rand = this.rand.nextInt(2);
        int answer = 0;
        for (int i = 0; i < 16; i++)
        {
            if (this.rand.nextFloat() < linkage)
            {
                rand = 1 - rand;
            }
            answer += ((data & (1 << (2 * i + rand))) >> rand) << n;
        }
        return answer;
    }

    // 
    public void inheritNamedGenes(Genome parent1, Genome parent2) {
        int rand1 = this.rand.nextInt(2);
        int rand2 = this.rand.nextInt(2);
        for (Linkage link : this.listLinkages()) {
            int allele1 = parent1.getAllele(link.gene, rand1);
            int allele2 = parent2.getAllele(link.gene, rand2);
            this.setAllele(link.gene, 0, allele1);
            this.setAllele(link.gene, 1, allele2);
            if (this.rand.nextFloat() < link.p) {
                rand1 = 1 - rand1;
            }
            if (this.rand.nextFloat() < link.p) {
                rand2 = 1 - rand2;
            }
        }
    }

    // Convert chromosome from int to pretty print string
    public static String chrToStr(int chr) {
        String s = "";
        for (int i = 16; i >0; i--) {
            s += (chr >>> (2 * i - 1)) & 1;
            s += (chr >>> (2 * i - 2)) & 1;
            if (i > 1) {
                s += " ";
            }
        }
        return s;
    }

    public void inheritGenericGenes(Genome parent1, Genome parent2) {
        float linkage = 0.5f;
        for (String chr : this.listGenericChromosomes()) {
            if (chr.startsWith("mhc")) {
                linkage = 0.05f;
            }
            else {
                linkage = 0.5f;
            }
            int mother = parent1.getRandomGenericGenes(1, parent1.getChromosome(chr), linkage);
            int father = parent2.getRandomGenericGenes(0, parent2.getChromosome(chr), linkage);
            this.entity.setChromosome(chr, mother | father);
        }
    }

    public void inheritGenes(Genome parent1, Genome parent2) {
        inheritNamedGenes(parent1, parent2);
        inheritGenericGenes(parent1, parent2);
        mutate();
    }

    // Chromosomal linkage for storing in a list
    // p is the probability there are an odd number of crossovers between this gene and the next
    public static class Linkage {
        public String gene;
        public float p;
        public Linkage(String gene, float p) {
            this.gene = gene;
            this.p = p;
        }
        public Linkage(String gene) {
            this.gene = gene;
            this.p = 0.5f;
        }
    }
}
