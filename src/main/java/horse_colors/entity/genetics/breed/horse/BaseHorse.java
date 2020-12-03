package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

// This represents the wild horse before domestication
public class BaseHorse {
    public static Breed breed = new Breed(BaseEquine.breed);

    static {
        Map<String, List<Float>> COLORS = breed.colors;

        COLORS.put("dun", ImmutableList.of(
            0.0f,   // Non-dun 2
            0.0f,   // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        COLORS.put("liver", ImmutableList.of(
            0.1f,   // Liver
            1f      // Non-liver
        ));
        COLORS.put("flaxen1", ImmutableList.of(
            0.1f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("flaxen2", ImmutableList.of(
            0.1f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("sooty2", ImmutableList.of(
            0.9f,   // Non-sooty
            1f      // Sooty
        ));
        COLORS.put("mealy1", ImmutableList.of(
            0.25f,  // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("mealy2", ImmutableList.of(
            0.25f,  // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("white_suppression", ImmutableList.of(
            15f / 16f,  // Non white-suppression
            1f          // White suppression
        ));
        COLORS.put("reduced_points", ImmutableList.of(
            0.9f,   // Higher leg black
            1f      // Lower leg black
        ));
        COLORS.put("flaxen_boost", ImmutableList.of(
            0.5f,   // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        COLORS.put("light_dun", ImmutableList.of(
            1f,     // Darker dun
            0f      // Lighter dun
        ));
    }
}
