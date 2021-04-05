package sekelsta.horse_colors.breed;

import com.google.common.collect.ImmutableList;
import java.util.*;

public class BaseEquine {
    public static Breed breed = new Breed();

    static {
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("extension", ImmutableList.of(
            0f,     // Red
            1f      // Black
        ));
        GENES.put("gray", ImmutableList.of(
            1.0f,   // Non-gray
            1.0f    // Gray
        ));
        GENES.put("dun", ImmutableList.of(
            0f,     // Non-dun 2
            0f,     // Non-dun 1
            0f,     // Dun
            1f      // Dun for donkeys
        ));
        GENES.put("agouti", ImmutableList.of(
            0.0f,   // Black
            0.1f,   // Seal
            0.1f,   // Seal unused
            0.1f,   // Bay unused
            1f      // Bay
        ));
        GENES.put("silver", ImmutableList.of(
            1.0f,  // Non-silver
            1.0f   // Silver
        ));
        GENES.put("cream", ImmutableList.of(
            1f,     // Non-cream
            0f,     // Snowdrop
            0f,     // Pearl
            0f,     // Cream
            0f      // MAPT minor
        ));
        GENES.put("liver", ImmutableList.of(
            0.1f,   // Liver
            1f      // Non-liver
        ));
        GENES.put("flaxen1", ImmutableList.of(
            0.0f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put("dapple", ImmutableList.of(
            1.0f,   // Non-dapple
            1f      // Dapple
        ));
        GENES.put("sooty1", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        GENES.put("sooty2", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        GENES.put("sooty3", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        GENES.put("light_belly", ImmutableList.of(
            0f,     // Non-mealy
            1f      // Mealy
        ));
        GENES.put("mealy1", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        GENES.put("mealy2", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        GENES.put("white_suppression", ImmutableList.of(
            0f,     // Non white-suppression
            1f      // White suppression
        ));
        GENES.put("KIT", ImmutableList.of(
            1f     // Wildtype
        ));
        GENES.put("frame", ImmutableList.of(
            1f,     // Non-frame
            1f      // Frame
        ));
        GENES.put("MITF", ImmutableList.of(
            0f,     // SW1
            0f,     // SW3
            0f,     // SW5
            1.0f    // Wildtype
        ));
        GENES.put("PAX3", ImmutableList.of(
            1f,     // Wildtype
            0f,     // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        GENES.put("leopard", ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        GENES.put("PATN1", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        GENES.put("PATN2", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        GENES.put("PATN3", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        GENES.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        GENES.put("slow_gray1", ImmutableList.of(
            0.5f,   // Lighter
            1f      // Darker
        ));
        GENES.put("slow_gray2", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        GENES.put("slow_gray3", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        GENES.put("white_star", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        GENES.put("white_forelegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        GENES.put("white_hindlegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        GENES.put("gray_melanoma", ImmutableList.of(
            0.5f,   // Less melanoma
            1f      // More melanoma
        ));
        GENES.put("gray_mane1", ImmutableList.of(
            0.25f,  // Lighter mane
            1f      // Lighter body
        ));
        GENES.put("gray_mane2", ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        GENES.put("rufous", ImmutableList.of(
            0.1f,   // Yellower
            1f      // Redder
        ));
        GENES.put("dense", ImmutableList.of(
            0.9f,   // Lighter
            1f      // Darker
        ));
        GENES.put("champagne", ImmutableList.of(
            1f,     // Non-champagne
            1f      // Champagne
        ));
        GENES.put("cameo", ImmutableList.of(
            1f,     // Non-cameo
            1f      // Cameo
        ));
        GENES.put("ivory", ImmutableList.of(
            1f,     // Non-ivory
            1f      // Ivory
        ));
        GENES.put("donkey_dark", ImmutableList.of(
            0f,     // Lighter
            1f      // Darker
        ));
        GENES.put("reduced_points", ImmutableList.of(
            1f,     // Higher leg black
            1f      // Lower leg black
        ));
        GENES.put("light_legs", ImmutableList.of(
            1f,     // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        GENES.put("less_light_legs", ImmutableList.of(
            1f,     // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        GENES.put("donkey_dun", ImmutableList.of(
            1f,     // Dun
            0f, 
            0f,
            0f
        ));
        GENES.put("flaxen_boost", ImmutableList.of(
            0.95f,  // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        GENES.put("light_dun", ImmutableList.of(
            0f,     // Darker dun
            1f      // Lighter dun
        ));
        GENES.put("marble", ImmutableList.of(
            1f,     // Round leopard spots
            1f      // Stretched leopard spots
        ));
        GENES.put("leopard_suppression", ImmutableList.of(
            1f,     // Full leopard
            1f      // Semileopard
        ));
        GENES.put("leopard_suppression2", ImmutableList.of(
            1f,     // Full leopard
            1f      // Semileopard
        ));
        GENES.put("PATN_boost1", ImmutableList.of(
            1f,     // Less pattern
            1f      // More pattern
        ));
        GENES.put("PATN_boost2", ImmutableList.of(
            1f,     // Less pattern
            1f      // More pattern
        ));
        GENES.put("dark_red", ImmutableList.of(
            0.5f,   // Lighter
            1f      // Darker
        ));
        GENES.put("LCORL", ImmutableList.of(
            1f,     // Wild type (T)
            1f      // Larger warmbloods and drafts (C)
        ));
        GENES.put("HMGA2", ImmutableList.of(
            1f,     // Wild type
            1f      // Smaller shetlands
        ));
        GENES.put("mushroom", ImmutableList.of(
            1f,     // Wild type
            1f      // Mushroom
        ));
        GENES.put("liver_boost", ImmutableList.of(
            1f,     // Wild type
            1f      // Darker liver chestnut
        ));

        ImmutableList<Float> halfAndHalf = ImmutableList.of(0.5f, 1f);
        for (int i = 0; i < 12; ++i) {
            GENES.put("speed" + i, halfAndHalf);
            GENES.put("jump" + i, halfAndHalf);
            GENES.put("health" + i, halfAndHalf);
        }
        for (int i = 0; i < 8; ++i) {
            GENES.put("athletics" + i, halfAndHalf);
        }
        // Sixteen equally likely alleles for each immune diversity gene
        ArrayList<Float> sixteen = new ArrayList<>();
        for (int i = 0; i < 16; ++i) {
            sixteen.add((i + 1) / 16f);
        }
        for (int i = 0; i < 8; ++i) {
            GENES.put("immune" + i, sixteen);
            GENES.put("mhc" + i, sixteen);
        }
        ImmutableList<Float> five = ImmutableList.of(0.2f, 0.4f, 0.6f, 0.8f, 1f);
        for (int i = 0; i < 8; ++i) {
            GENES.put("size_minor" + i, five);
        }

        ArrayList<Float> size_subtle = new ArrayList();
        size_subtle.add(0.2f);
        for (int i = 1; i < 9; ++i) {
            size_subtle.add(0.1f * (i + 2));
        }
        for (int i = 0; i < 8; ++i) {
            GENES.put("size_subtle" + i, size_subtle);
        }

        ImmutableList<Float> one = ImmutableList.of(1f);
        GENES.put("size0", one);
        GENES.put("size1", one);
        GENES.put("size2", one);
        GENES.put("size3", one);
        GENES.put("size4", one);

        GENES.put("double_ovulation", ImmutableList.of(
            0.8f,   // Twins less likely
            1.0f    // Twins more likely
        ));

        GENES.put("donkey_size0", ImmutableList.of(
            1f
        ));
        GENES.put("donkey_size1", ImmutableList.of(
            1f
        ));
        GENES.put("donkey_size2", ImmutableList.of(
            1f
        ));
        GENES.put("donkey_size3", ImmutableList.of(
            1f
        ));
        GENES.put("donkey_size4", ImmutableList.of(
            1f
        ));
        GENES.put("donkey_size5", ImmutableList.of(
            1f
        ));
        GENES.put("donkey_size6", ImmutableList.of(
            1f
        ));

        // Non-albino
        GENES.put("color", one);

        // No rabicano
        GENES.put("rabicano", one);
    }
}
