package sekelsta.horse_colors.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

// I'm taking a broad view of "tarpan" here, so these are all the 
// pre-domestic horses more closely related to domestic horses than to takhis.
public class TarpanEurope {
    public static Breed breed;

    static {
        Tarpan.init();
        breed = new Breed(Tarpan.breed);
        breed.name = "tarpan";
        Map<String, List<Float>> COLORS = breed.colors;

        COLORS.put("leopard", ImmutableList.of(
            31f/32f,    // Non-leopard
            1f          // Leopard
        ));
        COLORS.put("PATN1", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        COLORS.put("PATN2", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        COLORS.put("PATN3", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        COLORS.put("marble", ImmutableList.of(
            0.9f,   // Round leopard spots
            1f      // Stretched leopard spots
        ));
        COLORS.put("leopard_suppression", ImmutableList.of(
            0.92f,  // Full leopard
            1f      // Semileopard
        ));
        COLORS.put("leopard_suppression2", ImmutableList.of(
            0.88f,  // Full leopard
            1f      // Semileopard
        ));
        COLORS.put("PATN_boost1", ImmutableList.of(
            15f / 16f,  // Less pattern
            1f          // More pattern
        ));
        COLORS.put("PATN_boost2", ImmutableList.of(
            15f / 16f,  // Less pattern
            1f          // More pattern
        ));
    }

    public static void init() {}
}
