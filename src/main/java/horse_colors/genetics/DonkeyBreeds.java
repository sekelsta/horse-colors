package sekelsta.horse_colors.genetics;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonkeyBreeds {
    public static HashMap<String, List<Float>> DEFAULT;

    static {
        DEFAULT = new HashMap<String, List<Float>>();
        DEFAULT.put("extension", ImmutableList.of(
            0.2f, 0.2f, 0.2f, 0.2f, // Red
            1.0f, 1.0f, 1.0f, 1.0f  // Black
        ));
        DEFAULT.put("gray", ImmutableList.of(
            1.0f, // Non-gray
            1.0f   // Gray
        ));
        DEFAULT.put("dun", ImmutableList.of(
            0.0f,   // Non-dun 2
            0.5f, // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        DEFAULT.put("agouti", ImmutableList.of(
            0.1f,   // Black
            0.1f,   // Seal
            0.1f,   // Seal unused
            0.1f,   // Bay unused
            1f,     // Bay
            1f,     // Bay unused
            1f,     // Bay unused
            1f      // Bay unused
        ));
        DEFAULT.put("silver", ImmutableList.of(
            1.0f,  // Non-silver
            1.0f   // Silver
        ));
        DEFAULT.put("cream", ImmutableList.of(
            1f,     // Non-cream
            0f,     // Non-cream unused
            0f,     // Pearl (1/32)
            0f      // Cream (1/32)
        ));
        DEFAULT.put("liver", ImmutableList.of(
            0.25f,  // Liver
            1f      // Non-liver
        ));
        DEFAULT.put("flaxen1", ImmutableList.of(
            0.0f,   // Flaxen
            1f      // Non-flaxen
        ));
        DEFAULT.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        DEFAULT.put("dapple", ImmutableList.of(
            1.0f,   // Non-dapple
            1f      // Dapple
        ));
        DEFAULT.put("sooty1", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        DEFAULT.put("sooty2", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        DEFAULT.put("sooty3", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        DEFAULT.put("light_belly", ImmutableList.of(
            0f,     // Non-mealy
            1f      // Mealy
        ));
        DEFAULT.put("mealy1", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        DEFAULT.put("mealy2", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        DEFAULT.put("white_suppression", ImmutableList.of(
            1f,     // Non white-suppression
            1f      // White suppression
        ));
        DEFAULT.put("KIT", ImmutableList.of(
            1f,     // Wildtype
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
            1f,     // Non-frame
            1f      // Frame
        ));
        DEFAULT.put("MITF", ImmutableList.of(
            0f,     // SW1
            0f,     // SW3
            0f,     // SW5
            1.0f    // Wildtype
        ));
        DEFAULT.put("PAX3", ImmutableList.of(
            1f,     // Wildtype
            0f,     // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        DEFAULT.put("leopard", ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        DEFAULT.put("PATN1", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        DEFAULT.put("PATN2", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        DEFAULT.put("PATN3", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        DEFAULT.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        DEFAULT.put("slow_gray1", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        DEFAULT.put("slow_gray2", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        DEFAULT.put("slow_gray3", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        DEFAULT.put("white_star", ImmutableList.of(
            0.75f,  // Less white
            1f      // More white
        ));
        DEFAULT.put("white_forelegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        DEFAULT.put("white_hindlegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        DEFAULT.put("gray_melanoma", ImmutableList.of(
            0.5f,   // Less melanoma
            1f      // More melanoma
        ));
        DEFAULT.put("gray_mane1", ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        DEFAULT.put("gray_mane2", ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        DEFAULT.put("ivory", ImmutableList.of(
            0f,     // Non ivory
            1f      // Ivory
        ));
    }
}
