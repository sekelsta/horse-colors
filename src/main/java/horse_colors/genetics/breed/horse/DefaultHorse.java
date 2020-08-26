package sekelsta.horse_colors.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

// This allows for all variations found in horses
public class DefaultHorse {
    public static Breed breed = new Breed(BaseHorse.breed);

    static {
        Map<String, List<Float>> HORSE = breed.colors;

        HORSE.put("extension", ImmutableList.of(
            0.5f, 0.25f, 0.375f, 0.5f, // Red
            1.0f, 0.75f, 0.875f, 1.0f  // Black
        ));
        HORSE.put("gray", ImmutableList.of(
            0.95f, // Non-gray
            1.0f   // Gray
        ));
        HORSE.put("dun", ImmutableList.of(
            0.9f,   // Non-dun 2
            0.92f,  // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        HORSE.put("agouti", ImmutableList.of(
            0.375f,     // Black
            0.5f,       // Seal
            0.5f,       // Seal unused
            0.5f,       // Bay unused
            1f,         // Bay
            0.875f,     // Bay unused
            0.9375f,    // Bay unused
            1.0f        // Bay unused
        ));
        HORSE.put("silver", ImmutableList.of(
            31.0f / 32.0f,  // Non-silver
            1.0f            // Silver
        ));
        HORSE.put("cream", ImmutableList.of(
            30f / 32f,      // Non-cream
            30.1f/32f,      // Snowdrop
            31f / 32f,      // Pearl (1/32)
            1f             // Cream (1/32)
        ));
        HORSE.put("liver", ImmutableList.of(
            0.25f,  // Liver
            1f      // Non-liver
        ));
        HORSE.put("flaxen1", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        HORSE.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        HORSE.put("dapple", ImmutableList.of(
            0.75f,  // Non-dapple
            1f      // Dapple
        ));
        HORSE.put("sooty2", ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        HORSE.put("light_belly", ImmutableList.of(
            0.9f,   // Non-mealy
            1f      // Mealy
        ));
        HORSE.put("mealy1", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        HORSE.put("mealy2", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        HORSE.put("white_suppression", ImmutableList.of(
            15f / 16f,  // Non white-suppression
            1f          // White suppression
        ));
        HORSE.put("KIT", ImmutableList.of(
            0.65f,  // Wildtype
            0.66f,  // White boost
            0.67f,  // Markings1
            0.68f,  // Markings2
            0.69f,  // Markings3
            0.70f,  // Markings4
            0.71f,  // Markings5
            0.86f,  // W20
            0f,     // Rabicano / Unused
            0.88f,  // Flashy white
            0f,     // Unused
            0.92f,  // Tobiano
            0.94f,  // Sabino1
            0.96f,  // Tobiano + W20
            0.99f,  // Roan
            1.0f    // Dominant white
        ));
        HORSE.put("frame", ImmutableList.of(
            31f / 32f,  // Non-frame
            1f          // Frame
        ));
        HORSE.put("MITF", ImmutableList.of(
            0.1f,  // SW1
            0.1f,  // SW3
            0.1f,  // SW5
            1.0f    // Wildtype
        ));
        HORSE.put("PAX3", ImmutableList.of(
            1f,   // Wildtype
            0.96f,  // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        HORSE.put("white_star", ImmutableList.of(
            0.85f,  // Less white
            1f      // More white
        ));
        HORSE.put("white_forelegs", ImmutableList.of(
            0.9f,   // Less white
            1f      // More white
        ));
        HORSE.put("white_hindlegs", ImmutableList.of(
            0.9f,   // Less white
            1f      // More white
        ));
        HORSE.put("champagne", ImmutableList.of(
            1f,     // Non-champagne
            1f      // Champagne
        ));
        HORSE.put("donkey_dark", ImmutableList.of(
            1f,     // Lighter
            1f      // Darker
        ));
        HORSE.put("reduced_points", ImmutableList.of(
            0.95f,  // Higher leg black
            1f      // Lower leg black
        ));
        HORSE.put("flaxen_boost", ImmutableList.of(
            0.5f,   // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        HORSE.put("light_dun", ImmutableList.of(
            1f,     // Darker dun
            0f      // Lighter dun
        ));
        HORSE.put("marble", ImmutableList.of(
            0.9f,   // Round leopard spots
            1f      // Stretched leopard spots
        ));
        HORSE.put("leopard_suppression", ImmutableList.of(
            0.88f,  // Full leopard
            1f      // Semileopard
        ));
    }
}
