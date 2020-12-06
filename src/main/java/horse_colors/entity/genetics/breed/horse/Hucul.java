package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

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
// Allele frequencies are from here: https://www.researchgate.net/profile/Antoni_Brodacki/publication/289650841_Allele_frequency_in_loci_which_control_coat_colours_in_Hucul_horse_population/links/5c45a6d1299bf12be3d7edaa/Allele-frequency-in-loci-which-control-coat-colours-in-Hucul-horse-population.pdf
// Allele frequencies used to be from table 2 here:
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
        Mongolian.init();
        breed = new Breed(Mongolian.breed);
        // There may also have been contributions from Norikers, Arabians, 
        // and Turkish horses
        breed.name = "hucul";
        breed.population = 4000;
        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("extension", ImmutableList.of(
            0.115f, // Red
            1.0f    // Black
        ));
        GENES.put("agouti", ImmutableList.of(
            0.521f,       // Black
            0.6f,       // Seal
            0.6f,       // Seal unused
            0.6f,       // Bay unused
            1f          // Bay
        ));
        GENES.put("dun", ImmutableList.of(
            0.7f,       // Non-dun 2
            0.878f,     // Non-dun 1
            1f,         // Dun
            0f          // Dun unused
        ));
        GENES.put("KIT", ImmutableList.of(
            0.929f, // Wildtype
            0f,     // White boost
            0f,     // Markings1
            0f,     // Markings2
            0f,     // Markings3
            0f,     // Markings4
            0f,     // Markings5
            0f,     // W20
            0f,     // Rabicano / Unused
            0f,     // Flashy white
            0f,     // Unused
            1.0f,   // Tobiano
            1.0f,   // Sabino1
            1.0f,   // Tobiano + W20
            1.0f,   // Roan
            1.0f    // Dominant white
        ));
        // Included for historic reasons
        GENES.put("gray", ImmutableList.of(
            0.997f,     // Non-gray
            1f          // Gray
        ));
        // Cream was bred out
        GENES.put("cream", ImmutableList.of(
            1f,     // Non-cream
            1f,     // Snowdrop
            1f,     // Pearl
            1f      // Cream
        ));
        // Presumably leopard was bred out
        GENES.put("leopard", ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        // Remove mealy
        GENES.put("light_belly", ImmutableList.of(
            1f,     // Non-mealy
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
        // A source mentioned leg stripes, but looking at pictures of the actual ponies
        // I don't see them being more common than in other breeds
    }

    public static void init() {}
}
