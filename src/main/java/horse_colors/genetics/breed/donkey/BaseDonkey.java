package sekelsta.horse_colors.genetics.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

public class BaseDonkey {
    public static Breed breed = new Breed(BaseEquine.breed);

    static {
        Map<String, List<Float>> COLORS = breed.colors;

        // TODO: Somali wild asses don't have the shoulder cross
        COLORS.put("cross", ImmutableList.of(
            0f,     // No shoulder stripe
            1f      // Shoulder stripe
        ));
        COLORS.put("light_legs", ImmutableList.of(
            0.5f,   // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        COLORS.put("less_light_legs", ImmutableList.of(
            0.5f,   // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        COLORS.put("donkey_dun", ImmutableList.of(
            0.5f,   // Dun
            0.95f,  // Non-dun with cross
            1f,     // Non-dun, no cross
            0f      // Unused
        ));
    }
}
