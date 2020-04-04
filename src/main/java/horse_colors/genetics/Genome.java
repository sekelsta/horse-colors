package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.ArrayList;

public abstract class Genome {
    public abstract List<String> listGenes();
    public abstract List<String> listStats();

    protected IGeneticEntity entity;

    protected String textureCacheName;
    protected ArrayList<Layer> textureLayers;

    // Make sure to use this.entity.getRand() instead for anything
    // that should be consistent across worlds with the same seed
    public static java.util.Random rand = new java.util.Random();

    public Genome(IGeneticEntity entityIn) {
        this.entity = entityIn;
    }

    public void resetTexture() {
        this.textureCacheName = null;
    }

    public abstract List<String> humanReadableNamedGenes();
    public abstract List<String> humanReadableStats();
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
    public ArrayList<Layer> getVariantTexturePaths()
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
    
    public int getStat(String name)
    {
        int val = entity.getChromosome(name);
        int count = 0;
        for (int i = 0; i < 32; ++i)
        {
            count += ((val % 2) + 2) % 2;
            val >>= 1;
        }
        return count;
    }

    public int getGenePos(String name)
    {
        int i = 0;
        for (String gene : listGenes())
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

    /* This returns a bitmask which is 1 where the gene is stored and 0 everywhere else. */
    public int getGeneLoci(String gene)
    {
        return ((1 << (2 * getGeneSize(gene))) - 1) << (getGenePos(gene) % 32);
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
        entity.setChromosome(name, entity.getChromosome(name) ^ mutateIntMask(p / 2));
    }

    public void mutate() {
        double p = HorseConfig.Common.mutationChance.get();
        for (String gene : listGenes()) {
            mutateAlleleChance(gene, 0, p);
            mutateAlleleChance(gene, 1, p);
        }
        for (String stat : listStats()) {
            mutateStat(stat, p);
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
    /* Argument is the number of genewidths to the left each gene should be
    shifted. */
    public int getRandomGenes(int n, int type)
    {
        int result = 0;
        int random = 0;
        for (String gene : listGenes())
        {
            if (getGenePos(gene) / 32 != type)
            {
                continue;
            }

            random = this.rand.nextInt();
            int next = getNamedGene(gene);
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
            random >>= 1;

            // Add the allele we've selected to the final result
            result |= next << (getGenePos(gene) % 32) << (n * getGeneSize(gene));
        }

        return result;
    }

    public int getRandomGenericGenes(int n, int data)
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

}
