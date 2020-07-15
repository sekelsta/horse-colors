package sekelsta.horse_colors.genetics;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorseBreeds {
    public static HashMap<String, List<Float>> EQUINE;
    public static HashMap<String, List<Float>> HORSE;
    public static HashMap<String, List<Float>> APPALOOSA;
    public static HashMap<String, List<Float>> DONKEY;

    static {
        EQUINE = new HashMap<String, List<Float>>();


        EQUINE.put("extension", ImmutableList.of(
            0.2f, 0.2f, 0.2f, 0.2f, // Red
            1.0f, 1.0f, 1.0f, 1.0f  // Black
        ));
        EQUINE.put("gray", ImmutableList.of(
            1.0f, // Non-gray
            1.0f   // Gray
        ));
        EQUINE.put("dun", ImmutableList.of(
            0f,     // Non-dun 2
            0f,     // Non-dun 1
            0f,     // Dun
            1f      // Dun for donkeys
        ));
        EQUINE.put("agouti", ImmutableList.of(
            0.1f,   // Black
            0.1f,   // Seal
            0.1f,   // Seal unused
            0.1f,   // Bay unused
            1f,     // Bay
            1f,     // Bay unused
            1f,     // Bay unused
            1f      // Bay unused
        ));
        EQUINE.put("silver", ImmutableList.of(
            1.0f,  // Non-silver
            1.0f   // Silver
        ));
        EQUINE.put("cream", ImmutableList.of(
            1f,     // Non-cream
            0f,     // Non-cream unused
            0f,     // Pearl (1/32)
            0f      // Cream (1/32)
        ));
        EQUINE.put("liver", ImmutableList.of(
            0.1f,   // Liver
            1f      // Non-liver
        ));
        EQUINE.put("flaxen1", ImmutableList.of(
            0.0f,   // Flaxen
            1f      // Non-flaxen
        ));
        EQUINE.put("flaxen2", ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        EQUINE.put("dapple", ImmutableList.of(
            1.0f,   // Non-dapple
            1f      // Dapple
        ));
        EQUINE.put("sooty1", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        EQUINE.put("sooty2", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        EQUINE.put("sooty3", ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        EQUINE.put("light_belly", ImmutableList.of(
            0f,     // Non-mealy
            1f      // Mealy
        ));
        EQUINE.put("mealy1", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        EQUINE.put("mealy2", ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        EQUINE.put("white_suppression", ImmutableList.of(
            0f,     // Non white-suppression
            1f      // White suppression
        ));
        EQUINE.put("KIT", ImmutableList.of(
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
        EQUINE.put("frame", ImmutableList.of(
            1f,     // Non-frame
            1f      // Frame
        ));
        EQUINE.put("MITF", ImmutableList.of(
            0f,     // SW1
            0f,     // SW3
            0f,     // SW5
            1.0f    // Wildtype
        ));
        EQUINE.put("PAX3", ImmutableList.of(
            1f,     // Wildtype
            0f,     // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        EQUINE.put("leopard", ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        EQUINE.put("PATN1", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        EQUINE.put("PATN2", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        EQUINE.put("PATN3", ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        EQUINE.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        EQUINE.put("slow_gray1", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        EQUINE.put("slow_gray2", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        EQUINE.put("slow_gray3", ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        EQUINE.put("white_star", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        EQUINE.put("white_forelegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        EQUINE.put("white_hindlegs", ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        EQUINE.put("gray_melanoma", ImmutableList.of(
            0.5f,   // Less melanoma
            1f      // More melanoma
        ));
        EQUINE.put("gray_mane1", ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        EQUINE.put("gray_mane2", ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        EQUINE.put("rufous", ImmutableList.of(
            0.1f,   // Yellower
            1f      // Redder
        ));
        EQUINE.put("dense", ImmutableList.of(
            0.9f,   // Lighter
            1f      // Darker
        ));
        EQUINE.put("champagne", ImmutableList.of(
            1f,     // Non-champagne
            1f      // Champagne
        ));
        EQUINE.put("cameo", ImmutableList.of(
            1f,     // Non-cameo
            1f      // Cameo
        ));
        EQUINE.put("ivory", ImmutableList.of(
            1f,     // Non-ivory
            1f      // Ivory
        ));
        EQUINE.put("donkey_dark", ImmutableList.of(
            0f,     // Lighter
            1f      // Darker
        ));
        EQUINE.put("reduced_points", ImmutableList.of(
            1f,     // Higher leg black
            1f      // Lower leg black
        ));
        EQUINE.put("light_legs", ImmutableList.of(
            1f,     // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        EQUINE.put("less_light_legs", ImmutableList.of(
            1f,     // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        EQUINE.put("donkey_dun", ImmutableList.of(
            1f,     // Dun
            0f, 
            0f,
            0f
        ));
        EQUINE.put("flaxen_boost", ImmutableList.of(
            0.95f,  // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        EQUINE.put("light_dun", ImmutableList.of(
            0f,     // Darker dun
            1f      // Lighter dun
        ));

        HORSE = new HashMap<String, List<Float>>(EQUINE);

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
            30.2f/32f,      // Snowdrop
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
            31f / 32f,  // Non white-suppression
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
        HORSE.put("leopard", ImmutableList.of(
            15f/16f,     // Non-leopard
            1f      // Leopard
        ));
        HORSE.put("PATN1", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        HORSE.put("PATN2", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        HORSE.put("PATN3", ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        HORSE.put("gray_suppression", ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
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
            0.75f,  // Round leopard spots
            1f       // Stretched leopard spots
        ));

        APPALOOSA = new HashMap<String, List<Float>>(HORSE);
        APPALOOSA.put("leopard", ImmutableList.of(
            0.5f,     // Non-leopard
            1f        // Leopard
        ));
        APPALOOSA.put("PATN1", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        APPALOOSA.put("PATN2", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        APPALOOSA.put("PATN3", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));


        DONKEY = new HashMap<String, List<Float>>(EQUINE);
        DONKEY.put("cameo", ImmutableList.of(
            0.99f,  // Non cameo
            1f      // Cameo
        ));
        DONKEY.put("ivory", ImmutableList.of(
            0.9f,   // Non ivory
            1f      // Ivory
        ));
        // TODO: Somali wild asses don't have the shoulder cross
        DONKEY.put("cross", ImmutableList.of(
            0f,     // No shoulder stripe
            1f      // Shoulder stripe
        ));
        DONKEY.put("light_legs", ImmutableList.of(
            0.5f,   // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        DONKEY.put("less_light_legs", ImmutableList.of(
            0.5f,   // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        DONKEY.put("donkey_dun", ImmutableList.of(
            0.5f,   // Dun
            0.95f,  // Non-dun with cross
            1f,     // Non-dun, no cross
            0f      // Unused
        ));
    }
}
