package sekelsta.horse_colors.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class MiniatureDonkey {
    public static Breed breed = new Breed(DefaultDonkey.breed);

    static {
        // Set these to be not super rare
        breed.population = 1000000;
        Map<Gene, List<Float>> GENES = breed.genes;

        GENES.put(Gene.donkey_size0, ImmutableList.of(
            1f,
            1.0f,
            1.0f
        ));
        GENES.put(Gene.donkey_size1, ImmutableList.of(
            1f,
            1.0f,
            1.0f
        ));
        GENES.put(Gene.donkey_size2, ImmutableList.of(
            0.0f,
            0.5f,
            1.0f
        ));
        GENES.put(Gene.donkey_size3, ImmutableList.of(
            0.1f,
            1.0f
        ));
        GENES.put(Gene.donkey_size4, ImmutableList.of(
            0.0f,
            1.0f
        ));
        GENES.put(Gene.donkey_size5, ImmutableList.of(
            0.9f,
            1.0f
        ));
        GENES.put(Gene.donkey_size6, ImmutableList.of(
            0.5f,
            1.0f
        ));
    }
}
