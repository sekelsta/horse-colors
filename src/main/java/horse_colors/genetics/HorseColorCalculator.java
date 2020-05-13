package sekelsta.horse_colors.genetics;
import java.util.List;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.renderer.TextureLayer;

public class HorseColorCalculator
{
    private static final int GRAY_LEG_BITS = 2;
    private static final int FACE_MARKING_BITS = 2;
    private static final int LEG_MARKING_BITS = 12;

    private static final int GRAY_BODY_STAGES = 19;
    private static final int GRAY_MANE_STAGES = 20;

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

    public static void adjustConcentration(TextureLayer layer, float power) {

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

    public static void addWhite(TextureLayer layer, float white) {
        layer.red = (int)(255. * white + layer.red * (1f - white));
        layer.green = (int)(255. * white + layer.green * (1f - white));
        layer.blue = (int)(255. * white + layer.blue * (1f - white));
    }

    public static void setPheomelanin(TextureLayer layer, float concentration, float white) {
        layer.red = 0xe4;
        layer.green = 0xc0;
        layer.blue = 0x77;
        adjustConcentration(layer, concentration);
        addWhite(layer, white);
    }

    public static void setEumelanin(TextureLayer layer, float concentration, float white) {
        layer.red = 0xc0;
        layer.green = 0x9a;
        layer.blue = 0x5f;
        adjustConcentration(layer, concentration);
        addWhite(layer, white);
    }

    public static void colorRedBody(HorseGenome horse, TextureLayer layer) {
        // 5, 0.2 looks haflingerish
        // 5, 0.1 looks medium chestnut
        // 6, 0.1 looks liver chestnutish
        float concentration = 5f;
        float white = 0.08f;

        if (horse.isDoubleCream()) {
            concentration *= 0.1f;
            white += 0.4f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.05f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.7f;
            white += 0.15f;
        }
        else if (horse.isPearl()) {
            concentration *= 0.6f;
            white += 0.15f;
        }
        setPheomelanin(layer, concentration, white);

        // Treat liver like it leaks some eumelanin into the coat
        if (horse.isChestnut() 
                && horse.isHomozygous("liver", HorseAlleles.LIVER)) {
            TextureLayer dark = new TextureLayer();
            setPheomelanin(dark, concentration * 5f, white);
            float a = 0.6f;
            layer.red = (int)(dark.red * a + layer.red * (1 - a));
            layer.green = (int)(dark.green * a + layer.green * (1 - a));
            layer.blue = (int)(dark.blue * a + layer.blue * (1 - a));
            layer.clamp();
        }
    }

    public static TextureLayer getRedBody(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("base");
        colorRedBody(horse, layer);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void colorBlackBody(HorseGenome horse, TextureLayer layer) {
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
    }

    public static TextureLayer getBlackBody(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        TextureLayer layer = new TextureLayer();

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
            case HorseAlleles.A_BAY_SEMIWILD:
            case HorseAlleles.A_BAY_WILD:
                layer.name = fixPath("bay");
        }
        colorBlackBody(horse, layer);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void addRedManeTail(HorseGenome horse, List<TextureLayer> layers) {
        final float PALOMINO_POWER = 0.4f;
        if (!horse.isChestnut()) {
            return;
        }

        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            TextureLayer palomino_mane = new TextureLayer();
            palomino_mane.name = fixPath("manetail");
            colorRedBody(horse, palomino_mane);
            adjustConcentration(palomino_mane, PALOMINO_POWER);
            setGrayConcentration(horse, palomino_mane);
            layers.add(palomino_mane);
        }

        if (!horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)
                && !horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            // No flaxen, nothing to do
            return;
        }

        TextureLayer flaxen = new TextureLayer();
        flaxen.name = fixPath("flaxen");
        colorRedBody(horse, flaxen);
        float power = 1f;
        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            power *= PALOMINO_POWER;
        }
        float white = 0f;
        if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN) 
                && horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            power *= 0.4f;
            white = 0.3f;
        }
        else if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)) {
            power *= 0.5f;
            white = 0.2f;
        }
        else if (horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            power *= 0.7f;
            white = 0.1f;
        }
        adjustConcentration(flaxen, power);
        setGrayConcentration(horse, flaxen);
        addWhite(flaxen, white);
        layers.add(flaxen);
    }

    public static TextureLayer getBlackManeTail(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        if (!horse.hasAllele("silver", HorseAlleles.SILVER)) {
            return null;
        }
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("flaxen");
        setEumelanin(layer, 0.2f, 0.0f);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void colorSkin(HorseGenome horse, TextureLayer layer) {
        if (horse.isDoubleCream()) {
            // Pink skin
            layer.red = 0xff;
            layer.green = 0xd6;
            layer.blue = 0xb6;
        }
        else {
            // Black skin
            setEumelanin(layer, 20f, 0.1f);
        }
    }

    public static void colorGray(HorseGenome horse, TextureLayer layer) {
        // Show skin very faintly through the white hairs
        colorSkin(horse, layer);
        addWhite(layer, 0.99f);
    }

    public static TextureLayer getNose(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("nose");
        colorSkin(horse, layer);
        return layer;
    }

    public static TextureLayer getHooves(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("hooves");
        colorSkin(horse, layer);
        addWhite(layer, 0.4f);
        // Multiply by the shell color of hooves
        layer.red = (int)((float)layer.red * 255f / 255f);
        layer.green = (int)((float)layer.green * 229f / 255f);
        layer.blue= (int)((float)layer.blue * 184f / 255f);
        layer.clamp();
        return layer;
    }

    public static void addDun(HorseGenome horse, List<TextureLayer> layers) {
        if (!horse.isDun()) {
            return;
        }
        TextureLayer white = new TextureLayer();
        white.name = fixPath("dun");
        white.alpha = (int)(0.15f * 255f);
        white.type = TextureLayer.Type.SHADE;
        layers.add(white);

        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("dun");
        layer.type = TextureLayer.Type.ROOT;
        float dunpower = 0.6f;
        int val = (int)(dunpower * 255);
        layer.red = val;
        layer.green = val;
        layer.blue = val;
        layers.add(layer);
    }

    public static TextureLayer getSooty(HorseGenome horse)
    {
        TextureLayer layer = new TextureLayer();

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
            layer.alpha /= 2;
        }

        colorBlackBody(horse, layer);
        setGrayConcentration(horse, layer);

        return layer;
    }

    public static TextureLayer getMealy(HorseGenome horse)
    {
        // Agouti black hides mealy
        if (horse.isHomozygous("agouti", HorseAlleles.A_BLACK)) {
            return null;
        }

        if (horse.hasAllele("light_belly", HorseAlleles.MEALY)) {
            TextureLayer light_belly = new TextureLayer();
            int spread = 1;
            int color = 0;
            if (horse.hasAllele("mealy1", HorseAlleles.MEALY)) {
                spread += 1;
            }
            if (horse.isHomozygous("mealy2", HorseAlleles.MEALY)) {
                color += 1;
            }
            light_belly.name = fixPath("mealy/mealy" + spread);
            colorRedBody(horse, light_belly);
            adjustConcentration(light_belly, 0.08f * (2 - color));
            return light_belly;
        }

        return null;
    }

    public static void addGray(HorseGenome horse, List<TextureLayer> layers) {
        if (!horse.isGray()) {
            return;
        }
        float rate = horse.getGrayRate();
        float mane_rate = horse.getGrayManeRate();

        int body_stage = grayStage(horse, rate, GRAY_BODY_STAGES, 0.25f);
        int mane_stage = grayStage(horse, mane_rate, GRAY_MANE_STAGES, 0.3f);

        if (body_stage > 0) {
            TextureLayer body = new TextureLayer();
            if (body_stage > GRAY_BODY_STAGES) {
                body.name = fixPath("body");
            }
            else {
                body.name = fixPath("gray/dapple" + body_stage);
            }
            colorGray(horse, body);
            layers.add(body);
        }

        if (mane_stage > 0) {
            TextureLayer mane = new TextureLayer();
            if (mane_stage > GRAY_MANE_STAGES) {
                mane.name = fixPath("manetail");
            }
            else {
                mane.name = fixPath("gray/mane" + mane_stage);
            }
            colorGray(horse, mane);
            layers.add(mane);
        }
    }

    // num_stages does not count the starting and ending stages
    public static int grayStage(HorseGenome horse, float rate, int num_stages, float delay) {
        final int YEAR_TICKS = HorseConfig.COMMON.yearLength.get();
        final int MAX_AGE = (int)(HorseConfig.COMMON.maxAge.get() * YEAR_TICKS);
        int age = horse.getAge() + 24000;
        age = Math.min(age, MAX_AGE);
        float gray_age = (float)age / (float)(YEAR_TICKS * rate);
        gray_age = (gray_age - delay) / (1f - delay);
        if (gray_age <= 0) {
            return 0;
        }
        if (gray_age >= 1f) {
            return num_stages + 1;
        }
        return (int)(gray_age * num_stages);
    }

    public static float grayConcentration(HorseGenome horse, float rate) {
        int stage = grayStage(horse, rate, 50, 0f);
        float val = 1f + 5f * stage / 50f * stage / 50f;
        return val;
    }

    public static void setGrayConcentration(HorseGenome horse, TextureLayer layer) {
        if (horse.isGray()) {
            float concentration = grayConcentration(horse, horse.getGrayRate());
            adjustConcentration(layer, concentration);
        }
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

    public static TextureLayer getFaceMarking(HorseGenome horse)
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

        TextureLayer layer = new TextureLayer();
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

    public static TextureLayer getPinto(HorseGenome horse)
    {
        TextureLayer layer = new TextureLayer();
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

    public static TextureLayer getLeopard(HorseGenome horse)
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
