package sekelsta.horse_colors.genetics.breed;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseEquine {
    public static Breed breed = new Breed();

    static {
        Map<String, List<Float>> COLORS = breed.colors;

        COLORS.put("extension", ImmutableList.of(
            0f, 0f, 0f, 0f, // Red
            1f, 1f, 1f, 1f  // Black
        ));
        COLORS.put("gray", ImmutableList.of(
            1.0f, // Non-gray
            1.0f   // Gray
        ));
        COLORS.put("dun", ImmutableList.of(
            0f,     // Non-dun 2
            0f,     // Non-dun 1
            0f,     // Dun
            1f      // Dun for donkeys
        ));
        COLORS.put("agouti", ImmutableList.of(
            0.0f,   // Black
            0.1f,   // Seal
            0.1f,   // Seal unused
            0.1f,   // Bay unused
            1f,     // Bay
            1f,     // Bay unused
            1f,     // Bay unused
            1f      // Bay unused
        ));
        COLORS.put("silver", ImmutableList.of(
            1.0f,  // Non-silver
            1.0f   // Silver
        ));
        COLORS.put("cream", ImmutableList.of(
            1f,     // Non-cream
            0f,     // Snowdrop
            0f,     // Pearl
            0f,     // Cream
            0f      // MAPT minor
        ));
        COLORS.put("liver", ImmutableList.of(
            0.1f,   // Liver
            1f      // Non-liver
        ));
        COLORS.put("flaxen1", ImmutableList.of(
            0.0f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        COLORS.put("dapple", ImmutableList.of(
            1.0f,   // Non-dapple
            1f      // Dapple
        ));
        COLORS.put("sooty1", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        COLORS.put("sooty2", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        COLORS.put("sooty3", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        COLORS.put("light_belly", ImmutableList.of(
            0f,     // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("mealy1", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("mealy2", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        COLORS.put("white_suppression", ImmutableList.of(
            0f,     // Non white-suppression
            1f      // White suppression
        ));
        COLORS.put("KIT", ImmutableList.of(
            1f     // Wildtype
        ));
        COLORS.put("frame", ImmutableList.of(
            1f,     // Non-frame
            1f      // Frame
        ));
        COLORS.put("MITF", ImmutableList.of(
            0f,     // SW1
            0f,     // SW3
            0f,     // SW5
            1.0f    // Wildtype
        ));
        COLORS.put("PAX3", ImmutableList.of(
            1f,     // Wildtype
            0f,     // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        COLORS.put("leopard", ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        COLORS.put("PATN1", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        COLORS.put("PATN2", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        COLORS.put("PATN3", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        COLORS.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        COLORS.put("slow_gray1", ImmutableList.of(
            0.5f,   // Lighter
            1f      // Darker
        ));
        COLORS.put("slow_gray2", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        COLORS.put("slow_gray3", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        COLORS.put("white_star", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        COLORS.put("white_forelegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        COLORS.put("white_hindlegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        COLORS.put("gray_melanoma", ImmutableList.of(
            0.5f,   // Less melanoma
            1f      // More melanoma
        ));
        COLORS.put("gray_mane1", ImmutableList.of(
            0.25f,  // Lighter mane
            1f      // Lighter body
        ));
        COLORS.put("gray_mane2", ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        COLORS.put("rufous", ImmutableList.of(
            0.1f,   // Yellower
            1f      // Redder
        ));
        COLORS.put("dense", ImmutableList.of(
            0.9f,   // Lighter
            1f      // Darker
        ));
        COLORS.put("champagne", ImmutableList.of(
            1f,     // Non-champagne
            1f      // Champagne
        ));
        COLORS.put("cameo", ImmutableList.of(
            1f,     // Non-cameo
            1f      // Cameo
        ));
        COLORS.put("ivory", ImmutableList.of(
            1f,     // Non-ivory
            1f      // Ivory
        ));
        COLORS.put("donkey_dark", ImmutableList.of(
            0f,     // Lighter
            1f      // Darker
        ));
        COLORS.put("reduced_points", ImmutableList.of(
            1f,     // Higher leg black
            1f      // Lower leg black
        ));
        COLORS.put("light_legs", ImmutableList.of(
            1f,     // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        COLORS.put("less_light_legs", ImmutableList.of(
            1f,     // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        COLORS.put("donkey_dun", ImmutableList.of(
            1f,     // Dun
            0f, 
            0f,
            0f
        ));
        COLORS.put("flaxen_boost", ImmutableList.of(
            0.95f,  // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        COLORS.put("light_dun", ImmutableList.of(
            0f,     // Darker dun
            1f      // Lighter dun
        ));
        COLORS.put("marble", ImmutableList.of(
            1f,     // Round leopard spots
            1f      // Stretched leopard spots
        ));
        COLORS.put("leopard_suppression", ImmutableList.of(
            1f,     // Full leopard
            1f      // Semileopard
        ));
        COLORS.put("leopard_suppression2", ImmutableList.of(
            1f,     // Full leopard
            1f      // Semileopard
        ));
        COLORS.put("PATN_boost1", ImmutableList.of(
            1f,     // Less pattern
            1f      // More pattern
        ));
        COLORS.put("PATN_boost2", ImmutableList.of(
            1f,     // Less pattern
            1f      // More pattern
        ));
        COLORS.put("dark_red", ImmutableList.of(
            0.5f,   // Lighter
            1f      // Darker
        ));
    }
}
