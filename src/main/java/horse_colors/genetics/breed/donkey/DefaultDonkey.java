package sekelsta.horse_colors.genetics.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

public class DefaultDonkey {
    public static Breed breed = new Breed(BaseDonkey.breed);

    static {
        Map<String, List<Float>> COLORS = breed.colors;

        COLORS.put("cameo", ImmutableList.of(
            0.99f,  // Non cameo
            1f      // Cameo
        ));
        COLORS.put("ivory", ImmutableList.of(
            0.9f,   // Non ivory
            1f      // Ivory
        ));
    }
}
