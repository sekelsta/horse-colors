package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.*;

import sekelsta.horse_colors.entity.genetics.breed.*;

public class MiniatureHorse {
    public static Breed breed = new Breed(DefaultHorse.breed);

    static {
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("LCORL", ImmutableList.of(
            0.95f,  // Normal
            1f      // Large
        ));
        GENES.put("HMGA2", ImmutableList.of(
            0.5f,   // Normal
            1f      // Small
        ));

        GENES.put("size0", ImmutableList.of(
            0.9f,   // Normal
            1f      // Larger
        ));
        GENES.put("size1", ImmutableList.of(
            0.9f,   // Normal
            1f      // Larger
        ));
        GENES.put("size2", ImmutableList.of(
            0.8f,   // Normal
            0.9f,   // Slightly larger
            0.95f,  // Larger
            1f      // Largest
        ));
        GENES.put("size3", ImmutableList.of(
            0.1f,   // Normal
            1f      // Smaller
        ));
        GENES.put("size4", ImmutableList.of(
            0.1f,   // Normal
            0.2f,   // Slightly smaller
            0.4f,   // Small
            0.8f,   // Smaller
            1f      // Smallest
        ));

        ImmutableList<Float> size_minor = ImmutableList.of(
            0.1f, 0.15f, 0.25f, 0.3f, 1f
        );
        ImmutableList<Float> size_subtle = ImmutableList.of(
            0.05f, 0.09f, 0.19f, 0.22f, 0.42f, 0.44f, 0.69f, 0.70f, 1f
        );
        for (int i = 0; i < 8; ++i) {
            GENES.put("size_minor" + i, size_minor);
            GENES.put("size_subtle" + i, size_subtle);
        }
    }
}
