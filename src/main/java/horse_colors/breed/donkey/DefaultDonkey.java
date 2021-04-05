package sekelsta.horse_colors.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.Breed;

public class DefaultDonkey {
    public static Breed breed = new Breed(Breed.load("donkey_default_size"));

    static {
        breed.parent = BaseDonkey.breed;
        Map<String, List<Float>> genes = breed.genes;

        genes.put("cameo", ImmutableList.of(
            0.99f,  // Non cameo
            1f      // Cameo
        ));
        genes.put("ivory", ImmutableList.of(
            0.9f,   // Non ivory
            1f      // Ivory
        ));
        genes.put("color", ImmutableList.of(
            0.995f,   // Full color
            1f      // Albino
        ));
    }
}
