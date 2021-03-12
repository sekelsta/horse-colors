package sekelsta.horse_colors.entity.genetics.breed;

import sekelsta.horse_colors.HorseColors;
import com.google.common.collect.ImmutableList;
import java.util.*;
import net.minecraft.util.ResourceLocation;

public class Breed {
    // The breed's internal name
    public String name;
    // How many members the breed has in the real world
    public int population;
    // The strings represent gene names
    // The list of floats determines the likelihood of each allele
    public Map<String, List<Float>> genes;

    public Breed() {
        genes = new HashMap<>();
    }

    public Breed(Breed copy) {
        genes = new HashMap(copy.genes);
    }

    // Mix with another breed.
    // Mixes 'weight' of the other breed with '1 - weight' of this one.
    public void merge(Breed breed, float weight) {
        // Merge colors (named genes)
        TreeSet<String> geneKeys = new TreeSet(this.genes.keySet());
        for (String key : breed.genes.keySet()) {
            geneKeys.add(key);
        }
        // If either breed has no data for a certain gene, use the frequency
        // from the breed that does have data.
        for (String s : geneKeys) {
            if (breed.genes.containsKey(s)) {
                List<Float> other = breed.genes.get(s);
                if (!this.genes.containsKey(s)) {
                    this.genes.put(s, other);
                }
                else {
                    List<Float> l = this.genes.get(s);
                    this.genes.put(s, mergeList(l, other, weight));
                }
            }
        }
    }

    public List<Float> mergeList(List<Float> base, List<Float> additional, float weight) {
        ArrayList<Float> output = new ArrayList<>();
        int size = Math.max(base.size(), additional.size());
        for (int i = 0; i < size; ++i) {
            float baseNum = 1f;
            if (base.size() > i) {
                baseNum = base.get(i);
            }
            float otherNum = 1f;
            if (additional.size() > i) {
                otherNum = additional.get(i);
            }
            output.add(baseNum * (1 - weight) + otherNum * weight);
        }
        return output;
    }

    public static Breed load(String name) {
        return BreedManager.getBreed(new ResourceLocation(HorseColors.MODID, name));
    }

    // Print in a format that can be parsed as a python dictionary
    public void print() {
        String p = "{";
        boolean first_gene = true;
        for (String gene : this.genes.keySet()) {
            if (!first_gene) {
                p += ", ";
            }
            first_gene = false;
            p += "'" + gene + "': [";
            boolean first_float = true;
            for (float f : this.genes.get(gene)) {
                if (!first_float) {
                    p += ", ";
                }
                first_float = false;
                p += f;
            }
            p += "]";
        }
        p += "}";
        System.out.println(p);
    }
}
