package sekelsta.horse_colors.entity.genetics;
import java.util.*;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.client.renderer.TextureLayerGroup;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;

public class HorsePatternCalculator {

    public static boolean hasPigmentInEars(EquineGenome horse) {
        // Use the old value until the new system is done.
        return getPreviousFaceWhiteLevel(horse) <= 18;/*
        WhiteBoost whiteBoost = new WhiteBoost(horse);
        int boost = whiteBoost.getForehead() * 2;
        boost += whiteBoost.getMuzzle();
        boost += whiteBoost.getNoseBridge();
        // TODO: check that this is the right number
        return boost <= 18;*/
    }

    // This will be used only until the new face markings are done
    public static int getPreviousFaceWhiteLevel(EquineGenome horse) {
        int white = -2;
        if (horse.hasAllele(Gene.white_suppression, 1))
        {
            white -= 4;
        }

        white += horse.countAlleles(Gene.KIT, HorseAlleles.KIT_WHITE_BOOST);
        white += horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS1);
        white += 2 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS2);
        white += 2 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS3);
        white += 3 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS4);
        white += 3 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS5);
        white += 3 * horse.countW20();;
        white += 4 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_FLASHY_WHITE);

        white += 6 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW1);
        white += 9 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW3);
        white += 8 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW5);

        white += 7 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW2);
        white += 8 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW4);

        white += 3 * horse.countAlleles(Gene.white_star, 1);
        white += horse.countAlleles(Gene.white_forelegs, 1);
        white += horse.countAlleles(Gene.white_hindlegs, 1);

        if (horse.hasMC1RWhiteBoost()) {
            white += 2;
        }
        return white;
    }

    // This will be used only until the new face markings are done
    public static TextureLayer getPreviousFaceMarking(EquineGenome horse)
    {
        int white = getPreviousFaceWhiteLevel(horse);
        // Turn a signed integer into unsigned, also drop a few bits 
        // used elsewhere
        int unused_bits = 2;
        int random = (horse.getRandom("leg_white") << 1) >>> (1 + unused_bits);

        white += random & 3;


        if (white <= 0) {
            return null;
        }
        int face_marking = white / 5;

        TextureLayer layer = new TextureLayer();
        String folder = "face/";
        switch (face_marking)
        {
            case 0:
                break;
            case 1:
                layer.name = HorseColorCalculator.fixPath(folder + "star");
                break;
            case 2:
                layer.name = HorseColorCalculator.fixPath(folder + "strip");
                break;
            case 3:
                layer.name = HorseColorCalculator.fixPath(folder + "blaze");
                break;
            default:
                layer.name = HorseColorCalculator.fixPath(folder + "blaze");
                break;
        }

        return layer;
    }

    public static void addFaceMarkings(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        TextureLayer face = getPreviousFaceMarking(horse);
        if (face != null && face.name != null) {
            textureLayers.add(face);
        }/*
        WhiteBoost whiteBoost = new WhiteBoost(horse);
        int random = HorseColorCalculator.randSource.getVal("face_white", horse.getChromosome("random"));

        int starSize = whiteBoost.getForehead();
        starSize += random & 1;
        int stripSize = whiteBoost.getNoseBridge();
        stripSize += (random >>> 1) &  1;
        int snipSize = whiteBoost.getMuzzle();
        snipSize += (random >>> 2)  & 1;

        TextureLayer star = new TextureLayer();

        if (starSize > 5) {
            star.name = HorseColorCalculator.fixPath("face/star_strip");
        }
        else if (starSize > 3) {
            star.name = HorseColorCalculator.fixPath("face/star_large");
        }
        else if (starSize > 0) {
            int star_choice = HorseColorCalculator.randSource.getVal("star_choice", horse.getChromosome("random")) & 3;
            String starpic = "face/star";
            if (star_choice == 1) {
                starpic += "_moon";
            }
            if (star_choice == 2) {
                starpic += "_lightning";
            }
            if (star_choice == 3) {
                starpic += "6";
            }
            star.name = HorseColorCalculator.fixPath(starpic);
        }
        else {
            star.name = null;
        }

        if (star.name != null) {
            textureLayers.add(star);
        }

        TextureLayer strip = new TextureLayer();

        if (stripSize > 3) {
            strip.name = HorseColorCalculator.fixPath("face/strip");
        }
        else {
            strip.name = null;
        }

        if (strip.name != null) {
            textureLayers.add(strip);
        }

        TextureLayer snip = new TextureLayer();

        if (snipSize > 5) {
            snip.name = HorseColorCalculator.fixPath("face/mouth");
        }
        else if (snipSize > 4) {
            snip.name = HorseColorCalculator.fixPath("face/snip_large");
        }
        else if (snipSize > 3) {
            snip.name = HorseColorCalculator.fixPath("face/snip_offcenter");
        }
        else if (snipSize > 2) {
            snip.name = HorseColorCalculator.fixPath("face/snip");
        }
        else if (snipSize > 1) {
            snip.name = HorseColorCalculator.fixPath("face/snip_small");
        }
        else {
            snip.name = null;
        }

        if (snip.name != null) {
            textureLayers.add(snip);
        }
/*
        int white = whiteBoost.getMuzzle() + whiteBoost.getNoseBridge() + whiteBoost.getForehead();
        white += random & 3;


        if (white <= 0) {
            return null;
        }
        int face_marking = white / 5;

        TextureLayer layer = new TextureLayer();
        String folder = "face/";
        switch (face_marking)
        {
            case 0:
                break;
            case 1:
                layer.name = HorseColorCalculator.fixPath(folder + "star");
                break;
            case 2:
                layer.name = HorseColorCalculator.fixPath(folder + "strip");
                break;
            case 3:
                layer.name = HorseColorCalculator.fixPath(folder + "blaze");
                break;
            default:
                layer.name = HorseColorCalculator.fixPath(folder + "blaze");
                break;
        }

        return layer;*/
    }

    public static void addLegMarkings(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        WhiteBoost whiteBoost = new WhiteBoost(horse);


        String[] legs = new String[4];

        // Turn a signed integer into unsigned, also drop a few bits 
        // used elsewhere
        int random = horse.getRandom("leg_white");
        // Make unsigned, plus drop 4 unused bits from the beginning
        // for compatibility with previous versions
        random = (random << 1) >>> 5;

        for (int i = 0; i < 4; ++i) {
            int r = random & 7;
            random = random >>> 3;
            int w = whiteBoost.getForelegs();
            if (i >= 2) {
                w = whiteBoost.getHindlegs();
            }
            // Add bonus from things that don't make socks 
            // on their own but still increase white
            if (w > -2) {
                w += whiteBoost.conditional;
            }

            if (w < 0) {
                legs[i] = null;
            }
            else {
                legs[i] = HorseColorCalculator.fixPath("socks/" + String.valueOf(i) + "_" + String.valueOf(Math.min(7, w / 2 + r)));
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

        String folder = "pinto/";

        if (horse.isTobiano())
        {
            // Splashed white and tobiano can combine to make most of the horse white
            // See Pacific Pintos, Pacific Cloud Nine
            if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1))
            {
                layer.name = HorseColorCalculator.fixPath(folder + "medicine_hat");
            }
            else if (horse.hasAllele(Gene.KIT, HorseAlleles.KIT_SABINO1))
            {
                layer.name = HorseColorCalculator.fixPath(folder + "sabino_tobiano");
            }
            else if (horse.hasAllele(Gene.frame, HorseAlleles.FRAME))
            {
                layer.name =  HorseColorCalculator.fixPath(folder + "war_shield");
            }
            else
            {
                layer.name = HorseColorCalculator.fixPath(folder + "tobiano");
            }
        }
        else if (horse.hasAllele(Gene.KIT, HorseAlleles.KIT_SABINO1))
        {
            if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1)) {
                layer.name = HorseColorCalculator.fixPath(folder + "sabino_splash");
            }
            else if (horse.hasAllele(Gene.frame, HorseAlleles.FRAME)) {
                layer.name = HorseColorCalculator.fixPath(folder + "frame_sabino");
            }
            else {
                layer.name = HorseColorCalculator.fixPath(folder + "sabino");
            }
        }
        else if (horse.hasAllele(Gene.frame, HorseAlleles.FRAME))
        {
            if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1)) {
                layer.name = HorseColorCalculator.fixPath(folder + "frame_splash");
            }
            else {
                layer.name = HorseColorCalculator.fixPath(folder + "frame");
            }
        }
        else if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1))
        {
            layer.name = HorseColorCalculator.fixPath(folder + "splash");
        }
        // Use the layer
        if (layer.name != null && !layer.name.equals("")) {
            textureLayers.add(layer);
        }
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
        TextureLayer spread = new TextureLayer();
        if (patn == 0)
        {
            spread.name = HorseColorCalculator.fixPath("leopard/varnish_roan");
            textureLayers.add(spread);
            return;
        }
        else {
            WhiteBoost whiteBoost = new WhiteBoost(horse);
            patn += whiteBoost.getBlanket();

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

    // For figuring out what parts of the horse get what amount more or less white
    public static class WhiteBoost {
        public int general = 0;
        // Does not make white on its own but increases white
        public int conditional = 0;
        public int leg = 0;
        public int face = 0;
        public int foreleg = 0;
        public int hindleg = 0;
        public int forehead = 0;
        public int noseBridge = 0;
        public int muzzle = 0;
        // As in leopard complex, not including PATN genes
        public int blanket = 0;

        // Any gene with more than 4 alleles that affect white levels should
        // usually be incomplete dominant so it is easier to calculate
        public WhiteBoost(EquineGenome horse) {
            foreleg = 2 * horse.countAlleles(Gene.white_forelegs, 1);
            hindleg = 2 * horse.countAlleles(Gene.white_hindlegs, 1);
            forehead += horse.countAlleles(Gene.white_star, 1);
            setOldLegWhite(horse);

            if (horse.hasMC1RWhiteBoost()) {
                conditional += 2;
            }

            setGeneralWhite(horse);
            

            // Face white
            face = getOldFaceWhiteLevel(horse);
        }

        private void setGeneralWhite(EquineGenome horse) {
            if (horse.hasAllele(Gene.white_suppression, 1))
            {
                general -= 4;
            }
            general += horse.countAlleles(Gene.KIT, HorseAlleles.KIT_WHITE_BOOST);
            general += horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS1);
            general += 2 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS2);
            general += 2 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS3);
            general += 3 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS4);
            general += 3 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS5);
            general += 3 * horse.countW20();;
            general += 4 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_FLASHY_WHITE);

            general += 2 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW1);
            general += 6 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW3);
            general += 2 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW5);

            general += 2 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW2);
            general += 3 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW4);
            if (horse.hasAllele(Gene.white_star, 1)) {
                general += 1;
            }
        }

        public static int getOldFaceWhiteLevel(EquineGenome horse) {
            int white = -2;

            white += 6 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW1);
            white += 9 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW3);
            white += 8 * horse.countAlleles(Gene.MITF, HorseAlleles.MITF_SW5);

            white += 7 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW2);
            white += 8 * horse.countAlleles(Gene.PAX3, HorseAlleles.PAX3_SW4);

            white += horse.countAlleles(Gene.white_forelegs, 1);
            white += horse.countAlleles(Gene.white_hindlegs, 1);

            if (horse.hasMC1RWhiteBoost()) {
                white += 2;
            }
            return white;
        }

        private void setOldLegWhite(EquineGenome horse) {
            int white = -3;

            white += 1 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS1);
            white += 1 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS2);
            white += 2 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS3);
            white += 2 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS4);
            white += 3 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_MARKINGS5);
            white += 4 * horse.countW20();
            white += 4 * horse.countAlleles(Gene.KIT, HorseAlleles.KIT_FLASHY_WHITE);
            this.leg = white;
        }

        public void setBlanketWhite(EquineGenome horse) {
            if (horse.hasAllele(Gene.leopard_suppression, 1)) {
                blanket -= 1 + horse.countAlleles(Gene.PATN1, HorseAlleles.PATN);
            }
            if (horse.isHomozygous(Gene.leopard_suppression2, 1)) {
                blanket -= 1;
            }
            if (horse.hasAllele(Gene.PATN_boost1, 1)) {
                blanket += 1;
            }
            if (horse.isHomozygous(Gene.PATN_boost2, 1)) {
                blanket += 1;
            }
        }

        public int getForelegs() {
            return general + leg + foreleg;
        }

        public int getHindlegs() {
            return general + leg + hindleg;
        }

        public int getBlanket() {
            return general / 2 + blanket;
        }

        public int getForehead() {
            return (general + face) / 5 + forehead;
        }

        public int getNoseBridge() {
            return (general + face) / 5 + noseBridge;
        }

        public int getMuzzle() {
            return (general + face) / 5 + muzzle;
        }
    }
}
