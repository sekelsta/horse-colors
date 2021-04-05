package sekelsta.horse_colors.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.Breed;

public class ClevelandBay {
    public static Breed breed = new Breed(Breed.load("cleveland_bay_color"));

    static {
        breed.name = "cleveland_bay";
        breed.population = 550;
        breed.parent = QuarterHorse.breed;

        Map<String, List<Float>> GENES = breed.genes;

        GENES.put("HMGA2", ImmutableList.of(
            1f,     // Wild type (G)
            1f      // Smaller ponies (A)
        ));

        // From http://whitehorseproductions.com/ecg_basics4.html
        // Rambler's Renown is a Cleveland Bay with a dappled pattern of sooty
        GENES.put("dapple", ImmutableList.of(
            0.9f,
            1f
        ));

        // White leg markings rare
        GENES.put("KIT", ImmutableList.of(
            0.88f,  // Wildtype
            0.95f,  // White boost
            1.0f    // Markings1
        ));
    }

    public static void init() {}
}
