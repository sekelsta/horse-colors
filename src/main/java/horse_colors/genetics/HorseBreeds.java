package sekelsta.horse_colors.genetics;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.Map;

public class HorseBreeds {
    public static HashMap<String, ImmutableList<Float>> DEFAULT;

    static {
        DEFAULT = new HashMap<String, ImmutableList<Float>>();
        DEFAULT.put("extension", ImmutableList.of(
            0.125f, 0.25f, 0.375f, 0.5f, // Red
            0.625f, 0.75f, 0.875f, 1.0f  // Black
        ));
        DEFAULT.put("gray", ImmutableList.of(
            0.95f, // Non-gray
            1.0f   // Gray
        ));
        DEFAULT.put("dun", ImmutableList.of(
            0.75f,  // Non-dun 2
            0.875f, // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        DEFAULT.put("agouti", ImmutableList.of(
            0.375f,     // Black
            0.4375f,    // Seal
            0.5f,       // Brown - same as seal
            0.625f,     // Bay_dark - same as bay
            0.75f,      // Bay
            0.875f,     // Bay_light - same as bay
            0.9375f,    // Bay_semiwild
            1.0f        // Bay_wild
        ));
        DEFAULT.put("silver", ImmutableList.of(
            31.0f / 32.0f,  // Non-silver
            1.0f            // Silver
        ));
        DEFAULT.put("cream", ImmutableList.of(
            61f / 64f,  // Non-cream
            0f,         // Non-cream unused
            31f / 32f,  // Pearl (1/64)
            1f          // Cream (1/32)
        ));
        DEFAULT.put("liver", ImmutableList.of(
            0.25f,  // Liver
            1f      // Non-liver
        ));
        DEFAULT.put("flaxen1", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        DEFAULT.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        DEFAULT.put("dapple", ImmutableList.of(
            0.5f,   // Non-dapple
            1f      // Dapple
        ));
        DEFAULT.put("sooty1", ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        DEFAULT.put("sooty2", ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        DEFAULT.put("sooty3", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        DEFAULT.put("mealy1", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        DEFAULT.put("mealy2", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        DEFAULT.put("mealy3", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        DEFAULT.put("white_suppression", ImmutableList.of(
            31f / 32f,  // Non white-suppression
            1f          // White suppression
        ));
        DEFAULT.put("KIT", ImmutableList.of(
            0.6f,   // Wildtype
            0.63f,   // White boost
            0.66f,  // Markings1
            0.69f,   // Markings2
            0.72f,  // Markings3
            0.75f,   // Markings4
            0.77f,  // Markings5
            0.84f,  // W20
            0f,     // Rabicano / Unused
            0.86f,  // Flashy white
            0f,     // Unused
            0.90f,  // Tobiano
            0.94f,  // Sabino1
            0.96f,  // Tobiano + W20
            0.99f,  // Roan
            1.0f    // Dominant white
        ));
        DEFAULT.put("frame", ImmutableList.of(
            31f / 32f,  // Non-frame
            1f          // Frame
        ));
        DEFAULT.put("MITF", ImmutableList.of(
            0.1f,  // SW1
            0.12f,  // SW3
            0.14f,  // SW5
            1.0f    // Wildtype
        ));
        DEFAULT.put("PAX3", ImmutableList.of(
            0.9f,   // Wildtype
            0.96f,  // SW2
            1f,  // SW4
            1.0f    // Unused
        ));
        DEFAULT.put("leopard", ImmutableList.of(
            31f / 32f,  // Non-leopard
            1f          // Leopard
        ));
        DEFAULT.put("PATN1", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        DEFAULT.put("PATN2", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        DEFAULT.put("PATN3", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        DEFAULT.put("gray_suppression", ImmutableList.of(
            0.975f, // Non gray-suppression
            1f      // Gray suppression
        ));
        DEFAULT.put("gray_mane", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        DEFAULT.put("slow_gray1", ImmutableList.of(
            0.875f, // Lighter
            1f      // Darker
        ));
        DEFAULT.put("slow_gray2", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        DEFAULT.put("white_star", ImmutableList.of(
            0.75f,  // Less white
            1f      // More white
        ));
        DEFAULT.put("white_forelegs", ImmutableList.of(
            0.8f,  // Less white
            1f      // More white
        ));
        DEFAULT.put("white_hindlegs", ImmutableList.of(
            0.8f,  // Less white
            1f      // More white
        ));
        DEFAULT.put("gray_melanoma", ImmutableList.of(
            0.5f,   // Less melanoma
            1f      // More melanoma
        ));
    }
}
