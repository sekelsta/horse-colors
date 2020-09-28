package sekelsta.horse_colors.genetics.breed;

import com.google.common.collect.ImmutableList;
import java.util.*;

public class Breed {
    // The breed's internal name
    public String name;
    // How many members the breed has in the real world
    public int population;
    // The strings represent gene names
    // The list of floats determines the likelihood of each allele
    public Map<String, List<Float>> colors;

    public Breed() {
        colors = new HashMap<>();
    }

    public Breed(Breed copy) {
        colors = new HashMap(copy.colors);
    }

    // Mix with another breed.
    // Mixes 'weight' of the other breed with '1 - weight' of this one.
    public void merge(Breed breed, float weight) {
        // Merge colors (named genes)
        TreeSet<String> colorKeys = new TreeSet(this.colors.keySet());
        for (String key : breed.colors.keySet()) {
            colorKeys.add(key);
        }
        for (String s : colorKeys) {
            List<Float> l1 = ImmutableList.of(1f);
            if (this.colors.containsKey(s)) {
                l1 = this.colors.get(s);
            }
            List<Float> l2 = ImmutableList.of(1f);
            if (breed.colors.containsKey(s)) {
                l2 = breed.colors.get(s);
            }
            this.colors.put(s, mergeList(l1, l2, weight));
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
