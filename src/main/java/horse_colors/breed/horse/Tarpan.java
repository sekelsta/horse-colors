package sekelsta.horse_colors.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

// I'm taking a broad view of "tarpan" here, so these are all the 
// pre-domestic horses more closely related to domestic horses than to takhis.
public class Tarpan {
    public static Breed breed = new Breed(Takhi.breed);

    static {
        breed.name = "tarpan";
        Map<Gene, List<Float>> GENES = breed.genes;

        GENES.put(Gene.dun, ImmutableList.of(
            0.0f,   // Non-dun 2
            0.5f,   // Non-dun 1
            1f,     // Dun
            0f      // Dun unused
        ));
        GENES.put(Gene.liver, ImmutableList.of(
            0.2f,   // Liver
            1f      // Non-liver
        ));
        GENES.put(Gene.dapple, ImmutableList.of(
            0.75f,  // Non-dapple
            1f      // Dapple
        ));
        GENES.put(Gene.flaxen1, ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put(Gene.flaxen2, ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put(Gene.sooty2, ImmutableList.of(
            0.75f,  // Non-sooty
            1f      // Sooty
        ));
        GENES.put(Gene.mealy1, ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        GENES.put(Gene.mealy2, ImmutableList.of(
            0.75f,  // Non-mealy
            1f      // Mealy
        ));
        GENES.put(Gene.light_belly, ImmutableList.of(
            0.9f,   // Non-mealy
            1f      // Mealy
        ));
        GENES.put(Gene.gray_suppression, ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        GENES.put(Gene.reduced_points, ImmutableList.of(
            0.95f,  // Higher leg black
            1f      // Lower leg black
        ));

        GENES.put(Gene.agouti, ImmutableList.of(
            0.5f,       // Black
            0.65f,      // Seal
            0.65f,      // Seal unused
            0.65f,      // Bay unused
            1f          // Bay
        ));
        GENES.put(Gene.leopard, ImmutableList.of(
            31f/32f,    // Non-leopard
            1f          // Leopard
        ));
        GENES.put(Gene.PATN1, ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put(Gene.PATN2, ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put(Gene.PATN3, ImmutableList.of(
            15f / 16f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put(Gene.marble, ImmutableList.of(
            0.9f,   // Round leopard spots
            1f      // Stretched leopard spots
        ));
        GENES.put(Gene.leopard_suppression, ImmutableList.of(
            0.92f,  // Full leopard
            1f      // Semileopard
        ));
        GENES.put(Gene.leopard_suppression2, ImmutableList.of(
            0.88f,  // Full leopard
            1f      // Semileopard
        ));
        GENES.put(Gene.PATN_boost1, ImmutableList.of(
            15f / 16f,  // Less pattern
            1f          // More pattern
        ));
        GENES.put(Gene.PATN_boost2, ImmutableList.of(
            15f / 16f,  // Less pattern
            1f          // More pattern
        ));
    }
}
