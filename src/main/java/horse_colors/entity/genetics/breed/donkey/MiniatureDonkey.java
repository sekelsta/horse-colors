package sekelsta.horse_colors.entity.genetics.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

public class MiniatureDonkey {
    public static Breed breed = new Breed(DefaultDonkey.breed);

    static {
        // Set these to be not super rare
        breed.population = 1000000;
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("donkey_size0", ImmutableList.of(
            1f,
            1.0f,
            1.0f
        ));
        GENES.put("donkey_size1", ImmutableList.of(
            1f,
            1.0f,
            1.0f
        ));
        GENES.put("donkey_size2", ImmutableList.of(
            0.0f,
            0.5f,
            1.0f
        ));
        GENES.put("donkey_size3", ImmutableList.of(
            0.1f,
            1.0f
        ));
        GENES.put("donkey_size4", ImmutableList.of(
            0.0f,
            1.0f
        ));
        GENES.put("donkey_size5", ImmutableList.of(
            0.9f,
            1.0f
        ));
        GENES.put("donkey_size6", ImmutableList.of(
            0.5f,
            1.0f
        ));
    }
}
