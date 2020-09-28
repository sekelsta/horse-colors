package sekelsta.horse_colors.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

// I'm taking a broad view of "tarpan" here, so these are all the 
// pre-domestic horses more closely related to domestic horses than to takhis.
public class Tarpan {
    public static Breed breed = new Breed(BaseHorse.breed);

    static {
        breed.name = "tarpan";
        Map<String, List<Float>> COLORS = breed.colors;

        COLORS.put("dun", ImmutableList.of(
            0.0f,   // Non-dun 2
            0.5f,   // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        COLORS.put("liver", ImmutableList.of(
            0.25f,  // Liver
            1f      // Non-liver
        ));
        COLORS.put("dapple", ImmutableList.of(
            0.75f,  // Non-dapple
            1f      // Dapple
        ));
        COLORS.put("flaxen1", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("sooty2", ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        COLORS.put("mealy1", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("mealy2", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("light_belly", ImmutableList.of(
            0.9f,   // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        COLORS.put("reduced_points", ImmutableList.of(
            0.95f,  // Higher leg black
            1f      // Lower leg black
        ));
    }

    public static void init() {}
}
