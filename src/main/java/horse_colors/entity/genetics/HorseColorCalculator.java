package sekelsta.horse_colors.entity.genetics;
import java.util.*;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.client.renderer.TextureLayerGroup;
import sekelsta.horse_colors.util.Color;
import sekelsta.horse_colors.util.Pigment;

public class HorseColorCalculator
{
    private static final int GRAY_BODY_STAGES = 19;
    private static final int GRAY_MANE_STAGES = 20;

    private static final Color EUMELANIN = new Color(0xc0, 0x9a, 0x5f);
    private static final Color PHEOMELANIN = new Color(0xe4, 0xc0, 0x77);
    private static final Color MUSHROOM = new Color(0xde, 0xcf, 0xbc);
    private static final Color SHELL_HOOF = new Color(0xff, 0xe5, 0xb8);
    private static final Color PINK_SKIN = new Color(0xff, 0xd6, 0xb6);

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

    public static Pigment redFurPigment(HorseGenome horse) {
        // 5, 0.2 looks haflingerish
        // 5, 0.1 looks medium chestnut
        // 6, 0.1 looks liver chestnutish
        float concentration = 5f * getRandomShadeModifier(horse);
        float white = 0.08f;

        if (horse.isDoubleCream() || horse.isHomozygous("ivory", HorseAlleles.IVORY)) {
            concentration *= 0.05f;
            white += 0.4f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.1f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.6f;
            white += 0.15f;
            if (horse.hasAllele("cream", HorseAlleles.MATP_MINOR)) {
                concentration *= 0.6f;
                white += 0.04;
            }
        }
        else if (horse.isPearl()) {
            concentration *= 0.6f;
            white += 0.15f;
            if (horse.hasAllele("cream", HorseAlleles.MATP_MINOR)) {
                concentration *= 0.9f;
                white += 0.04f;
            }
        }
        else if (horse.isHomozygous("cream", HorseAlleles.MATP_MINOR)) {
            concentration *= 0.9f;
            white += 0.04f;
        }

        if (horse.hasAllele("cameo", HorseAlleles.CAMEO)) {
            concentration *= 0.3f;
            white += 0.25f;
        }

        if (horse.hasAllele("rufous", 1)) {
            concentration *= 1.1f;
        }

        if (horse.isHomozygous("dark_red", 1)) {
            concentration *= 1.2f;
        }

        if (horse.isHomozygous("dense", 1)) {
            concentration *= 1.1f;
            white -= 0.03f;
        }

        white = Math.max(white, 0);

        Color color;
        if (horse.isMushroom()) {
            color = MUSHROOM;
        }
        else {
            color = PHEOMELANIN;
        }
        return new Pigment(color, concentration, white);
    }

    public static Color redBodyColor(HorseGenome horse) {
        Color color = redFurPigment(horse).toColor();
        // Treat liver like it leaks some eumelanin into the coat
        int liv = horse.countAlleles("liver", HorseAlleles.LIVER);
        if (liv > 0) {
            Color dark = blackBodyColor(horse);
            dark.addWhite(0.02f);

            // Adjust liver chestnut strength randomly
            float a = 0.4f;
            int r = horse.getRandom("liver_darkness") >>> 1;
            float r1 = (r % 64) / 64f;
            float r2 = (r / 64 % 64) / 64f;
            if (horse.hasAllele("liver_boost", 1)) {
                r1 = (float)Math.pow(r1, 0.5);
            }
            if (liv == 1) {
                // Make incomplete dominant
                r1 *= 0.5f;
                r2 = 0;
            }
            a *= (0.2f + r1) * (1f + r2);
            color.average(dark, a);
        }
        return color;
    }

    public static TextureLayer getRedBody(HorseGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("base");
        layer.color = redBodyColor(horse);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static Pigment blackFurPigment(HorseGenome horse) {
        float concentration = 15f * getRandomShadeModifier(horse);
        float white = 0.02f;
        if (horse.isDoubleCream() || horse.isHomozygous("ivory", HorseAlleles.IVORY)) {
            concentration *= 0.027f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.033f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.7f;
        }
        else if (horse.isPearl()) {
            concentration *= 0.33f;
            white += 0.18f;
        }

        if (horse.hasAllele("cameo", HorseAlleles.CAMEO)) {
            concentration *= 0.27f;
            white += 0.2f;
        }

        if (horse.hasAllele("silver", HorseAlleles.SILVER)) {
            concentration *= 0.53f;
        }

        if (horse.isHomozygous("dense", 1)) {
            concentration *= 1.1f;
            white -= 0.01f;
        }
 
        white = Math.max(white, 0);
        return new Pigment(EUMELANIN, concentration, white);
    }

    public static Color blackBodyColor(HorseGenome horse) {
        return blackFurPigment(horse).toColor();
    }

    public static TextureLayer getBlackBody(HorseGenome horse) {
        if (horse.isChestnut()) {
            return null;
        }
        TextureLayer layer = new TextureLayer();
        layer.description = "black body";

        if (horse.getMaxAllele("agouti") == HorseAlleles.A_BLACK) {
            layer.name = fixPath("base");
        }
        else if (horse.getMaxAllele("agouti") == HorseAlleles.A_SEAL
                || horse.getMaxAllele("agouti") == HorseAlleles.A_BROWN) {
            layer.name = fixPath("brown");
        }
        else {
            return getSooty(horse);
        }
        layer.color = blackBodyColor(horse);
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static float getRandomShadeModifier(HorseGenome horse) {
        int r = horse.getRandom("shade") >>> 1;
        // Number ranging from -8 to 8
        int x = r % 8 + r / 8 % 8 - 8;
        return 1f + x / 100f;
    }

    public static void addRedManeTail(HorseGenome horse, List<TextureLayer> layers) {
        final float PALOMINO_POWER = 0.2f;
        if (!horse.isChestnut()) {
            return;
        }

        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            TextureLayer palomino_mane = new TextureLayer();
            palomino_mane.description = "palomino mane";
            palomino_mane.name = fixPath("manetail");
            palomino_mane.color = redBodyColor(horse);
            palomino_mane.color.power(PALOMINO_POWER);
            setGrayConcentration(horse, palomino_mane);
            layers.add(palomino_mane);
        }

        if (!horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)
                && !horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)
                && !horse.isMushroom()) {
            // No flaxen, nothing to do
            return;
        }

        TextureLayer flaxen = new TextureLayer();
        flaxen.name = fixPath("flaxen");
        flaxen.description = "flaxen";
        flaxen.color = redBodyColor(horse);
        float power = 1f;
        if (horse.hasAllele("cream", HorseAlleles.CREAM)) {
            power *= PALOMINO_POWER;
        }
        float white = 0f;
        if (horse.isHomozygous("flaxen1", HorseAlleles.FLAXEN)) {
            power *= 0.5f;
            white += 0.2f;
        }
        if (horse.isHomozygous("flaxen2", HorseAlleles.FLAXEN)) {
            power *= 0.8f;
            white += 0.1f;
        }
        if (horse.hasAllele("flaxen_boost", 1)) {
            Math.pow(power, 1.5);
            white *= 1.5;
        }
        if (horse.isMushroom()) {
            power *= 0.5f;
            white += 0.02f;
        }
        flaxen.color.power(power);
        setGrayConcentration(horse, flaxen);
        flaxen.color.addWhite(white);
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
        layer.description = "silver dapple mane";
        Pigment black = blackFurPigment(horse);
        black.concentration *= 0.3f / 15f;
        layer.color = black.toColor();
        setGrayConcentration(horse, layer);
        return layer;
    }

    public static void colorSkin(HorseGenome horse, TextureLayer layer) {
        if (horse.isCreamPearl() || horse.hasAllele("cameo", HorseAlleles.CAMEO)) {
            // Light skin
            layer.color = new Pigment(EUMELANIN, 5f, 0.2f).toColor();
        }
        else if (horse.isDoubleCream() || horse.isHomozygous("ivory", HorseAlleles.IVORY)) {
            // Very light skin
            layer.color = new Pigment(EUMELANIN, 0.1f, 0.1f).toColor();
        }
        else {
            // Black skin
            layer.color = new Pigment(EUMELANIN, 18f, 0.1f).toColor();
        }
        // Multiply by pink
        layer.color.multiply(PINK_SKIN);
    }

    public static void colorGray(HorseGenome horse, TextureLayer layer) {
        // Show skin very faintly through the white hairs
        colorSkin(horse, layer);
        layer.color.addWhite(0.99f);
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
        layer.color.addWhite(0.4f);
        // Multiply by the shell color of hooves
        layer.color.multiply(SHELL_HOOF);
        return layer;
    }

    public static void addDun(HorseGenome horse, List<TextureLayer> layers) {
        if (!horse.hasStripe()) {
            return;
        }
        TextureLayer white = new TextureLayer();
        white.name = fixPath("dun/dun_dilute");
        white.color.a = 0.1f;
        if (!horse.isDun()) {
            white.color.a *= 0.1f;
        }
        if (horse.isHomozygous("light_dun", 1)) {
            white.color.a *= 2f;
        }
        white.type = TextureLayer.Type.SHADE;
        layers.add(white);

        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("dun/dun_dilute");
        layer.type = TextureLayer.Type.ROOT;
        float dunpower = 0.6f;
        if (!horse.isDun()) {
            dunpower = 0.9f;
        }
        layer.color = new Color(dunpower, dunpower, dunpower);
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
                layer.color.a = 0.4f;
                break;
            case 2:
                layer.color.a = 0.8f;
                break;
            case 3:
                layer.color.a = 1f;
                break;
            default:
                layer.color.a = 1f;
        }

        // TODO: replace this with something that actually looks good
        if (horse.hasAllele("donkey_dark", 1) && !horse.isChestnut()) {
            layer.color.a = 1f;
        }

        layer.name = fixPath("sooty_countershade");
        if (horse.isDappleInclined()) {
            layer.name = fixPath("sooty_dapple");
        }
        else if (horse.isChestnut()) {
            layer.name = fixPath("base");
            layer.color.a *= 0.5f;
        }

        layer.color = blackBodyColor(horse);
        setGrayConcentration(horse, layer);

        return layer;
    }

    public static void addMealy(HorseGenome horse, List<TextureLayer> textureLayers)
    {
        // Agouti black hides mealy
        if (!horse.isMealy()) {
            return;
        }

        TextureLayer light_belly = new TextureLayer();
        int spread = 1;
        int color = 0;
        if (horse.hasAllele("mealy1", HorseAlleles.MEALY)) {
            spread += 2;
        }
        if (horse.hasAllele("mealy2", HorseAlleles.MEALY)) {
            color += 1;
        }
        if (horse.isHomozygous("flaxen2", 0)) {
            spread += 1;
        }
        
        String prefix = "";
        TextureLayer other = null;
        if (horse.isHomozygous("light_legs", 1)) {
            // Use version with darker legs
            prefix = "l";
        }
        else if (horse.hasAllele("less_light_legs", 0)) {
            // Set light_belly texture to leave the legs dark and be one 
            // shade darker as a whole, and add a thin layer with light legs
            prefix = "l";
            if (spread > 1) {
                spread -= 1;
                other = new TextureLayer();
                other.name = fixPath("mealy/mealy1");
                other.color = redBodyColor(horse);
                other.color.power(0.04f * (2 - color));
            }
        }

        light_belly.name = fixPath("mealy/" + prefix + "mealy" + spread);
        light_belly.color = redBodyColor(horse);
        light_belly.color.power(0.04f * (2 - color));

        textureLayers.add(light_belly);
        if (other != null) {
            textureLayers.add(other);
        }
    }

    public static void addPoints(HorseGenome horse, List<TextureLayer> layers) {
        TextureLayerGroup points = new TextureLayerGroup();
        // Add dorsal stripe for dun primitive markings
        if (horse.hasStripe()) {
            TextureLayer stripe = new TextureLayer();
            if (horse.hasAllele("cross", 1)) {
                stripe.name = fixPath("dun/cross");
            }
            else {
                stripe.name = fixPath("dun/dorsal");
            }
            points.add(stripe);
        }
        // Add black mane, tail, and legs for bay or bay dun horses and 
        // undiluted mane, tail, and legs for red duns or grullos
        if (horse.hasStripe() || !horse.isChestnut()) {
            TextureLayer legs = new TextureLayer();
            String name = "bay";
            if (horse.hasAllele("reduced_points", 1)) {
                name = "wild_bay";
            }
            legs.name = fixPath(name);
            points.add(legs);

            TextureLayer mane = new TextureLayer();
            mane.name = fixPath("manetail");
            points.add(mane);
        }
        // Set the points to be the right color depending on whether the horse
        // is a red dun or bay/black based
        if (horse.isChestnut()) {
            points.color = redBodyColor(horse);
        }
        else {
            points.color = blackBodyColor(horse);
        }
        points.color.power(1.1f);
        // Ignore this for horses that don't need it
        if (points.layers.size() > 0) {
            layers.add(points);
        }
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
        final int YEAR_TICKS = (int)(HorseConfig.GROWTH.yearLength.get() * 24000);
        final int MAX_AGE = HorseConfig.GROWTH.getMaxAge();
        int age = horse.getAge() + 24000;
        age = Math.min(age, MAX_AGE);
        if (!HorseConfig.GROWTH.grayGradually.get()) {
            // If horses should not gray gradually, treat them as being 8 years old
            age = (int)(MAX_AGE * 0.5f);
        }
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
        double val = 1.1 + Math.pow(1.06, stage) * stage / 50. * stage / 50.;
        return (float)val;
    }

    public static void setGrayConcentration(HorseGenome horse, TextureLayer layer) {
        if (horse.isGray()) {
            // Darken by increasing concentration
            float concentration = grayConcentration(horse, horse.getGrayRate());
            Color dark = new Color(layer.color);
            dark.power(concentration);
            // Darken by averaging with black
            float lightnessDiff = (float)(dark.r + dark.g + dark.b) / (layer.color.r + layer.color.g + layer.color.b);
            layer.color.average(Color.BLACK, 1f - lightnessDiff);
            // Average the two darkened versions
            layer.color.average(dark, 0.5f);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public static TextureLayerGroup getTexturePaths(HorseGenome horse) {
        List<TextureLayer> textureLayers = new ArrayList<TextureLayer>();
        TextureLayer red = HorseColorCalculator.getRedBody(horse);
        textureLayers.add(red);
        addMealy(horse, textureLayers);
        TextureLayer black = HorseColorCalculator.getBlackBody(horse);
        textureLayers.add(black);
        HorseColorCalculator.addDun(horse, textureLayers);
        addPoints(horse, textureLayers);
        HorseColorCalculator.addRedManeTail(horse, textureLayers);
        textureLayers.add(HorseColorCalculator.getBlackManeTail(horse));
        HorseColorCalculator.addGray(horse, textureLayers);
        textureLayers.add(HorseColorCalculator.getNose(horse));
        textureLayers.add(HorseColorCalculator.getHooves(horse));

        if (horse.hasAllele("KIT", HorseAlleles.KIT_ROAN)) {
            TextureLayer roan = new TextureLayer();
            roan.name = HorseColorCalculator.fixPath("roan/roan");
            int r = horse.getRandom("roan_density") >>> 1;
            float a = (50 - (r % 16) - (r / 16 % 16)) / 50f;
            roan.color.a *= a;
            textureLayers.add(roan);
        }

        HorsePatternCalculator.addFaceMarkings(horse, textureLayers);
        if (horse.showsLegMarkings())
        {
            HorsePatternCalculator.addLegMarkings(horse, textureLayers);
        }

        HorsePatternCalculator.addPinto(horse, textureLayers);
        HorsePatternCalculator.addLeopard(horse, textureLayers);

        TextureLayer highlights = new TextureLayer();
        highlights.name = HorseColorCalculator.fixPath("base");
        highlights.type = TextureLayer.Type.HIGHLIGHT;
        highlights.color.a = 0.2f;
        textureLayers.add(highlights);

        TextureLayer shading = new TextureLayer();
        shading.name = HorseColorCalculator.fixPath("shading");
        shading.type = TextureLayer.Type.SHADE;
        shading.color.a = 0.5f;
        textureLayers.add(shading);

        TextureLayer common = new TextureLayer();
        common.name = HorseColorCalculator.fixPath("common");
        textureLayers.add(common);
        return new TextureLayerGroup(textureLayers);
    }
}
