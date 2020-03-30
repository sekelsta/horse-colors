package sekelsta.horse_colors.genetics;
import sekelsta.horse_colors.renderer.ComplexLayeredTexture.Layer;

public class HorseColorCalculator
{
    private static final int GRAY_LEG_BITS = 2;
    private static final int FACE_MARKING_BITS = 2;
    private static final int LEG_MARKING_BITS = 12;

    public static String fixPath(String inStr) {
        if (inStr == null || inStr.contains(".png")) {
            return inStr;
        }
        else if (inStr == "")
        {
            return null;
        }
        else {
            return "horse_colors:textures/entity/horse/" + inStr +".png";
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

    public static void colorRedBody(HorseGenome horse, Layer layer) {
        if (horse.isDoubleCream()) {
            setCreamy(layer);
        }
        else if (horse.hasCream()) {
            setGolden(layer);
        }
        else if (horse.isChestnut() 
                && horse.isHomozygous("liver", HorseAlleles.LIVER)) {
            setLiverChestnut(layer);
        }
        else {
            setChestnut(layer);
        }
        setDun(horse, layer);
    }

    public static Layer getRedBody(HorseGenome horse) {
        Layer layer = new Layer();
        layer.name = fixPath("base");
        colorRedBody(horse, layer);
        return layer;
    }

    public static void colorBlackBody(HorseGenome horse, Layer layer) {
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
        setDun(horse, layer);
    }

    public static Layer getBlackBody(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        Layer layer = new Layer();

        switch(horse.getMaxAllele("agouti"))
        {
            case HorseAlleles.A_BLACK:
                layer.name = fixPath("base");
                break;
            case HorseAlleles.A_SEAL:
            case HorseAlleles.A_BROWN:
                layer.name = fixPath("brown");
                break;
            case HorseAlleles.A_BAY_DARK:
            case HorseAlleles.A_BAY:
            case HorseAlleles.A_BAY_LIGHT:
            case HorseAlleles.A_BAY_WILD:
            case HorseAlleles.A_BAY_MEALY:
                layer.name = fixPath("bay");
        }
        colorBlackBody(horse, layer);
        return layer;
    }

    public static Layer getRedManeTail(HorseGenome horse) {
        if (!horse.isChestnut()) {
            return null;
        }
        Layer layer = new Layer();
        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            layer.name = fixPath("manetail");
            setCreamy(layer);
        }
        else if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN) 
                && horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            layer.name = fixPath("flaxen");
            setFlaxen(layer);
        }
        else if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN) 
                || horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            layer.name = fixPath("flaxen");
            layer.alpha = 255 / 3;
            setFlaxen(layer);
        }
        else {
            return null;
        }
        return layer;
    }

    public static Layer getBlackManeTail(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        if (!horse.hasAllele("silver", HorseAlleles.SILVER)) {
            return null;
        }
        Layer layer = new Layer();
        layer.name = fixPath("flaxen");
        setFlaxen(layer);
        return layer;
    }

    public static Layer getNose(HorseGenome horse) {
        Layer layer = new Layer();
        layer.name = fixPath("nose");
        if (horse.isDoubleCream()) {
            // The base texture already comes with a pink nose
            return null;
        }
        else {
            setBlack(layer);
        }
        return layer;
    }

    public static Layer getHooves(HorseGenome horse) {
        Layer layer = new Layer();
        layer.name = fixPath("hooves");
        return layer;
    }

    public static void setDun(HorseGenome horse, Layer base) {
        if (base == null) {
            return;
        }
        if (!horse.isDun()) {
            return;
        }
        Layer layer = new Layer();
        layer.name = base.name;
        layer.mask = fixPath("dun");

        float r = base.red / 255.0F;
        float g = base.green / 255.0F;
        float b = base.blue / 255.0F;

        float dunpower = 0.6F;
        float white = 0.2F * (0.8F - Math.max(Math.max(r, g), b));
        float red = (float)Math.pow(white + r * (1.0F - white), dunpower) * 255.0F;
        float green = (float)Math.pow(white + g * (1.0F - white), dunpower) * 255.0F;
        float blue = (float)Math.pow(white + b * (1.0F - white), dunpower) * 255.0F;

        layer.red = Math.min(255, (int)red);
        layer.green = Math.min(255, (int)green);
        layer.blue = Math.min(255, (int)blue);
        base.next = layer;
    }

    public static Layer getGray(HorseGenome horse) {
        if (!horse.isGray()) {
            return null;
        }
        Layer layer = new Layer();
        layer.name = fixPath("base");
        if (!horse.isDoubleCream()) {
            layer.red = 0xeb;
            layer.green = 0xeb;
            layer.blue = 0xeb;
        }
        return layer;
    }

    public static Layer getSooty(HorseGenome horse)
    {
        Layer layer = new Layer();

        int sooty_level = horse.getSootyLevel();
        switch (sooty_level) {
            case 0:
                return null;
            case 1:
                layer.alpha = (int)(0.4F * 255.0F);
                break;
            case 2:
                layer.alpha = (int)(0.8F * 255.0F);
                break;
            case 3:
                layer.alpha = 255;
                break;
            default:
                layer.alpha = 255;
        }

        layer.name = fixPath("sooty_countershade");
        if (horse.isDappleInclined()) {
            layer.name = fixPath("sooty_dapple");
        }
        else if (horse.isChestnut()) {
            layer.name = fixPath("base");
            layer.alpha -= (int)(0.15f * 255.0F);
        }

        colorBlackBody(horse, layer);

        return layer;
    }

    public static Layer getMealy(HorseGenome horse)
    {
        // TODO
        return null;
    }

    public static String getGrayMane(HorseGenome horse)
    {
        // Only for dappled gray horses, not non-gray (0) nor full gray (2)
        if (horse.countAlleles("gray", HorseAlleles.GRAY) != 1)
        {
            return null;
        }
        // Doesn't affect double dilute creams
        if (horse.isHomozygous("cream", HorseAlleles.CREAM))
        {
            return null;
        }

        int gray = horse.getSlowGrayLevel();
        int mane = horse.countAlleles("gray_mane", 1);
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

    public static int getFaceWhiteLevel(HorseGenome horse) {
        int white = -2;
        if (horse.hasAllele("white_suppression", 1))
        {
            white -= 4;
        }

        white += horse.countAlleles("KIT", HorseAlleles.KIT_WHITE_BOOST);
        white += horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS1);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS2);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS3);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS4);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS5);
        white += 3 * horse.countW20();;
        white += 4 * horse.countAlleles("KIT", HorseAlleles.KIT_FLASHY_WHITE);

        white += 6 * horse.countAlleles("MITF", HorseAlleles.MITF_SW1);
        white += 9 * horse.countAlleles("MITF", HorseAlleles.MITF_SW3);
        white += 8 * horse.countAlleles("MITF", HorseAlleles.MITF_SW5);

        white += 7 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW2);
        white += 8 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW4);

        white += 3 * horse.countAlleles("white_star", 1);
        white += horse.countAlleles("white_forelegs", 1);
        white += horse.countAlleles("white_hindlegs", 1);

        if (horse.hasMC1RWhiteBoost()) {
            white += 2;
        }
        return white;
    }

    public static Layer getFaceMarking(HorseGenome horse)
    {
        int white = getFaceWhiteLevel(horse);
        // Turn a signed integer into unsigned, also drop a few bits 
        // used elsewhere
        int random = (horse.getChromosome("random") << 1) 
                        >>> (1 + GRAY_LEG_BITS);

        white += random & 3;


        if (white <= 0) {
            return null;
        }
        int face_marking = white / 5;

        Layer layer = new Layer();
        String folder = "face/";
        switch (face_marking)
        {
            case 0:
                break;
            case 1:
                layer.name = fixPath(folder + "star");
                break;
            case 2:
                layer.name = fixPath(folder + "strip");
                break;
            case 3:
                layer.name = fixPath(folder + "blaze");
                break;
            default:
                layer.name = fixPath(folder + "blaze");
                break;
        }

        return layer;
    }

    public static String[] getLegMarkings(HorseGenome horse)
    {
        int white = -3;
        if (horse.hasAllele("white_suppression", 1))
        {
            white -= 4;
        }

        white += horse.countAlleles("KIT", HorseAlleles.KIT_WHITE_BOOST);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS1);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS2);
        white += 4 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS3);
        white += 5 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS4);
        white += 6 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS5);
        white += 7 * horse.countW20();
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
        int random = (horse.getChromosome("random") << 1) 
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
                legs[i] = fixPath("socks/" + String.valueOf(i) + "_" + String.valueOf(Math.min(7, w / 2 + r)));
            }
        }


        return legs;
    }

    public static Layer getPinto(HorseGenome horse)
    {
        Layer layer = new Layer();
        if (horse.isWhite())
        {
            layer.name = fixPath("base");
            return layer;
        }

        String folder = "pinto/";

        if (horse.isTobiano())
        {
            if (horse.hasAllele("frame", HorseAlleles.FRAME))
            {
                if (horse.isHomozygous("MITF", HorseAlleles.MITF_SW1))
                {
                    layer.name = fixPath(folder + "medicine_hat");
                }
                else
                {
                    layer.name =  fixPath(folder + "war_shield");
                }
            }
            else
            {
                layer.name = fixPath(folder + "tobiano");
            }
        }
        else if (horse.hasAllele("frame", HorseAlleles.FRAME))
        {
            layer.name = fixPath(folder + "frame");
        }
        else if (horse.isHomozygous("MITF", HorseAlleles.MITF_SW1))
        {
            layer.name = fixPath(folder + "splash");
        }
        else if (horse.hasAllele("KIT", HorseAlleles.KIT_SABINO1))
        {
            layer.name = fixPath(folder + "sabino");
        }
        return layer;
    }

    public static Layer getLeopard(HorseGenome horse)
    {/*
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
        }*/return null;
    }
}
