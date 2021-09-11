package sekelsta.horse_colors.breed;

import sekelsta.horse_colors.HorseColors;
import com.google.common.collect.ImmutableList;
import java.util.*;
import net.minecraft.resources.ResourceLocation;

public class Breed<T extends Enum<T>> {
    // The breed's internal name
    public String name;
    // How many members the breed has in the real world
    public int population;
    // The enums represent genes
    // The list of floats determines the likelihood of each allele
    public Map<T, List<Float>> genes;
    // If a gene is not in this breed's map, it will add it from the parent
    public Breed<T> parent = null;

    public static final List<Float> DEFAULT_FREQUENCIES = ImmutableList.of(1f);

    public Breed() {
    }

    public Breed(Class<T> keyType) {
        this.genes = new EnumMap<T, List<Float>>(keyType);
    }

    public Breed(Breed<T> copy) {
        this.genes = new EnumMap(copy.genes);
        this.parent = copy.parent;
    }

    public boolean contains(T gene) {
        return genes.containsKey(gene) 
                || (parent != null && parent.contains(gene));
    }

    /* Returns the list of frequencies for a gene. If needed, will copy the info
    from the parent. */
    public List<Float> get(T gene) {
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

    // Write in a format that can be parsed as a python dictionary
    public String getMapString() {
        String p = "{";
        boolean first_gene = true;
        for (T gene : this.genes.keySet()) {
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
        return p;
    }

    public String toString() {
        String s = "Breed: " + this.name + "\nPopulation: " + this.population
            + "\nGenes:\n" + this.getMapString() + "\nParent: ";
        if (parent == null) {
            return s + "null\n";
        }
        String p = parent.toString();
        return s + "\n" + p.replace("\n", "\n    ");
    }
}
