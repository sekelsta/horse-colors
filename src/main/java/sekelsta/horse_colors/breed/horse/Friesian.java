package sekelsta.horse_colors.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class Friesian {
    public static Breed breed = new Breed(MongolianHorse.breed);

    static {
        // Population info from
        // "Genetic diversity in the Dutch Friesian horse"
        // Ducro, Bovenhuis, Neuteboom, Hellinga
        // Proceedings of the 8th World Congress on Genetics Applied to
        // Livestock Production, Belo Horizonte, Minas Gerais, Brazil,
        // 13-18 August, 2006 pp.08-03
        breed.name = "friesian";
        breed.population = 80000;
        Map<Gene, List<Float>> GENES = breed.genes;

        // Friesians can, rarely, have chestnut foals
        GENES.put(Gene.extension, ImmutableList.of(
            0.01f,  // Red
            1f      // Black
        ));
        // But otherwise they are black
        GENES.put(Gene.agouti, ImmutableList.of(
            1f      // Black
        ));
        GENES.put(Gene.gray, ImmutableList.of(
            1f      // Non-gray
        ));
        GENES.put(Gene.dun, ImmutableList.of(
            1f      // Non-dun 2
        ));
        GENES.put(Gene.silver, ImmutableList.of(
            1f      // Non-silver
        ));
        GENES.put(Gene.cream, ImmutableList.of(
            0.9f,   // Non-cream
            0f,     // Snowdrop
            0f,     // Pearl
            0f,     // Cream
            1f      // MAPT minor
        ));
        GENES.put(Gene.champagne, ImmutableList.of(
            1f     // Non-champagne
        ));
        // White markings rare
        GENES.put(Gene.KIT, ImmutableList.of(
            0.9f,   // Wildtype
            0.99f,  // White boost
            1f      // Markings1
        ));
        GENES.put(Gene.frame, ImmutableList.of(
            0f      // Non-frame
        ));
        GENES.put(Gene.MITF, ImmutableList.of(
            0f,     // SW1
            0f,     // SW3
            0f,     // SW5
            1.0f    // Wildtype
        ));
        GENES.put(Gene.PAX3, ImmutableList.of(
            1f      // Wildtype
        ));
        GENES.put(Gene.leopard, ImmutableList.of(
            1f      // Non-leopard
        ));

        // Friesians are pretty big. They probably don't have the small allele
        // at HMGA2.
        GENES.put(Gene.HMGA2, ImmutableList.of(
            1f      // Normal size
        ));

    }

    public static void init() {}
}
