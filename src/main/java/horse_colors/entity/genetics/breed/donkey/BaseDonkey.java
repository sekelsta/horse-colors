package sekelsta.horse_colors.entity.genetics.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

public class BaseDonkey {
    public static Breed breed = new Breed(BaseEquine.breed);

    static {
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("extension", ImmutableList.of(
            0.2f,   // Red
            1.0f    // Black
        ));
        GENES.put("agouti", ImmutableList.of(
            0.05f,   // Black
            0.15f,   // Seal
            0.1f,   // Seal unused
            0.1f,   // Bay unused
            1f      // Bay
        ));
        // TODO: Somali wild asses don't have the shoulder cross
        GENES.put("cross", ImmutableList.of(
            0f,     // No shoulder stripe
            1f      // Shoulder stripe
        ));
        GENES.put("light_legs", ImmutableList.of(
            0.5f,   // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        GENES.put("less_light_legs", ImmutableList.of(
            0.5f,   // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        GENES.put("donkey_dun", ImmutableList.of(
            0.5f,   // Dun
            0.95f,  // Non-dun with cross
            1f,     // Non-dun, no cross
            0f      // Unused
        ));
    }
}
