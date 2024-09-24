package sekelsta.horse_colors.entity.genetics;

import java.util.*;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseConfig;
import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.client.renderer.CustomLayeredTexture;
import sekelsta.horse_colors.client.renderer.TextureLayerGroup;
import sekelsta.horse_colors.util.RandomSupplier;
import sekelsta.horse_colors.util.Util;

public abstract class Genome {
    public final Species species;
    public abstract List<Enum> listGenes();
    public List<Linkage> listLinkages() {
        ArrayList linkages = new ArrayList<Genome.Linkage>();
        for (Enum gene : listGenes()) {
            linkages.add(new Genome.Linkage(gene));
        }
        return linkages;
    }

    protected IGeneticEntity entity;

    protected TextureLayerGroup textureLayers;

    protected final RandomSupplier randSource;

    public static java.util.Random rand = new java.util.Random();

    public Genome(Species species, RandomSupplier rand) {
        this(species, new FakeGeneticEntity(), rand);
    }

    public Genome(Species species, IGeneticEntity entityIn, RandomSupplier rand) {
        this.species = species;
        this.entity = entityIn;
        this.randSource = rand;
    }

    public void resetTexture() {
        this.textureLayers = null;
    }

    public abstract List<List<String>> getBookContents();
    public abstract void setTexturePaths();
    public abstract String genesToString();
    public abstract void genesFromString(String s);
    public abstract boolean isValidGeneString(String s);

    @OnlyIn(Dist.CLIENT)
    public TextureLayerGroup getTexturePaths()
    {
        if (this.textureLayers == null)
        {
            this.setTexturePaths();
        }

        return this.textureLayers;
    }

    public int getAllele(Enum gene, int n) {
        // Each gene has two alleles, so double the index
        int index = 2 * gene.ordinal() + n;
        if (index >= entity.getGeneData().length()) {
            return 0;
        }
        return (int)entity.getGeneData().charAt(index);
    }

    public void setAllele(Enum gene, int n, int v) {
        int index = 2 * gene.ordinal() + n;
        StringBuffer buffer = new StringBuffer(entity.getGeneData());
        // Append null characters until it is long enough
        if (buffer.length() <= index) {
            buffer.setLength(index + 1);
        }
        buffer.setCharAt(index, (char)v);
        entity.setGeneData(new String(buffer));
    }

    public List<Integer> getAllowedAlleles(Enum gene, Breed breed) {
        if (!breed.contains(gene)) {
            return null;
        }
        List<Float> frequencies = breed.get(gene);
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
        return allowedAlleles;
    }

    // Replace the given allele with a random one.
    // It may be the same as before.
    public void mutateAllele(Enum gene, int n) {
        Breed breed = entity.getDefaultBreed();
        List<Integer> allowedAlleles = getAllowedAlleles(gene, breed);
        if (allowedAlleles == null) {
            return;
        }
        int size = allowedAlleles.size();
        int v = allowedAlleles.get(this.rand.nextInt(size));
        setAllele(gene, n, v);
    }

    // Will mutate with p probability
    public void mutateAlleleChance(Enum gene, int n, double p) {
        if (this.rand.nextDouble() < p) {
            mutateAllele(gene, n);
        }
    }

    public void mutate() {
        double p = HorseConfig.GENETICS.mutationChance.get();
        for (Enum gene : listGenes()) {
            int a = getAllele(gene, 0);
            int b = getAllele(gene, 1);
            mutateAlleleChance(gene, 0, p);
            mutateAlleleChance(gene, 1, p);
        }
    }

    // Add together allele values for a set of genes named according to
    // name + n, where min <= n < max
    public int sumGenes(Class enumType, String name, int min, int max) {
        int sum = 0;
        for (int i = min; i < max; ++i) {
            Enum e = Enum.valueOf(enumType, name + i);
            sum += getAllele(e, 0);
            sum += getAllele(e, 1);
        }
        return sum;
    }

    public boolean hasAllele(Enum gene, int allele)
    {
        return getAllele(gene, 0) == allele || getAllele(gene, 1) == allele;
    }

    public int getMaxAllele(Enum gene)
    {
        return Math.max(getAllele(gene, 0), getAllele(gene, 1));
    }

    public boolean isHomozygous(Enum gene, int allele)
    {
        return  getAllele(gene, 0) == allele && getAllele(gene, 1) == allele;
    }

    public int countAlleles(Enum gene, int allele) {
        int count = 0;
        if (getAllele(gene, 0) == allele) {
            count++;
        }
        if (getAllele(gene, 1) == allele) {
            count++;
        }
        return count;
    }

    public void inheritGenes(Genome parent1, Genome parent2) {
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
        mutate();
    }

    public int getRandom(String key) {
        return randSource.getVal(key, this.entity.getSeed());
    }

    // Returns the gene data as a base 64 string of printable characters
    public String getBase64() {
        String genes = entity.getGeneData();
        StringBuilder builder = new StringBuilder(genes.length());
        for (int i = 0; i < genes.length(); ++i) {
            int v = (int)genes.charAt(i);
            builder.append(Util.toBase64(v));
        }
        return builder.toString();
    }

    // Reads the gene data from a base 64 string
    public void setFromBase64(String base64Genes) {
        StringBuilder builder = new StringBuilder(base64Genes.length());
        for (int i = 0; i < base64Genes.length(); ++i) {
            char c = base64Genes.charAt(i);
            int v = Util.fromBase64(c);
            builder.append((char)v);
        }
        entity.setGeneData(builder.toString());
    }

    // Chromosomal linkage for storing in a list
    // p is the probability there are an odd number of crossovers between this gene and the next
    public static class Linkage {
        public Enum gene;
        public float p;
        public Linkage(Enum gene, float p) {
            this.gene = gene;
            this.p = p;
        }
        public Linkage(Enum gene) {
            this.gene = gene;
            this.p = 0.5f;
        }
    }
}
