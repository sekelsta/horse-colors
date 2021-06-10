package sekelsta.horse_colors.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class MammothDonkey {
    public static Breed breed = new Breed(DefaultDonkey.breed);

    static {
        // Set these to be fairly common for the sake of not changing Minecraft
        // mechanics so much
        breed.population = 2000000;
        Map<Gene, List<Float>> GENES = breed.genes;

        GENES.put(Gene.donkey_size0, ImmutableList.of(
            0.1f,
            0.3f,
            1.0f
        ));
        GENES.put(Gene.donkey_size1, ImmutableList.of(
            0.1f,
            0.3f,
            1.0f
        ));
        GENES.put(Gene.donkey_size2, ImmutableList.of(
            0.8f,
            0.9f,
            1.0f
        ));
        GENES.put(Gene.donkey_size3, ImmutableList.of(
            1.0f,
            1.0f
        ));
        GENES.put(Gene.donkey_size4, ImmutableList.of(
            0.9f,
            1.0f
        ));
        GENES.put(Gene.donkey_size5, ImmutableList.of(
            0.4f,
            1.0f
        ));
        GENES.put(Gene.donkey_size6, ImmutableList.of(
            1.0f,
            1.0f
        ));
    }
}
