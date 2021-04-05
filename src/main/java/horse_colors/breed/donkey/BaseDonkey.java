package sekelsta.horse_colors.breed.donkey;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.BaseEquine;
import sekelsta.horse_colors.breed.Breed;

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

        GENES.put("double_ovulation", ImmutableList.of(
            0.5f,   // Twins less likely
            1.0f    // Twins more likely
        ));

        GENES.put("donkey_size0", ImmutableList.of(
            0.6f,
            0.8f,
            1.0f
        ));
        GENES.put("donkey_size1", ImmutableList.of(
            0.6f,
            0.8f,
            1.0f
        ));
        GENES.put("donkey_size2", ImmutableList.of(
            0.6f,
            0.8f,
            1.0f
        ));
        GENES.put("donkey_size3", ImmutableList.of(
            0.9f,
            1.0f
        ));
        GENES.put("donkey_size4", ImmutableList.of(
            0.8f,
            1.0f
        ));
        GENES.put("donkey_size5", ImmutableList.of(
            0.8f,
            1.0f
        ));
        GENES.put("donkey_size6", ImmutableList.of(
            0.9f,
            1.0f
        ));

        // Make the max speed and jump of donkeys lower than that of horses
        ImmutableList<Float> one = ImmutableList.of(1f);
        for (int i = 0; i < 12; i += 2) {
            GENES.put("speed" + i, one);
            GENES.put("jump" + i, one);
        }
        for (int i = 0; i < 8; i += 2) {
            GENES.put("athletics" + i, one);
        }
        // Make the max heaalth of donkeys higher on average without changing
        // the possible range
        ImmutableList<Float> good = ImmutableList.of(4f/11f, 1f);
        for (int i = 0; i < 12; ++i) {
            GENES.put("health" + i, good);
        }
        // Now change the range a little, and make hoof health higher
        GENES.put("health8", ImmutableList.of(0f, 1f));
        // The overall effect moves average donkey health to 16, in a range
        // of 2-24
    }
}
