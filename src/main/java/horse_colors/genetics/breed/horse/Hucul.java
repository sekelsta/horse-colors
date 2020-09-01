package sekelsta.horse_colors.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

// The hucul is a very old breed, thought to have arisen from crosses between
// tarpans and Mongolian horses.
// Genetic analysis supports that it is not closely related to other breeds:
// http://funpecrp.com.br/gmr/year2011/vol10-4/pdf/gmr1410.pdf
// Crypto-tobianos in the Hucul breed:
// https://www.researchgate.net/publication/276243381_Crypto-tobiano_horses_in_Hucul_breed
// See here for images of crypto-tobiano Hucul horses:
// https://meetings.eaap.org/wp-content/uploads/2018/Session71/71.20_cd0o43jf.pdf
// See here for information about inbreeding and heterozygosity:
// https://doi.org/10.5194/aab-58-23-2015
// Allele frequencies are from table 2 here:
// https://www.tandfonline.com/doi/pdf/10.1080/09712119.2020.1715224
// The above link also has pictures including what appears to be a dun with
// bider markings
// Possible darker modifier or version of dun?
// https://doi.org/10.1017/S1751731118003506
// In images some Hucul horses have a soft shoulder stripe or blurred
// zebra-like markings.
public class Hucul {
    public static Breed breed;

    static {
        TarpanEurope.init();
        breed = new Breed(TarpanEurope.breed);
        EarlyDomesticHorse.init();
        breed.merge(EarlyDomesticHorse.breed, 0.2f);
        MongolianHorse.init();
        breed.merge(MongolianHorse.breed, 0.2f);
        // There may also have been contributions from Norikers, Arabians, 
        // and Turkish horses
        breed.name = "hucul";
        breed.population = 4000;
        Map<String, List<Float>> COLORS = breed.colors;

        // 28% chestnut frequency to get 8% chestnut horses
        COLORS.put("extension", ImmutableList.of(
            0.28f, 0.28f, 0.28f, 0.28f, // Red
            1.0f, 1.0f, 1.0f, 1.0f  // Black
        ));
        // Of non-chestnut horses, want 76% bay and 24% black,
        // so the black allele should have a 50% frequency
        COLORS.put("agouti", ImmutableList.of(
            0.5f,       // Black
            0.6f,       // Seal
            0.6f,       // Seal unused
            0.6f,       // Bay unused
            1f,         // Bay
            1f,         // Bay unused
            1f,         // Bay unused
            1f          // Bay unused
        ));
        // 7% dun frequency to get 13% dun horses, non-dun 1 common
        COLORS.put("dun", ImmutableList.of(
            0.3f,   // Non-dun 2
            0.8f,   // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        // Should have 11% tobiano allele frequency to get 22% tobiano horses
        COLORS.put("KIT", ImmutableList.of(
            0.89f,  // Wildtype
            0.89f,  // White boost
            0.89f,  // Markings1
            0.89f,  // Markings2
            0.89f,  // Markings3
            0.89f,  // Markings4
            0.89f,  // Markings5
            0.89f,  // W20
            0f,     // Rabicano / Unused
            0.89f,  // Flashy white
            0f,     // Unused
            1.0f,   // Tobiano
            1.0f,   // Sabino1
            1.0f,   // Tobiano + W20
            1.0f,   // Roan
            1.0f    // Dominant white
        ));
        // Gray was bred out
        COLORS.put("gray", ImmutableList.of(
            1f,     // Non-gray
            1f      // Gray
        ));
        // Cream was bred out
        COLORS.put("cream", ImmutableList.of(
            1f,     // Non-cream
            1f,     // Snowdrop
            1f,     // Pearl
            1f      // Cream
        ));
        // Presumably leopard was bred out
        COLORS.put("leopard", ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        // Remove mealy
        COLORS.put("light_belly", ImmutableList.of(
            1f,     // Non-mealy
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
        // A source mentioned leg stripes, but looking at pictures of the actual ponies
        // I don't see them being more common than in other breeds
    }

    public static void init() {}
}
