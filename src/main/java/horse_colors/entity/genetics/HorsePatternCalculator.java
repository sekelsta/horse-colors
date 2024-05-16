package sekelsta.horse_colors.entity.genetics;
import java.util.*;

import sekelsta.horse_colors.HorseConfig;
import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.client.renderer.TextureLayerGroup;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class HorsePatternCalculator {

    public static boolean hasPigmentInEars(EquineGenome horse) {
        int white = (int)(getSplashFactor(horse) * (1f + horse.getSabinoFactor() / 100f));
        return white <= 60;
    }

    public static int getSplashFactor(EquineGenome horse) {
        int white = -2;

        if (horse.species == Species.DONKEY) {
            white -= 4;
        }

        if (horse.hasAllele(Gene.white_suppression, 1))
        {
            white -= 4;
        }

        white += 5 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW1);
        white += 8 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW3);
        white += 7 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW5);

        white += 6 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW2);
        white += 7 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW4);

        white += 2 * horse.countAlleles(Gene.white_star, 1);

        if (horse.hasMC1RWhiteBoost()) {
            white += 1;
        }

        if (white > 0) {
            white = (int)Math.round(Math.pow(white, 1.25));
        }

        return white;
    }

    public static void addFaceMarkings(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        int splash = getSplashFactor(horse);
        int white = 2 * splash + horse.getSabinoFactor();

        int random = horse.getRandom("face_white");

        white += Math.min(2 * white, random & 7 - 4);
        random = random >>> 3;
        String symmetry = (random & 1) == 0 ? "_l" : "_r";

        if (white <= 0) {
            return;
        }

        TextureLayer layer = new TextureLayer();

        String marking = null;
        if (white < 10) {
            marking = "star";
        }
        else if (white < 16) {
            marking = "lightning" + symmetry;
        }
        else if (white < 20) {
            marking = "star2" + symmetry;
        }
        else if (white < 30 && splash >= 5) {
            marking = "star3";
        }
        else if (white < 30) {
            marking = "strip";
        }
        else if (white < 50 && splash >= 7) {
            marking = "star_snip" + symmetry;
        }
        else if (white >= 50 && splash >= 7) {
            marking = "broad_blaze";
        }
        else {
            marking = "blaze";
        }

        layer.name = HorseColorCalculator.fixPath("face/" + marking);
        textureLayers.add(layer);
    }

    public static void addLegMarkings(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        String[] legs = new String[4];
        int random = horse.getRandom("leg_white");
        int base_white = horse.getSabinoFactor() / 4 + getSplashFactor(horse) / 6;
        for (int i = 0; i < 4; ++i) {
            int w = base_white;
            if (i < 2) {
                w += horse.countAlleles(Gene.white_forelegs, 1);
            }
            else {
                w += horse.countAlleles(Gene.white_hindlegs, 1);
            }
            int r = random & 7 - 4;
            random = random >>> 3;
            w += Math.min(r, 2 * w);

            if (w < 1) {
                legs[i] = null;
            }
            else {
                legs[i] = HorseColorCalculator.fixPath("socks/" + String.valueOf(i) + "_" + String.valueOf(Math.min(7, w - 1)));
            }
        }

        for (String marking : legs) {
            if (marking != null) {
                TextureLayer layer = new TextureLayer();
                layer.name = marking;
                textureLayers.add(layer);
            }
        }
    }

    public static void addPinto(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        TextureLayer layer = new TextureLayer();
        if (horse.isWhite())
        {
            layer.name = HorseColorCalculator.fixPath("pinto/white");
            textureLayers.add(layer);
            return;
        }

        String pinto = null;
        int sabino = horse.getSabinoFactor();

        if (horse.hasAllele(Gene.KIT, HorseAlleles.KIT_DONKEY_SPOTTING)) {
            if (horse.isHomozygous(Gene.tyger, 1) || sabino > 30) {
                pinto = "tyger_spotting";
            }
            else {
                pinto = "donkey_spotting";
            }
        }
        else if (horse.isTobiano())
        {
            // Splashed white and tobiano can combine to make most of the horse white
            // See Pacific Pintos, Pacific Cloud Nine
            if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1))
            {
                pinto = "medicine_hat";
            }
            else if (sabino >= 35)
            {
                pinto = "sabino_tobiano";
            }
            else if (horse.hasAllele(Gene.frame, HorseAlleles.FRAME))
            {
                if ((horse.getRandom("pinto") & 1) == 0) {
                    pinto = "tovero";
                }
                else
                {
                    pinto = "war_shield";
                }
            }
            else if (horse.isHomozygousTobiano()) {
                if ((horse.getRandom("pinto") & 1) == 0) {
                    pinto = "homozygous_tobiano";
                }
                else
                {
                    pinto = "tobiano";
                }
            }
            else
            {
                pinto = "tobiano";
            }
        }
        else if (sabino >= 50)
        {
            if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1)) {
                pinto = "sabino_splash";
            }
            else if (horse.hasAllele(Gene.frame, HorseAlleles.FRAME)) {
                pinto = "frame_sabino";
            }
            else {
                pinto = "sabino_50";
            }
        }
        else if (horse.hasAllele(Gene.frame, HorseAlleles.FRAME))
        {
            if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1)) {
                pinto = "frame_splash";
            }
            else {
                pinto = "frame";
            }
        }
        else if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1))
        {
            pinto = "splash";
        }
        else if (sabino >= 40) {
            pinto = "sabino_40";
        }
        else if (sabino >= 25) {
            pinto = "sabino_25";
        }
        else {
            return;
        }
        layer.name = HorseColorCalculator.fixPath("pinto/" + pinto);

        if (horse.isHomozygousTobiano()) {
            int cat_tracks = 5;
            cat_tracks -= horse.getRandom("cat_tracks") & 1;
            cat_tracks -= getSplashFactor(horse) / 3;
            cat_tracks -= horse.getSabinoFactor() / 4;
            if (cat_tracks > 0) {
                cat_tracks = Math.min(5, cat_tracks);
                TextureLayer cat = new TextureLayer();
                cat.name = HorseColorCalculator.fixPath("repigmentation/cat_tracks" + cat_tracks);
                cat.type = TextureLayer.Type.MASK;
                TextureLayerGroup layers = new TextureLayerGroup();
                layers.add(layer);
                layers.add(cat);
                layer = layers;            
            }
        }

        textureLayers.add(layer);
    }

    public static void addLeopard(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        if (!horse.hasAllele(Gene.leopard, HorseAlleles.LEOPARD)) {
            return;
        }
        TextureLayer hooves = new TextureLayer();
        if (horse.isHomozygous(Gene.leopard, HorseAlleles.LEOPARD)) {
            hooves.name = HorseColorCalculator.fixPath("leopard/lplp_features");
        }
        else {
            hooves.name = HorseColorCalculator.fixPath("leopard/lp_features");
        }
        textureLayers.add(hooves);
        int patn = 7 * horse.countAlleles(Gene.PATN1, HorseAlleles.PATN);
        patn += 2 * horse.countAlleles(Gene.PATN2, HorseAlleles.PATN);
        patn += horse.countAlleles(Gene.PATN3, HorseAlleles.PATN);

        int boost = 0;
        if (horse.hasAllele(Gene.leopard_suppression, 1)) {
            boost -= 1 + horse.countAlleles(Gene.PATN1, HorseAlleles.PATN);
        }
        if (horse.isHomozygous(Gene.leopard_suppression2, 1)) {
            boost -= 1;
        }
        if (horse.hasAllele(Gene.PATN_boost1, 1)) {
            boost += 1;
        }
        if (horse.isHomozygous(Gene.PATN_boost2, 1)) {
            boost += 1;
        }

        TextureLayer spread = new TextureLayer();
        if (patn == 0)
        {
            spread.name = HorseColorCalculator.fixPath("leopard/varnish_roan");
            textureLayers.add(spread);
            return;
        }
        else {
            patn += boost + horse.getSabinoFactor() / 8 + getSplashFactor(horse) / 6;

            if (patn < 1) {
                spread.name = HorseColorCalculator.fixPath("leopard/varnish_roan");
            }
            else {
                spread.name = HorseColorCalculator.fixPath("leopard/blanket" + patn);
            }
        }
        TextureLayer spots = new TextureLayer();
        if (horse.isHomozygous(Gene.leopard, HorseAlleles.LEOPARD))
        {
            spots.name = HorseColorCalculator.fixPath("leopard/fewspot");
        }
        else if (horse.hasAllele(Gene.white_suppression, 1)) {
            spots.name = HorseColorCalculator.fixPath("leopard/leopard_large");
        }
        else if (horse.hasAllele(Gene.marble, 1)) {
            spots.name = HorseColorCalculator.fixPath("leopard/leopard_marble");
        }
        else
        {
            spots.name = HorseColorCalculator.fixPath("leopard/leopard");
        }
        if (patn >= 8) {
            textureLayers.add(spots);
            return;
        }
        spots.type = TextureLayer.Type.MASK;
        // Make the spots mask the spread
        ArrayList<TextureLayer> layers = new ArrayList<>();
        layers.add(spread);
        layers.add(spots);
        textureLayers.add(new TextureLayerGroup(layers));
    }
}
