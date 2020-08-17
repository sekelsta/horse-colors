package sekelsta.horse_colors.genetics.breed;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This represents the wild horse before domestication
public class BaseHorse {
    public static HashMap<String, List<Float>> COLORS;

    static {
        BaseEquine.init();
        COLORS = new HashMap<String, List<Float>>(BaseEquine.COLORS);

        COLORS.put("dun", ImmutableList.of(
            0.0f,   // Non-dun 2
            0.12f,  // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        COLORS.put("agouti", ImmutableList.of(
            0.375f,     // Black
            0.5f,       // Seal
            0.5f,       // Seal unused
            0.5f,       // Bay unused
            1f,         // Bay
            0.875f,     // Bay unused
            0.9375f,    // Bay unused
            1.0f        // Bay unused
        ));
        COLORS.put("liver", ImmutableList.of(
            0.25f,  // Liver
            1f      // Non-liver
        ));
        COLORS.put("flaxen1", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("dapple", ImmutableList.of(
            0.75f,  // Non-dapple
            1f      // Dapple
        ));
        COLORS.put("sooty2", ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        COLORS.put("light_belly", ImmutableList.of(
            0.9f,   // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("mealy1", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("mealy2", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("white_suppression", ImmutableList.of(
            15f / 16f,  // Non white-suppression
            1f          // White suppression
        ));
        COLORS.put("leopard", ImmutableList.of(
            31f/32f,     // Non-leopard
            1f      // Leopard
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
        COLORS.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        COLORS.put("white_star", ImmutableList.of(
            0.85f,  // Less white
            1f      // More white
        ));
        COLORS.put("white_forelegs", ImmutableList.of(
            0.9f,   // Less white
            1f      // More white
        ));
        COLORS.put("reduced_points", ImmutableList.of(
            0.95f,  // Higher leg black
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

    public static void init() {
    }
}
