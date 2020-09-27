package sekelsta.horse_colors.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

public class EarlyDomesticHorse {
    public static Breed breed;

    static {
        Tarpan.init();
        breed = new Breed(Tarpan.breed);
        Map<String, List<Float>> COLORS = breed.colors;

        COLORS.put("extension", ImmutableList.of(
            0.2f, 0.2f, 0.2f, 0.2f, // Red
            1.0f, 1.0f, 1.0f, 1.0f  // Black
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
        COLORS.put("dun", ImmutableList.of(
            0.4f,   // Non-dun 2
            0.8f,  // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        COLORS.put("KIT", ImmutableList.of(
            0.65f,  // Wildtype
            0.66f,  // White boost
            0.67f,  // Markings1
            0.68f,  // Markings2
            0.69f,  // Markings3
            0.70f,  // Markings4
            0.71f,  // Markings5
            0.71f,  // W20
            0f,     // Rabicano / Unused
            0.73f,  // Flashy white
            0f,     // Unused
            1.0f,   // Tobiano
            1.0f,   // Sabino1
            1.0f,   // Tobiano + W20
            1.0f,   // Roan
            1.0f    // Dominant white
        ));
        COLORS.put("white_star", ImmutableList.of(
            0.85f,  // Less white
            1f      // More white
        ));
        COLORS.put("white_forelegs", ImmutableList.of(
            0.9f,   // Less white
            1f      // More white
        ));
    }

    public static void init() {}
}
