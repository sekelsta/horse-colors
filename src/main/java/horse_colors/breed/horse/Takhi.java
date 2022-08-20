package sekelsta.horse_colors.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.BaseEquine;
import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

// Also called the Prezwalski's Wild horse, they are related to the domesticated
// horses of the Botai culture
public class Takhi {
    public static Breed<Gene> breed = new Breed<>(BaseEquine.breed);

    static {
        breed.name = "takhi";
        breed.population = 2000;
        Map<Gene, List<Float>> GENES = breed.genes;

        GENES.put(Gene.dun, ImmutableList.of(
            0.0f,   // Non-dun 2
            0.0f,   // Non-dun 1
            1f      // Dun
        ));
        GENES.put(Gene.flaxen1, ImmutableList.of(
            0.1f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put(Gene.flaxen2, ImmutableList.of(
            0.1f,   // Flaxen
            1f      // Non-flaxen
        ));
        GENES.put(Gene.sooty2, ImmutableList.of(
            0.9f,   // Non-sooty
            1f      // Sooty
        ));
        GENES.put(Gene.mealy1, ImmutableList.of(
            0.25f,  // Non-mealy
            1f      // Mealy
        ));
        GENES.put(Gene.mealy2, ImmutableList.of(
            0.25f,  // Non-mealy
            1f      // Mealy
        ));
        GENES.put(Gene.white_suppression, ImmutableList.of(
            15f / 16f,  // Non white-suppression
            1f          // White suppression
        ));
        GENES.put(Gene.flaxen_boost, ImmutableList.of(
            0.5f,   // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        GENES.put(Gene.light_dun, ImmutableList.of(
            1f,     // Darker dun
            0f      // Lighter dun
        ));
        GENES.put(Gene.liver_boost, ImmutableList.of(
            0.8f,   // Wild type
            1f      // Darker liver chestnut
        ));
        GENES.put(Gene.donkey_dark, ImmutableList.of(
            1f    // Lighter
        ));
    }
}
