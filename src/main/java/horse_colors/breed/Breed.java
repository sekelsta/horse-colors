package sekelsta.horse_colors.breed;

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
    // If a gene is not in this breed's map, it will add it from the parent
    public Breed parent = null;

    public static final List<Float> DEFAULT_FREQUENCIES = ImmutableList.of(1f);

    public Breed() {
        genes = new HashMap<>();
    }

    public Breed(Breed copy) {
        genes = new HashMap(copy.genes);
    }

    public boolean contains(String gene) {
        return genes.containsKey(gene) 
                || (parent != null && parent.contains(gene));
    }

    /* Returns the list of frequencies for a gene. If needed, will copy the info
    from the parent. */
    public List<Float> get(String gene) {
        if (!genes.containsKey(gene)) {
            if (parent != null && parent.contains(gene)) {
                genes.put(gene, parent.get(gene));
            }
            else {
                return DEFAULT_FREQUENCIES;
            }
        }
        return genes.get(gene);
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
