package sekelsta.horse_colors.util;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.HorseGeneticEntity;

public class HorseColorCalculator
{
    private static final int GRAY_LEG_BITS = 2;
    private static final int FACE_MARKING_BITS = 2;
    private static final int LEG_MARKING_BITS = 12;

    public static String fixPath(String folder, String inStr) {
        if (inStr == null || inStr.contains(".png")) {
            return inStr;
        }
        else if (inStr == "")
        {
            return null;
        }
        else {
            if (folder != null && folder != "") {
                folder = folder + "/";
            }
            else {
                folder = "";
            }
            return "horse_colors:textures/entity/horse/" + folder + inStr +".png";
        }
    }

    public static void setChestnut(Layer layer) {
            layer.red = 0xa6;
            layer.green = 0x57;
            layer.blue = 0x2e;
    }

    public static void setLiverChestnut(Layer layer) {
            layer.red = 0x76;
            layer.green = 0x3e;
            layer.blue = 0x1f;
    }

    public static void setGolden(Layer layer) {
            layer.red = 0xdc;
            layer.green = 0xa3;
            layer.blue = 0x61;
    }

    public static void setCreamy(Layer layer) {
            layer.red = 0xfd;
            layer.green = 0xed;
            layer.blue = 0xd2;
    }

    public static void setFlaxen(Layer layer) {
            layer.red = 0xea;
            layer.green = 0xce;
            layer.blue = 0xb3;
    }

    public static void setBlack(Layer layer) {
            layer.red = 0x18;
            layer.green = 0x1a;
            layer.blue = 0x1c;
    }

    public static void setSmokyBlack(Layer layer) {
            layer.red = 0x25;
            layer.green = 0x1f;
            layer.blue = 0x1c;
    }

    public static void setSmokyCream(Layer layer) {
            layer.red = 0xed;
            layer.green = 0xd3;
            layer.blue = 0xa8;
    }

    public static void setBrownBlack(Layer layer) {
            layer.red = 0x1d;
            layer.green = 0x1b;
            layer.blue = 0x1a;
    }

    public static void setChocolate(Layer layer) {
            layer.red = 0x3f;
            layer.green = 0x28;
            layer.blue = 0x1d;
    }

    public static void setSmokySilver(Layer layer) {
            layer.red = 0x3f;
            layer.green = 0x29;
            layer.blue = 0x1d;
    }

    public static Layer getRedBody(AbstractHorseGenetic horse) {
        Layer layer = new Layer();
        layer.name = fixPath("", "base");
        layer.shading = fixPath("", "shading");
        if (horse.isDoubleCream()) {
            setCreamy(layer);
        }
        else if (horse.hasCream()) {
            setGolden(layer);
        }
        else if (horse.isHomozygous("liver", HorseAlleles.LIVER)) {
            setLiverChestnut(layer);
        }
        else {
            setChestnut(layer);
        }
        return layer;
    }

    public static Layer getBlackBody(AbstractHorseGenetic horse) {
        if (horse.isChestnut()) {
            return null;
        }
        Layer layer = new Layer();
        layer.shading = fixPath("", "shading");

        switch(horse.getMaxAllele("agouti"))
        {
            case HorseAlleles.A_BLACK:
                layer.name = fixPath("", "base");
                break;
            case HorseAlleles.A_SEAL:
            case HorseAlleles.A_BROWN:
                layer.name = fixPath("", "brown");
                break;
            case HorseAlleles.A_BAY_DARK:
            case HorseAlleles.A_BAY:
            case HorseAlleles.A_BAY_LIGHT:
            case HorseAlleles.A_BAY_WILD:
            case HorseAlleles.A_BAY_MEALY:
                layer.name = fixPath("", "bay");
        }
        if (horse.hasAllele("silver", HorseAlleles.SILVER)) {
            if (horse.isDoubleCream()) {
                setSmokyCream(layer);
            }
            else if (horse.hasCream()) {
                setSmokySilver(layer);
            }
            else {
                setChocolate(layer);
            }
        }
        else {
            if (horse.isDoubleCream()) {
                setSmokyCream(layer);
            }
            else if (horse.hasCream()) {
                setSmokyBlack(layer);
            }
            else {
                setBlack(layer);
            }
        }
        return layer;
    }

    public static Layer getRedManeTail(AbstractHorseGenetic horse) {
        if (!horse.isChestnut()) {
            return null;
        }
        Layer layer = new Layer();
        layer.shading = fixPath("", "shading");
        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            layer.name = fixPath("", "manetail");
            setCreamy(layer);
        }
        else if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN) 
                && horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            layer.name = fixPath("", "flaxen");
            setFlaxen(layer);
        }
        else if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN) 
                || horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            layer.name = fixPath("", "flaxen");
            layer.alpha = 255 / 3;
            setFlaxen(layer);
        }
        else {
            return null;
        }
        return layer;
    }

    public static Layer getBlackManeTail(AbstractHorseGenetic horse) {
        if (horse.isChestnut()) {
            return null;
        }
        if (!horse.hasAllele("silver", HorseAlleles.SILVER)) {
            return null;
        }
        Layer layer = new Layer();
        layer.name = fixPath("", "flaxen");
        layer.shading = fixPath("", "shading");
        setFlaxen(layer);
        return layer;
    }

    public static Layer getNose(AbstractHorseGenetic horse) {
        Layer layer = new Layer();
        layer.name = fixPath("", "nose");
        if (horse.isDoubleCream()) {
            // The base texture already comes with a pink nose
            return null;
        }
        else {
            setBlack(layer);
        }
        return layer;
    }

    public static void setDun(AbstractHorseGenetic horse, Layer base) {
        if (base == null) {
            return;
        }
        if (!horse.isDun()) {
            return;
        }
        Layer layer = new Layer();
        layer.name = base.name;
        layer.shading = base.shading;
        layer.mask = fixPath("", "dun");
        float dunPower = 0.5F;
        // Add 1 to each color because 0 isn't allowed
        // Cap at 255
        float red = Math.min(255, base.red + 1);
        float green = Math.min(255, base.green + 1);
        float blue = Math.min(255, base.blue + 1);
        float avg = (red + green + blue) / 3.0F;
        float targetAvg = 255.0F * dunPower + avg * (1.0F - dunPower);
        float multiplier = targetAvg / avg;
        if (multiplier < 1.0F) {
            System.out.println("Error: dun is too dark");
        }
        int c = 255 / 4;
        layer.red = Math.min(255, (int)(c + red * multiplier));
        layer.green = Math.min(255, (int)(c + green * multiplier));
        layer.blue = Math.min(255, (int)(c + blue * multiplier));
        layer.alpha = base.alpha * 2 / 3;
        base.next = layer;
    }

    public static Layer getGray(AbstractHorseGenetic horse) {
        if (!horse.isGray()) {
            return null;
        }
        Layer layer = new Layer();
        if (horse.isDoubleCream()) {
            layer.name = fixPath("base", "white");
        }
        else {
            layer.name = fixPath("base", "gray");
        }
        return layer;
    }

    public static String getSooty(AbstractHorseGenetic horse)
    {
        if (horse.isHomozygous("cream", HorseAlleles.CREAM))
        {
            return null;
        }
        int sooty_level = 0;
        boolean is_gray = horse.getPhenotype("gray") != 0;
        if (!is_gray)
        {
            sooty_level = horse.getPhenotype("sooty");
        }
        else
        {
            sooty_level = horse.getPhenotype("slow_gray");
        }
        if (sooty_level == 0)
        {
            return null;
        }

        boolean is_dun = horse.getPhenotype("dun") == 3 
                        && horse.getPhenotype("gray") == 0;
        String suffix = "";
        if (is_dun)
        {
            if (sooty_level == 0)
            {
                return "sooty_dun";
            }
            sooty_level -= 1;
            suffix = "_dun";
        }

        if (sooty_level == 3)
        {
            suffix += "_dark";
        }
        else if (sooty_level == 1)
        {
            suffix += "_light";
        }

        if (is_gray)
        {
            int random = (horse.getHorseVariant("random") << 1) >>> 1;
            return ((random % 4 == 1)? "sooty_dappled" : "gray") + suffix;
        }

        // TODO: change this
        String type = "countershaded";
        boolean is_chestnut = horse.isChestnut()
            && !horse.hasAllele("cream", HorseAlleles.CREAM)
            && horse.getPhenotype("liver") != 0;
        if (horse.getPhenotype("dapple") == 1)
        {
            type = is_chestnut? "even" : "dappled";
        }

        String prefix = is_gray? "gray" : "sooty_" + type;
        return prefix + suffix;
    }

    public static String getMealy(AbstractHorseGenetic horse)
    {
        // TODO
        return null;
    }

    public static String getLegs(AbstractHorseGenetic horse)
    {
        // Dappled gray horses can have dark legs
        if (horse.getPhenotype("gray") != 0
            && horse.getPhenotype("slow_gray") > 1)
        {
            int random = (horse.getHorseVariant("random") << 1) >>> 1;
            if (random % 4 == 0)
            {
                return "bay_legs";
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    public static String getGrayMane(AbstractHorseGenetic horse)
    {
        if (horse.isHomozygous("gray", HorseAlleles.GRAY)
            && horse.getPhenotype("gray_mane") == 2)
        {

        }
        // Only for dappled gray horses, not non-gray (0) nor full gray (2)
        if (horse.getPhenotype("gray") != 1)
        {
            return null;
        }
        // Doesn't affect double dilute creams
        if (horse.isHomozygous("cream", HorseAlleles.CREAM))
        {
            return null;
        }

        int gray = horse.getPhenotype("slow_gray");
        int mane = horse.getPhenotype("gray_mane");
        if (mane == 2 && gray > 1)
        {
            return "black_mane";
        }
        else if (mane == 0)
        {
            return null;
        }
        return "gray_mane";
    }

    public static String getFaceMarking(AbstractHorseGenetic horse)
    {
        int white = -2;
        if (horse.getPhenotype("white_suppression") != 0)
        {
            white -= 4;
        }

        white += horse.countAlleles("KIT", HorseAlleles.KIT_WHITE_BOOST);
        white += horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS1);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS2);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS3);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS4);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS5);
        white += 3 * horse.getPhenotype("W20");
        white += 4 * horse.countAlleles("KIT", HorseAlleles.KIT_FLASHY_WHITE);

        white += 6 * horse.countAlleles("MITF", HorseAlleles.MITF_SW1);
        white += 9 * horse.countAlleles("MITF", HorseAlleles.MITF_SW3);
        white += 8 * horse.countAlleles("MITF", HorseAlleles.MITF_SW5);

        white += 7 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW2);
        white += 8 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW4);

        white += 3 * horse.countAlleles("white_star", 1);
        white += horse.countAlleles("white_forelegs", 1);
        white += horse.countAlleles("white_hindlegs", 1);


        // Anything after here doesn't create face white from scratch, but
        // only increases the size
        if (white <= -2) {
            return null;
        }

        if (horse.hasMC1RWhiteBoost()) {
            white += 2;
        }

        // Turn a signed integer into unsigned, also drop a few bits 
        // used elsewhere
        int random = (horse.getHorseVariant("random") << 1) 
                        >>> (1 + GRAY_LEG_BITS);

        white += random & 3;


        if (white <= 0) {
            return null;
        }
        int face_marking = white / 5;

        switch (face_marking)
        {
            case 0:
                return null;
            case 1:
                return "star";
            case 2:
                return "strip";
            case 3:
                return "blaze";
            default:
                return "blaze";
        }
    }

    public static String[] getLegMarkings(AbstractHorseGenetic horse)
    {
        int white = -3;
        if (horse.getPhenotype("white_suppression") != 0)
        {
            white -= 4;
        }

        white += horse.countAlleles("KIT", HorseAlleles.KIT_WHITE_BOOST);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS1);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS2);
        white += 4 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS3);
        white += 5 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS4);
        white += 6 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS5);
        white += 7 * horse.getPhenotype("W20");
        white += 8 * horse.countAlleles("KIT", HorseAlleles.KIT_FLASHY_WHITE);

        white += 2 * horse.countAlleles("MITF", HorseAlleles.MITF_SW1);
        white += 6 * horse.countAlleles("MITF", HorseAlleles.MITF_SW3);
        white += 2 * horse.countAlleles("MITF", HorseAlleles.MITF_SW5);

        white += 2 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW2);
        white += 3 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW4);
        white += horse.countAlleles("white_star", 1);

        int forelegs = white;
        forelegs += 2 * horse.countAlleles("white_forelegs", 1);

        int hindlegs = white;
        hindlegs += 2 * horse.countAlleles("white_hindlegs", 1);

        String[] legs = new String[4];

        // Anything after here doesn't create leg white from scratch, but
        // only increases the size
        int white_boost = 0;

        if (horse.hasMC1RWhiteBoost()) {
            white_boost += 2;
        }

        // Turn a signed integer into unsigned, also drop a few bits 
        // used elsewhere
        int random = (horse.getHorseVariant("random") << 1) 
                        >>> (1 + GRAY_LEG_BITS + FACE_MARKING_BITS);

        for (int i = 0; i < 4; ++i) {
            int r = random & 7;
            random = random >>> 3;
            int w = forelegs;
            if (i >= 2) {
                w = hindlegs;
            }
            // Add bonus from things that don't make socks 
            // on their own but still increase white
            if (w > -2) {
                w += white_boost;
            }

            if (w < 0) {
                legs[i] = null;
            }
            else {
                legs[i] = String.valueOf(i) + "_" + String.valueOf(Math.min(7, w / 2 + r));
            }
        }


        return legs;
    }

    public static String getPinto(AbstractHorseGenetic horse)
    {
        if (horse.getPhenotype("white") == 1)
        {
            return "white";
        }
        String pinto = null;
        int tobiano = horse.getPhenotype("tobiano");
        int sabino = horse.getPhenotype("sabino1");
        int splash = horse.getPhenotype("splash");
        int frame = horse.getPhenotype("frame");

        if (tobiano != 0)
        {
            if (frame == 1)
            {
                if (splash == 2)
                {
                    pinto = "medicine_hat";
                }
                else
                {
                    pinto = "war_shield";
                }
            }
            else
            {
                pinto = "tobiano";
            }
        }
        else if (frame == 1)
        {
            pinto = "frame";
        }
        else if (splash == 2)
        {
            pinto = "splash";
        }
        else if (sabino == 1)
        {
            pinto = "sabino";
        }
        return pinto;
    }

    public static String getLeopard(AbstractHorseGenetic horse)
    {
        if (horse.getPhenotype("leopard") == 0)
        {
            return null;
        }
        int patn = horse.getPhenotype("PATN");
        if (patn == 0)
        {
            return "varnish_roan";
        }
        if (horse.getPhenotype("leopard") == 1)
        {
            // TODO: different coverage based on the value of patn
            return "leopard";
        }
        else
        {
            // TODO: different coverage based on the value of patn
            return "fewspot";
        }
    }
}
