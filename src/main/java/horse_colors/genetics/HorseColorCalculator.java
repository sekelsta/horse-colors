package sekelsta.horse_colors.genetics;
import java.util.List;
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

    public static void adjustConcentration(Layer layer, float power) {

        float r = layer.red / 255.0F;
        float g = layer.green / 255.0F;
        float b = layer.blue / 255.0F;

        float red = (float)Math.pow(r, power) * 255.0F;
        float green = (float)Math.pow(g, power) * 255.0F;
        float blue = (float)Math.pow(b, power) * 255.0F;

        layer.red = Math.max(0, Math.min(255, (int)red));
        layer.green = Math.max(0, Math.min(255, (int)green));
        layer.blue = Math.max(0, Math.min(255, (int)blue));
    }

    public static void addWhite(Layer layer, float white) {
        layer.red = (int)(255. * white + layer.red * (1f - white));
        layer.green = (int)(255. * white + layer.green * (1f - white));
        layer.blue = (int)(255. * white + layer.blue * (1f - white));
    }

    public static void setPheomelanin(Layer layer, float concentration, float white) {
        layer.red = 0xe4;
        layer.green = 0xbf;
        layer.blue = 0x77;
        adjustConcentration(layer, concentration);
        addWhite(layer, white);
    }

    public static void setEumelanin(Layer layer, float concentration, float white) {
        layer.red = 0xc0;
        layer.green = 0x9a;
        layer.blue = 0x5f;
        adjustConcentration(layer, concentration);
        addWhite(layer, white);
    }

    public static void colorRedBody(HorseGenome horse, Layer layer) {
        // 5, 0.2 looks haflingerish
        // 5, 0.1 looks medium chestnut
        // 6, 0.1 looks liver chestnutish
        float concentration = 5f;
        float white = 0f;

        if (horse.isChestnut() 
                && horse.isHomozygous("liver", HorseAlleles.LIVER)) {
            concentration *= 1.2f;
        }

        if (horse.isDoubleCream()) {
            concentration *= 0.04f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.05f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.84f;
        }
        else if (horse.isPearl()) {
            concentration *= 0.6f;
            white += 0.15f;
        }
        setPheomelanin(layer, concentration, white);

        setDun(horse, layer);
    }

    public static Layer getRedBody(HorseGenome horse) {
        Layer layer = new Layer();
        layer.name = fixPath("base");
        colorRedBody(horse, layer);
        return layer;
    }

    public static void colorBlackBody(HorseGenome horse, Layer layer) {
        float concentration = 20f;
        float white = 0f;
        if (horse.isDoubleCream()) {
            concentration *= 0.02f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.025f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.5f;
        }
        else if (horse.isPearl()) {
            concentration *= 0.2f;
            white += 0.2f;
        }

        if (horse.hasAllele("silver", HorseAlleles.SILVER)) {
            concentration *= 0.4f;
        }
 

        setEumelanin(layer, concentration, white);
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
                layer.name = fixPath("bay");
                break;
            case HorseAlleles.A_BAY_SEMIWILD:
                layer.name = fixPath("semiwild_bay");
                break;
            case HorseAlleles.A_BAY_WILD:
                layer.name = fixPath("wild_bay");
        }
        colorBlackBody(horse, layer);
        return layer;
    }

    public static void addRedManeTail(HorseGenome horse, List<Layer> layers) {
        if (!horse.isChestnut()) {
            return;
        }

        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            Layer palomino_mane = new Layer();
            palomino_mane.name = fixPath("manetail");
            colorRedBody(horse, palomino_mane);
            adjustConcentration(palomino_mane, 0.04f);
            layers.add(palomino_mane);
        }

        if (!horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)
                && !horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            // No flaxen, nothing to do
            return;
        }

        Layer flaxen = new Layer();
        flaxen.name = fixPath("flaxen");
        colorRedBody(horse, flaxen);
        float power = 1f;
        if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN) 
                && horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            power = 0.2f;
        }
        else if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)) {
            power = 0.3f;
        }
        else if (horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            power = 0.6f;
        }
        adjustConcentration(flaxen, power);
        layers.add(flaxen);
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
        setEumelanin(layer, 0.2f, 0.0f);
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
            // Black skin
            setEumelanin(layer, 20f, 0.1f);
        }
        return layer;
    }

    public static Layer getHooves(HorseGenome horse) {
        Layer layer = new Layer();
        layer.name = fixPath("hooves");
        colorBlackBody(horse, layer);
        addWhite(layer, 0.4f);
        // Multiply by the shell color of hooves
        layer.red = (int)((float)layer.red * 255f / 255f);
        layer.green = (int)((float)layer.green * 229f / 255f);
        layer.blue= (int)((float)layer.blue * 184f / 255f);
        layer.clamp();
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
        layer.next = new Layer();
        layer.next.name = fixPath("dun");
        layer.next.type = Layer.Type.MASK;

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
        if (!horse.isDoubleCream() &&!horse.isCreamPearl()) {
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
