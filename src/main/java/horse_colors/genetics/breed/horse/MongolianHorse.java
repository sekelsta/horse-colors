package sekelsta.horse_colors.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

public class MongolianHorse {
    public static Breed breed;

    static {
        EarlyDomesticHorse.init();
        breed = new Breed(EarlyDomesticHorse.breed);
        breed.name = "mongolian";
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
    }

    public static void init() {}
}
