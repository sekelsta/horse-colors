package sekelsta.horse_colors.genetics.breed;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Breed {
    // The breed's internal name
    public String name;
    // The strings represent gene names
    // The list of floats determines the likelihood of each allele
    public Map<String, List<Float>> colors;

    public Breed() {
        colors = new HashMap<>();
    }

    public Breed(Breed copy) {
        colors = new HashMap(copy.colors);
    }
}
