package sekelsta.horse_colors.breed;

import com.google.common.collect.ImmutableList;
import java.util.*;

import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class BaseEquine {
    public static Breed<Gene> breed = new Breed<>(Gene.class);

    static {
        Map<Gene, List<Float>> genes = breed.genes;

        genes.put(Gene.extension, ImmutableList.of(
            0f,     // Red
            1f      // Black
        ));
        genes.put(Gene.gray, ImmutableList.of(
            1.0f,   // Non-gray
            1.0f    // Gray
        ));
        genes.put(Gene.dun, ImmutableList.of(
            0f,     // Non-dun 2
            0f,     // Non-dun 1
            0f,     // Dun
            1f      // Dun for donkeys
        ));
        genes.put(Gene.agouti, ImmutableList.of(
            0.0f,   // Black
            0.1f,   // Seal
            0.1f,   // Seal unused
            0.1f,   // Bay unused
            1f      // Bay
        ));
        genes.put(Gene.silver, ImmutableList.of(
            1.0f,  // Non-silver
            1.0f   // Silver
        ));
        genes.put(Gene.cream, ImmutableList.of(
            1f,     // Non-cream
            0f,     // Snowdrop
            0f,     // Pearl
            0f,     // Cream
            0f      // MAPT minor
        ));
        genes.put(Gene.liver, ImmutableList.of(
            0.1f,   // Liver
            1f      // Non-liver
        ));
        genes.put(Gene.flaxen1, ImmutableList.of(
            0.0f,   // Flaxen
            1f      // Non-flaxen
        ));
        genes.put(Gene.flaxen2, ImmutableList.of(
            0.2f,   // Flaxen
            1f      // Non-flaxen
        ));
        genes.put(Gene.dapple, ImmutableList.of(
            1.0f,   // Non-dapple
            1f      // Dapple
        ));
        genes.put(Gene.sooty1, ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        genes.put(Gene.sooty2, ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        genes.put(Gene.sooty3, ImmutableList.of(
            0.5f,   // Non-sooty
            1f      // Sooty
        ));
        genes.put(Gene.light_belly, ImmutableList.of(
            0f,     // Non-mealy
            1f      // Mealy
        ));
        genes.put(Gene.mealy1, ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        genes.put(Gene.mealy2, ImmutableList.of(
            0.0f,   // Non-mealy
            1f      // Mealy
        ));
        genes.put(Gene.white_suppression, ImmutableList.of(
            0f,     // Non white-suppression
            1f      // White suppression
        ));
        genes.put(Gene.KIT, ImmutableList.of(
            1f     // Wildtype
        ));
        genes.put(Gene.frame, ImmutableList.of(
            1f,     // Non-frame
            1f      // Frame
        ));
        genes.put(Gene.MITF, ImmutableList.of(
            0f,     // SW1
            0f,     // SW3
            0f,     // SW5
            1.0f    // Wildtype
        ));
        genes.put(Gene.PAX3, ImmutableList.of(
            1f,     // Wildtype
            0f,     // SW2
            1f,     // SW4
            1.0f    // Unused
        ));
        genes.put(Gene.leopard, ImmutableList.of(
            1f,     // Non-leopard
            1f      // Leopard
        ));
        genes.put(Gene.PATN1, ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        genes.put(Gene.PATN2, ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        genes.put(Gene.PATN3, ImmutableList.of(
            1f,     // Non-PATN
            1f      // PATN
        ));
        genes.put(Gene.gray_suppression, ImmutableList.of(
            1f,     // Non gray-suppression
            1f      // Gray suppression
        ));
        genes.put(Gene.slow_gray1, ImmutableList.of(
            0.5f,   // Lighter
            1f      // Darker
        ));
        genes.put(Gene.slow_gray2, ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        genes.put(Gene.slow_gray3, ImmutableList.of(
            0.75f,  // Lighter
            1f      // Darker
        ));
        genes.put(Gene.white_star, ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        genes.put(Gene.white_forelegs, ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        genes.put(Gene.white_hindlegs, ImmutableList.of(
            1f,     // Less white
            1f      // More white
        ));
        genes.put(Gene.gray_melanoma, ImmutableList.of(
            0.5f,   // Less melanoma
            1f      // More melanoma
        ));
        genes.put(Gene.gray_mane1, ImmutableList.of(
            0.25f,  // Lighter mane
            1f      // Lighter body
        ));
        genes.put(Gene.gray_mane2, ImmutableList.of(
            0.5f,   // Lighter mane
            1f      // Lighter body
        ));
        genes.put(Gene.rufous, ImmutableList.of(
            0.1f,   // Yellower
            1f      // Redder
        ));
        genes.put(Gene.dense, ImmutableList.of(
            0.9f,   // Lighter
            1f      // Darker
        ));
        genes.put(Gene.champagne, ImmutableList.of(
            1f,     // Non-champagne
            1f      // Champagne
        ));
        genes.put(Gene.cameo, ImmutableList.of(
            1f,     // Non-cameo
            1f      // Cameo
        ));
        genes.put(Gene.ivory, ImmutableList.of(
            1f,     // Non-ivory
            1f      // Ivory
        ));
        genes.put(Gene.donkey_dark, ImmutableList.of(
            0.3f,   // Lighter
            1f      // Darker
        ));
        genes.put(Gene.reduced_points, ImmutableList.of(
            1f,     // Higher leg black
            1f      // Lower leg black
        ));
        genes.put(Gene.light_legs, ImmutableList.of(
            1f,     // Mealy lightens the legs
            1f      // Mealy does not lighten the legs
        ));
        genes.put(Gene.less_light_legs, ImmutableList.of(
            1f,     // Mealy does not lighten the legs so much
            1f      // Mealy lightens the legs all the way
        ));
        genes.put(Gene.flaxen_boost, ImmutableList.of(
            0.95f,  // Flaxen manes are darker
            1f      // Flaxen manes are lighter
        ));
        genes.put(Gene.light_dun, ImmutableList.of(
            0f,     // Darker dun
            1f      // Lighter dun
        ));
        genes.put(Gene.marble, ImmutableList.of(
            1f,     // Round leopard spots
            1f      // Stretched leopard spots
        ));
        genes.put(Gene.leopard_suppression, ImmutableList.of(
            1f,     // Full leopard
            1f      // Semileopard
        ));
        genes.put(Gene.leopard_suppression2, ImmutableList.of(
            1f,     // Full leopard
            1f      // Semileopard
        ));
        genes.put(Gene.PATN_boost1, ImmutableList.of(
            1f,     // Less pattern
            1f      // More pattern
        ));
        genes.put(Gene.PATN_boost2, ImmutableList.of(
            1f,     // Less pattern
            1f      // More pattern
        ));
        genes.put(Gene.dark_red, ImmutableList.of(
            0.5f,   // Lighter
            1f      // Darker
        ));
        genes.put(Gene.LCORL, ImmutableList.of(
            1f,     // Wild type (T)
            1f      // Larger warmbloods and drafts (C)
        ));
        genes.put(Gene.HMGA2, ImmutableList.of(
            1f,     // Wild type
            1f      // Smaller shetlands
        ));
        genes.put(Gene.mushroom, ImmutableList.of(
            1f,     // Wild type
            1f      // Mushroom
        ));
        genes.put(Gene.liver_boost, ImmutableList.of(
            1f,     // Wild type
            1f      // Darker liver chestnut
        ));

        ImmutableList<Float> halfAndHalf = ImmutableList.of(0.5f, 1f);
        for (int i = 0; i < 12; ++i) {
            genes.put(Gene.valueOf("speed" + i), halfAndHalf);
            genes.put(Gene.valueOf("jump" + i), halfAndHalf);
            genes.put(Gene.valueOf("health" + i), halfAndHalf);
        }
        for (int i = 0; i < 8; ++i) {
            genes.put(Gene.valueOf("athletics" + i), halfAndHalf);
        }
        // Sixteen equally likely alleles for each immune diversity gene
        ArrayList<Float> sixteen = new ArrayList<>();
        for (int i = 0; i < 16; ++i) {
            sixteen.add((i + 1) / 16f);
        }
        for (int i = 0; i < 8; ++i) {
            genes.put(Gene.valueOf("immune" + i), sixteen);
            genes.put(Gene.valueOf("mhc" + i), sixteen);
        }
        ImmutableList<Float> five = ImmutableList.of(0.2f, 0.4f, 0.6f, 0.8f, 1f);
        for (int i = 0; i < 8; ++i) {
            genes.put(Gene.valueOf("size_minor" + i), five);
        }

        ArrayList<Float> size_subtle = new ArrayList();
        size_subtle.add(0.2f);
        for (int i = 1; i < 9; ++i) {
            size_subtle.add(0.1f * (i + 2));
        }
        for (int i = 0; i < 8; ++i) {
            genes.put(Gene.valueOf("size_subtle" + i), size_subtle);
        }

        ImmutableList<Float> one = ImmutableList.of(1f);
        genes.put(Gene.size0, one);
        genes.put(Gene.size1, one);
        genes.put(Gene.size2, one);
        genes.put(Gene.size3, one);
        genes.put(Gene.size4, one);

        genes.put(Gene.double_ovulation, ImmutableList.of(
            0.8f,   // Twins less likely
            1.0f    // Twins more likely
        ));

        genes.put(Gene.donkey_size0, ImmutableList.of(
            1f
        ));
        genes.put(Gene.donkey_size1, ImmutableList.of(
            1f
        ));
        genes.put(Gene.donkey_size2, ImmutableList.of(
            1f
        ));
        genes.put(Gene.donkey_size3, ImmutableList.of(
            1f
        ));
        genes.put(Gene.donkey_size4, ImmutableList.of(
            1f
        ));
        genes.put(Gene.donkey_size5, ImmutableList.of(
            1f
        ));
        genes.put(Gene.donkey_size6, ImmutableList.of(
            1f
        ));

        // Non-albino
        genes.put(Gene.color, one);

        // No rabicano
        genes.put(Gene.rabicano, one);

        genes.put(Gene.blue_eye_shade1, ImmutableList.of(
            0.8f,   // Lighter blue eyes
            1f      // Darker blue eyes
        ));

        genes.put(Gene.blue_eye_shade2, ImmutableList.of(
            0.7f,   // Darker blue eyes
            1f      // Lighter blue eyes
        ));

        genes.put(Gene.blue_eye_shade3, ImmutableList.of(
            0.7f,   // Darker blue eyes
            1f      // Lighter blue eyes
        ));

        genes.put(Gene.tiger_eye, ImmutableList.of(
            1f,     // Wildtype
            1f      // Tiger eye
        ));

        genes.put(Gene.brown_eye_shade1, ImmutableList.of(
            0.6f,   // Darker brown eyes
            1f      // Lighter brown eyes
        ));

        genes.put(Gene.brown_eye_shade2, ImmutableList.of(
            0.6f,   // Darker brown eyes
            1f      // Lighter brown eyes
        ));

        genes.put(Gene.brown_eye_shade3, ImmutableList.of(
            0.6f,   // Darker brown eyes
            1f      // Lighter brown eyes
        ));

        genes.put(Gene.stripe_width, ImmutableList.of(
            0.7f,   // Wide
            1f      // Narrow
        ));
    }
}
