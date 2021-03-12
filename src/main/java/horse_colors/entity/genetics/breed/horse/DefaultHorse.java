package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.genetics.breed.*;

// This allows for all variations found in horses
public class DefaultHorse {
    public static Breed breed;

    static {
        MongolianHorse.init();
        breed = new Breed(MongolianHorse.breed);
        breed.merge(Breed.load("horse_default_size"), 1f);
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("extension", ImmutableList.of(
            0.5f,   // Red
            1f      // Black
        ));
        GENES.put("agouti", ImmutableList.of(
            0.375f,     // Black
            0.5f,       // Seal
            0.5f,       // Seal unused
            0.5f,       // Bay unused
            1f          // Bay
        ));
        GENES.put("gray", ImmutableList.of(
            0.95f, // Non-gray
            1.0f   // Gray
        ));
        GENES.put("dun", ImmutableList.of(
            0.9f,   // Non-dun 2
            0.92f,  // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        GENES.put("silver", ImmutableList.of(
            31.0f / 32.0f,  // Non-silver
            1.0f            // Silver
        ));
        GENES.put("cream", ImmutableList.of(
            0.8375f,     // Non-cream
            0.84f,     // Snowdrop
            0.86875f,     // Pearl
            0.9f,     // Cream
            1f      // MAPT minor
        ));
        GENES.put("liver", ImmutableList.of(
            0.2f,   // Liver
            1f      // Non-liver
        ));
        GENES.put("flaxen1", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put("dapple", ImmutableList.of(
            0.75f,  // Non-dapple
            1f      // Dapple
        ));
        GENES.put("sooty2", ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        GENES.put("light_belly", ImmutableList.of(
            0.9f,   // Non-mealy
            1f      // Mealy
        ));
        GENES.put("mealy1", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        GENES.put("mealy2", ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        GENES.put("white_suppression", ImmutableList.of(
            15f / 16f,  // Non white-suppression
            1f          // White suppression
        ));
        GENES.put("KIT", ImmutableList.of(
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
        GENES.put("frame", ImmutableList.of(
            31f / 32f,  // Non-frame
            1f          // Frame
        ));
        GENES.put("MITF", ImmutableList.of(
            0.1f,  // SW1
            0.1f,  // SW3
            0.1f,  // SW5
            1.0f    // Wildtype
        ));
        GENES.put("PAX3", ImmutableList.of(
            1f,   // Wildtype
            0.96f,  // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        GENES.put("white_star", ImmutableList.of(
            0.85f,  // Less white
            1f      // More white
        ));
        GENES.put("white_forelegs", ImmutableList.of(
            0.9f,   // Less white
            1f      // More white
        ));
        GENES.put("white_hindlegs", ImmutableList.of(
            0.9f,   // Less white
            1f      // More white
        ));
        GENES.put("champagne", ImmutableList.of(
            1f,     // Non-champagne
            1f      // Champagne
        ));
        GENES.put("donkey_dark", ImmutableList.of(
            1f,     // Lighter
            1f      // Darker
        ));
        GENES.put("reduced_points", ImmutableList.of(
            0.95f,  // Higher leg black
            1f      // Lower leg black
        ));
        GENES.put("flaxen_boost", ImmutableList.of(
            0.5f,   // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        GENES.put("light_dun", ImmutableList.of(
            1f,     // Darker dun
            0f      // Lighter dun
        ));

        GENES.put("leopard", ImmutableList.of(
            31f/32f,    // Non-leopard
            1f          // Leopard
        ));
        GENES.put("PATN1", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put("PATN2", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put("PATN3", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put("marble", ImmutableList.of(
            0.9f,   // Round leopard spots
            1f      // Stretched leopard spots
        ));
        GENES.put("leopard_suppression", ImmutableList.of(
            0.88f,  // Full leopard
            1f      // Semileopard
        ));
        GENES.put("leopard_suppression2", ImmutableList.of(
            0.88f,  // Full leopard
            1f      // Semileopard
        ));
        GENES.put("PATN_boost1", ImmutableList.of(
            15f / 16f,  // Less pattern
            1f          // More pattern
        ));
        GENES.put("PATN_boost2", ImmutableList.of(
            15f / 16f,  // Less pattern
            1f          // More pattern
        ));
        GENES.put("mushroom", ImmutableList.of(
            0.995f,     // Wild type
            1f          // Mushroom
        ));

        GENES.put("rabicano", ImmutableList.of(
            0.99f,  // No rabicano
            1f      // Rabicano
        ));
    }

    public static void init() {}
}
