package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

public class Appaloosa {
    public static Breed breed = new Breed(QuarterHorse.breed);

    static {
        breed.name = "appaloosa";
        // Estimating population based on:
        // 670,000 appaloosas were registered between 1938 and 2008 (source:
        // https://www.appaloosa.com/popupwindows/factsheet.htm). That is 69
        // years, dividing by 69 and multiplying by a lifespan of 25 years gets
        // us an average of 242,000 appaloosas alive at any time.
        // 12,000 appaloosas were registered in 2014 (source:
        // https://www.ridewithequo.com/blog/the-horse-industry-by-the-numbers)
        // Multiplying that by 25, we get 302,000 alive at any time.
        // Since there are also appaloosas outside of America, take the higher
        // number.
        breed.population = 300000;

        Map<String, List<Float>> GENES = breed.genes;
        GENES.put("leopard", ImmutableList.of(
            0.5f,     // Non-leopard
            1f        // Leopard
        ));
        GENES.put("PATN1", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put("PATN2", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        GENES.put("PATN3", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        // No numbers for this but it does seem like black and bay are more
        // common and chestnut less common in appaloosas than in Quarter horses
        GENES.put("extension", ImmutableList.of(
            0.7f,   // Red
            1f      // Black
        ));
        GENES.put("agouti", ImmutableList.of(
            0.5f,      // Black
            0.7f,       // Seal
            0.5f,       // Seal unused
            0.5f,       // Bay unused
            1f          // Bay
        ));
        // Gray is discouraged
        GENES.put("gray", ImmutableList.of(
            0.996f,     // Non-gray
            1f          // Gray
        ));

        // Source: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4608717/
        GENES.put("HMGA2", ImmutableList.of(
            1f,     // Normal size
            0f      // Small
        ));
    }

    public static void init() {}
}
