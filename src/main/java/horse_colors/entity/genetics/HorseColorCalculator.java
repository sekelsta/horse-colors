package sekelsta.horse_colors.entity.genetics;
import java.util.*;

import com.google.common.collect.ImmutableList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.client.renderer.TextureLayerGroup;
import sekelsta.horse_colors.entity.genetics.EquineGenome.Gene;
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
    private static final Color BLUE_EYES = new Color(0xc1, 0xda, 0xf8);

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

    private static Pigment redFurPigment(EquineGenome horse) {
        // 5, 0.2 looks haflingerish
        // 5, 0.1 looks medium chestnut
        // 6, 0.1 looks liver chestnutish
        float concentration = 5f * getRandomShadeModifier(horse);
        float white = 0.08f;
        // Set albino donkeys to white
        if (horse.isAlbino()) {
            return new Pigment(Color.WHITE, 0, 0);
        }

        if (horse.isDoubleCream() || horse.isHomozygous(Gene.ivory, HorseAlleles.IVORY)) {
            concentration *= 0.05f;
            white += 0.4f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.1f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.6f;
            white += 0.15f;
            if (horse.hasAllele(Gene.cream, HorseAlleles.MATP_MINOR)) {
                concentration *= 0.6f;
                white += 0.04;
            }
        }
        else if (horse.isPearl()) {
            concentration *= 0.6f;
            white += 0.15f;
            if (horse.hasAllele(Gene.cream, HorseAlleles.MATP_MINOR)) {
                concentration *= 0.9f;
                white += 0.04f;
            }
        }
        else if (horse.isHomozygous(Gene.cream, HorseAlleles.MATP_MINOR)) {
            concentration *= 0.9f;
            white += 0.04f;
        }

        if (horse.isHomozygous(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
            concentration *= 0.15f;
            white += 0.2;
        }
        else if (horse.hasAllele(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
            concentration *= 0.5f;
            white += 0.1;
        }

        if (horse.hasAllele(Gene.cameo, HorseAlleles.CAMEO)) {
            concentration *= 0.3f;
            white += 0.25f;
        }

        if (horse.hasAllele(Gene.rufous, 1)) {
            concentration *= 1.1f;
        }

        if (horse.isHomozygous(Gene.dark_red, 1)) {
            concentration *= 1.2f;
        }

        if (horse.isHomozygous(Gene.dense, 1)) {
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

    private static Color redBodyColor(EquineGenome horse) {
        Color color = redFurPigment(horse).toColor();
        // Treat liver like it leaks some eumelanin into the coat
        int liv = horse.countAlleles(Gene.liver, HorseAlleles.LIVER);
        if (liv > 0) {
            Color dark = blackBodyColor(horse);
            dark.addWhite(0.02f);

            // Adjust liver chestnut strength randomly
            float a = 0.4f;
            int r = horse.getRandom("liver_darkness") >>> 1;
            float r1 = (r % 64) / 64f;
            float r2 = (r / 64 % 64) / 64f;
            if (horse.hasAllele(Gene.liver_boost, 1)) {
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

    private static TextureLayer getRedBody(EquineGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("base");
        layer.color = redBodyColor(horse);
        setGrayConcentration(horse, layer);
        return layer;
    }

    // The starting color of black pigment, for skin, eyes, and fur.
    private static Pigment blackBasePigment(EquineGenome horse) {
        float concentration = 15f * getRandomShadeModifier(horse);
        float white = 0f;
        // Set albino donkeys to white
        if (horse.isAlbino()) {
            return new Pigment(Color.WHITE, 0, 0);
        }

        if (horse.isDoubleCream() || horse.isHomozygous(Gene.ivory, HorseAlleles.IVORY)) {
            concentration *= 0.03f;
        }
        else if (horse.isCreamPearl()) {
            concentration *= 0.04f;
        }
        else if (horse.hasCream()) {
            concentration *= 0.7f;
        }
        else if (horse.isPearl()) {
            concentration *= 0.33f;
            white += 0.09f;
        }

        if (horse.isHomozygous(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
            concentration *= 0.125f;
            white += 0.15;
        }
        else if (horse.hasAllele(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
            concentration *= 0.2f;
            white += 0.1;
        }

        if (horse.hasAllele(Gene.cameo, HorseAlleles.CAMEO)) {
            concentration *= 0.27f;
            white += 0.1f;
        }

        if (horse.hasAllele(Gene.silver, HorseAlleles.SILVER)) {
            concentration *= 0.7f;
        }

        if (horse.isHomozygous(Gene.dense, 1)) {
            concentration *= 1.1f;
            white -= 0.01f;
        }
 
        white = Math.max(white, 0);
        return new Pigment(EUMELANIN, concentration, white);
    }

    private static Pigment blackFurPigment(EquineGenome horse) {
        Pigment pigment = blackBasePigment(horse);
        pigment.white *= 2f;
        pigment.white += 0.02f;
        // Silver dapple has more effect on the fur than on the eyes and skin
        if (horse.hasAllele(Gene.silver, HorseAlleles.SILVER)) {
            pigment.concentration *= 0.75f;
        }
        // This is for pearl's reflective effect
        if (horse.isPearl()) {
            //pigment.white += 0.18f;
        }
        return pigment;
    }

    private static Color blackBodyColor(EquineGenome horse) {
        return blackFurPigment(horse).toColor();
    }

    private static void addBlackBody(EquineGenome horse, TextureLayerGroup layers) {
        if (horse.isChestnut()) {
            return;
        }
        TextureLayer layer = new TextureLayer();

        if (horse.getMaxAllele(Gene.agouti) == HorseAlleles.A_BLACK) {
            layer.name = fixPath("base");
        }
        else if ((horse.getMaxAllele(Gene.agouti) == HorseAlleles.A_SEAL
                || horse.getMaxAllele(Gene.agouti) == HorseAlleles.A_BROWN)
                && !horse.hasAllele(Gene.reduced_points, 1)) {
            layer.name = fixPath("brown");
        }
        else {
            layers.add(getSooty(horse));
            if (horse.species == Species.DONKEY) {
                layer.name = fixPath("donkey_bay");
            }
            else {
                return;
            }
        }
        layer.color = blackBodyColor(horse);
        setGrayConcentration(horse, layer);
        layers.add(layer);
    }

    private static float getRandomShadeModifier(EquineGenome horse) {
        int r = horse.getRandom("shade") >>> 1;
        // Number ranging from -8 to 8
        int x = r % 8 + r / 8 % 8 - 8;
        return 1f + x / 100f;
    }

    private static Pigment redManePigment(EquineGenome horse) {
        float power = 1f;
        float white = 0f;

        if (horse.hasAllele(Gene.cream, HorseAlleles.CREAM)) {
            power *= 0.2f;
        }
        if (horse.hasAllele(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
            power *= 0.2f;
        }
        if (horse.isMushroom()) {
            power *= 0.5f;
            white += 0.02f;
        }
        return new Pigment(redBodyColor(horse), power, white);
    }

    private static Pigment getFlaxenPower(EquineGenome horse) {
        float power = 1f;
        float white = 0f;
        if (horse.isHomozygous(Gene.flaxen1, HorseAlleles.FLAXEN)) {
            power *= 0.5f;
            white += 0.2f;
        }
        if (horse.isHomozygous(Gene.flaxen2, HorseAlleles.FLAXEN)) {
            power *= 0.8f;
            white += 0.1f;
        }
        if (horse.hasAllele(Gene.flaxen_boost, 1)) {
            power = (float)Math.pow(power, 1.5);
            white *= 1.5;
        }
        if (horse.isMealy()) {
            power *= 0.4f;
            white += 0.25f;
        }
        return new Pigment(PHEOMELANIN, power, white);
    }

    private static void addLightManeTail(EquineGenome horse, List<TextureLayer> layers) {
        if (!horse.isChestnut() && !horse.isFrostedDun()) {
            return;
        }

        if (!horse.isHomozygous(Gene.flaxen1, HorseAlleles.FLAXEN)
                && !horse.isHomozygous(Gene.flaxen2, HorseAlleles.FLAXEN)
                && !horse.hasAllele(Gene.cream, HorseAlleles.CREAM)
                && !horse.hasAllele(Gene.cream, HorseAlleles.CHAMPAGNE)
                && !horse.isMushroom()
                && !horse.isFrostedDun()
                && !horse.isMealy()) {
            // No flaxen, nothing to do
            return;
        }

        TextureLayer flaxen = new TextureLayer();
        flaxen.name = fixPath("manetail");

        Pigment flaxenPower = getFlaxenPower(horse);
        if (horse.isFrostedDun()) {
            flaxenPower.white = Math.max(flaxenPower.white, 0.6f);
            flaxenPower.concentration = Math.min(flaxenPower.concentration, 0.5f);
        }
        Pigment maneColor = horse.isChestnut()? redManePigment(horse) : blackManePigment(horse);
        maneColor.concentration *= flaxenPower.concentration;
        maneColor.white += flaxenPower.white;


        flaxen.color = maneColor.toColor();
        setGrayConcentration(horse, flaxen);
        layers.add(flaxen);

    }

    // Used for hoof and nose color of most horses. Champagne horses use 
    // blackBasePigment directly
    private static void colorSkin(EquineGenome horse, TextureLayer layer) {
        Pigment pigment = blackBasePigment(horse);
        pigment.concentration *= 1.2f;
        layer.color = pigment.toColor();
        // Multiply by pink
        layer.color.multiply(PINK_SKIN);
    }

    private static float blueEyeShade(EquineGenome horse) {
        int shade = 0;
        shade += 3 * horse.countAlleles(Gene.blue_eye_shade1, 1);
        shade += 2 * (2 - horse.countAlleles(Gene.blue_eye_shade2, 1));
        shade += 1 * (2 - horse.countAlleles(Gene.blue_eye_shade3, 1));
        return 0.34f + (2.56f / 12f) * shade;
    }

    private static void colorGray(EquineGenome horse, TextureLayer layer) {
        // Show skin very faintly through the white hairs
        colorSkin(horse, layer);
        layer.color.addWhite(0.99f);
    }

    private static void addNose(EquineGenome horse, TextureLayerGroup layerGroup) {
        TextureLayer noseBase = new TextureLayer();
        noseBase.name = fixPath("nose");
        // For champagne horses, make the main nose texture pink and add darker
        // freckles
        if (horse.hasAllele(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
            Pigment frecklePigment = blackBasePigment(horse);
            TextureLayer freckles = new TextureLayer();
            freckles.name = fixPath("freckles");
            freckles.type = TextureLayer.Type.NO_ALPHA;
            frecklePigment.concentration *= 3f;
            freckles.color = frecklePigment.toColor();
            freckles.color.multiply(PINK_SKIN);

            Pigment black = blackBasePigment(horse);
            black.concentration *= 0.2f;
            black.white *= 0.1f;
            noseBase.color = black.toColor();
            noseBase.color.multiply(PINK_SKIN);

            TextureLayerGroup group = new TextureLayerGroup();
            group.add(noseBase);
            group.add(freckles);
            layerGroup.add(group);
        }
        else {
            colorSkin(horse, noseBase);
            layerGroup.add(noseBase);
        }
    }

    private static TextureLayer getHooves(EquineGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("hooves");
        colorSkin(horse, layer);
        layer.color.addWhite(0.4f);
        // Multiply by the shell color of hooves
        layer.color.multiply(SHELL_HOOF);
        return layer;
    }

    private static TextureLayer getEyes(EquineGenome horse) {
        TextureLayer layer = new TextureLayer();
        layer.name = fixPath("iris");
        // Blue background color
        Pigment blue = new Pigment(BLUE_EYES, blueEyeShade(horse), 0f);
        if (horse.isHomozygous(Gene.MITF, HorseAlleles.MITF_SW1)) {
            // Unpigmented blue eyes
            layer.color = blue.toColor();
        }
        else {
            // Pigmented eyes
            Pigment pigment = blackBasePigment(horse);
            // Champagne lightens the fur more than the eyes
            if (horse.hasAllele(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
                pigment.concentration *= 1.5f;
            }
            // Pearl too
            if (horse.hasAllele(Gene.cream, HorseAlleles.PEARL)) {
                pigment.concentration *= 2f;
            }
            // Tiger eye
            if (horse.isHomozygous(Gene.tiger_eye, HorseAlleles.TIGER_EYE)) {
                pigment.concentration *= 0.25f;
                // Cream and tiger eye appear to interact in the mare 
                // Plantera Dorada
                if (horse.hasAllele(Gene.cream, HorseAlleles.CREAM)) {
                    pigment.concentration *= 0.2f;
                }
                else {
                    // To make eyes more orange/yellow
                    blue.concentration *= 0.25f;
                }
            }
            // Minor shade genes
            if (horse.isHomozygous(Gene.brown_eye_shade1, 1)) {
                pigment.concentration *= 0.8f;
            }
            for (int i = 0; i < horse.countAlleles(Gene.brown_eye_shade2, 1); ++i) {
                pigment.concentration *= 0.9f;
            }
            if (horse.isHomozygous(Gene.brown_eye_shade3, 1)) {
                pigment.concentration *= 1.1f;
            }

            pigment.concentration *= 0.5f;
            pigment.white *= 0.2f;
            // Adjust so pigmented eyes have less blue to them
            blue.concentration = Math.max(0f, blue.concentration - 0.5f * pigment.concentration);
            layer.color = pigment.toColor();
            layer.color.multiply(blue.toColor());
        }
        return layer;
    }

    private static void addDun(EquineGenome horse, List<TextureLayer> layers) {
        if (!horse.hasStripe()) {
            return;
        }
        TextureLayer white = new TextureLayer();
        white.name = fixPath("dun/dun_dilute");
        white.color.a = 0.2f;
        if (!horse.isDun()) {
            white.color.a *= 0.1f;
        }
        if (horse.isHomozygous(Gene.light_dun, 1)) {
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

    private static TextureLayer getSooty(EquineGenome horse)
    {
        TextureLayer layer = new TextureLayer();

        // Set the color before changing its alpha
        layer.color = blackBodyColor(horse);
        setGrayConcentration(horse, layer);

        int sooty_level = horse.getSootyLevel();
        switch (sooty_level) {
            case 0:
                return null;
            case 1:
                layer.color.a = 0.2f;
                break;
            case 2:
                layer.color.a = 0.5f;
                break;
            case 3:
                layer.color.a = 0.8f;
                break;
            case 4:
                layer.color.a = 1f;
                break;
            default:
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

        return layer;
    }

    private static void addMealy(EquineGenome horse, List<TextureLayer> textureLayers)
    {
        // Agouti black hides mealy
        if (!horse.isMealy()) {
            return;
        }

        TextureLayer light_belly = new TextureLayer();
        int spread = 1;
        int color = 0;
        if (horse.hasAllele(Gene.mealy1, HorseAlleles.MEALY)) {
            spread += 2;
        }
        if (horse.hasAllele(Gene.mealy2, HorseAlleles.MEALY)) {
            color += 1;
        }
        if (horse.isHomozygous(Gene.flaxen2, 0)) {
            spread += 1;
        }
        
        String prefix = "";
        TextureLayer other = null;
        if (horse.isHomozygous(Gene.light_legs, 1)) {
            // Use version with darker legs
            prefix = "l";
        }
        else if (horse.hasAllele(Gene.less_light_legs, 0)) {
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

    private static Pigment blackManePigment(EquineGenome horse) {
        Pigment pigment = blackFurPigment(horse);

        if (horse.hasAllele(Gene.silver, HorseAlleles.SILVER)) {
            pigment.concentration *= 0.02f;
        }
        return pigment;
    }

    private static void addPoints(EquineGenome horse, List<TextureLayer> layers) {
        TextureLayerGroup points = new TextureLayerGroup();
        // Add dorsal stripe for dun primitive markings
        if (horse.hasStripe()) {
            TextureLayer stripe = new TextureLayer();
            stripe.name = fixPath("dun/dorsal");
            points.add(stripe);
            if (horse.hasAllele(Gene.cross, 1)) {
                TextureLayer cross = new TextureLayer();
                cross.name = fixPath("dun/cross");
                points.add(cross);
            }
        }
        // Add black mane, tail, and legs for bay or bay dun horses and 
        // undiluted mane, tail, and legs for red duns or grullos
        if (horse.hasStripe() || !horse.isChestnut()) {
            TextureLayer legs = new TextureLayer();
            String name = "bay";
            if (horse.hasAllele(Gene.reduced_points, 1)) {
                name = "wild_bay";
            }
            if (horse.species == Species.DONKEY) {
                name = "wild_bay";
            }
            legs.name = fixPath(name);
            points.add(legs);

            TextureLayer mane = new TextureLayer();
            if (horse.hasNarrowStripe()) {
                mane.name = fixPath("dun/dunmanetail");
                if (!horse.isChestnut()) {
                    TextureLayer layer = new TextureLayer();
                    layer.name = fixPath("manetail");
                    layer.color = blackManePigment(horse).toColor();
                    setGrayConcentration(horse, layer);
                    layers.add(layer);
                }
            }
            else {
                mane.name = fixPath("manetail");
            }

            if (horse.isChestnut()) {
                mane.color = redManePigment(horse).toColor();
            }
            else {
                mane.color = blackManePigment(horse).toColor();
            }
            mane.color.power(1.1f);
            setGrayConcentration(horse, mane);
            layers.add(mane);
        }
        // Set the points to be the right color depending on whether the horse
        // is a red dun or bay/black based
        if (horse.isChestnut()) {
            points.color = redBodyColor(horse);
            points.color.power(1.1f);
        }
        else {
            Pigment pigment = blackFurPigment(horse);
            // Adjust champagnes to have darker points
            if (horse.hasAllele(Gene.champagne, HorseAlleles.CHAMPAGNE)) {
                pigment.concentration *= 1.2f;
                pigment.white *= 0.5;
            }
            points.color = pigment.toColor();
            points.color.power(1.2f);
        }
        // Ignore this for horses that don't need it
        if (points.layers.size() > 0) {
            layers.add(points);
        }
        setGrayConcentration(horse, points);
    }

    private static void addGray(EquineGenome horse, List<TextureLayer> layers) {
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
                mane.name = fixPath("fullmanetail");
            }
            else {
                mane.name = fixPath("gray/mane" + mane_stage);
            }
            colorGray(horse, mane);
            layers.add(mane);
        }
    }

    // num_stages does not count the starting and ending stages
    private static int grayStage(EquineGenome horse, float rate, int num_stages, float delay) {
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

    private static float grayConcentration(EquineGenome horse, float rate) {
        int stage = grayStage(horse, rate, 50, 0f);
        double val = 1.1 + Math.pow(1.06, stage) * stage / 50. * stage / 50.;
        return (float)val;
    }

    private static void setGrayConcentration(EquineGenome horse, TextureLayer layer) {
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
    public static TextureLayerGroup getTexturePaths(EquineGenome horse) {
        List<TextureLayer> textureLayers = new ArrayList<TextureLayer>();
        TextureLayerGroup layerGroup = new TextureLayerGroup(textureLayers);
        TextureLayer red = getRedBody(horse);
        textureLayers.add(red);
        addMealy(horse, textureLayers);
        addBlackBody(horse, layerGroup);
        addLightManeTail(horse, textureLayers);
        addDun(horse, textureLayers);
        addPoints(horse, textureLayers);
        addGray(horse, textureLayers);
        addNose(horse, layerGroup);
        textureLayers.add(getHooves(horse));

        // Add roan
        if (horse.hasAllele(Gene.KIT, HorseAlleles.KIT_ROAN)) {
            TextureLayer roan = new TextureLayer();
            roan.name = fixPath("roan/roan");
            int r = horse.getRandom("roan_density") >>> 1;
            float a = (50 - (r % 16) - (r / 16 % 16)) / 50f;
            roan.color.a *= a;
            textureLayers.add(roan);
        }

        // Add rabicano
        if (horse.hasAllele(Gene.rabicano, 1)) {
            TextureLayer rabicano = new TextureLayer();
            rabicano.name = fixPath("roan/rabicano");
            textureLayers.add(rabicano);
        }

        HorsePatternCalculator.addFaceMarkings(horse, textureLayers);
        if (horse.showsLegMarkings())
        {
            HorsePatternCalculator.addLegMarkings(horse, textureLayers);
        }

        HorsePatternCalculator.addPinto(horse, textureLayers);
        HorsePatternCalculator.addLeopard(horse, textureLayers);

        textureLayers.add(HorseColorCalculator.getEyes(horse));

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
        return layerGroup;
    }
}
