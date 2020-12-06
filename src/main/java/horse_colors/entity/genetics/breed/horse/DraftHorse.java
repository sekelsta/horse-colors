package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.*;

import sekelsta.horse_colors.entity.genetics.breed.*;

public class DraftHorse {
    public static Breed breed = new Breed(DefaultHorse.breed);

    static {
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("LCORL", ImmutableList.of(
            0.05f,  // Normal
            1f      // Large
        ));
        GENES.put("HMGA2", ImmutableList.of(
            1f,     // Normal
            1f      // Small
        ));

        GENES.put("size0", ImmutableList.of(
            0.1f,   // Normal
            1f      // Larger
        ));
        GENES.put("size1", ImmutableList.of(
            0.2f,   // Normal
            1f      // Larger
        ));
        GENES.put("size2", ImmutableList.of(
            0.1f,   // Normal
            0.3f,   // Slightly larger
            0.8f,   // Larger
            1f      // Largest
        ));
        GENES.put("size3", ImmutableList.of(
            0.95f,  // Normal
            1f      // Smaller
        ));
        GENES.put("size4", ImmutableList.of(
            0.6f,   // Normal
            0.9f,   // Slightly smaller
            0.95f,  // Small
            0.99f,  // Smaller
            1f      // Smallest
        ));

        ImmutableList<Float> size_minor = ImmutableList.of(
            0.1f, 0.2f, 0.25f, 0.95f, 1f
        );
        ImmutableList<Float> size_subtle = ImmutableList.of(
            0.05f, 0.15f, 0.19f, 0.39f, 0.42f, 0.67f, 0.69f, 0.99f, 1f
        );
        for (int i = 0; i < 8; ++i) {
            GENES.put("size_subtle" + i, size_subtle);
        }
    }
}
