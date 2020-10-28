package sekelsta.horse_colors.entity.genetics.breed;

import com.google.common.collect.ImmutableList;
import java.util.*;

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
        for (String s : geneKeys) {
            List<Float> l1 = ImmutableList.of(1f);
            if (this.genes.containsKey(s)) {
                l1 = this.genes.get(s);
            }
            List<Float> l2 = ImmutableList.of(1f);
            if (breed.genes.containsKey(s)) {
                l2 = breed.genes.get(s);
            }
            this.genes.put(s, mergeList(l1, l2, weight));
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
}
