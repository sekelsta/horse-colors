package sekelsta.horse_colors;

public class HorseColorCalculator
{
    private static final int GRAY_LEG_BITS = 2;
    private static final int FACE_MARKING_BITS = 2;
    private static final int LEG_MARKING_BITS = 12;
    public static String getBaseTexture(HorseGeneticEntity horse)
    {
        // Double cream dilute + gray gives white
        if (horse.isHomozygous("cream", HorseAlleles.CREAM)
                && horse.hasAllele("gray", HorseAlleles.GRAY))
        {
            return "white";
        }
        // Other gray
        if (horse.hasAllele("gray", HorseAlleles.GRAY))
        {
            return "gray";
        }

        // Handle double cream dilutes
        if (horse.isHomozygous("cream", HorseAlleles.CREAM))
        {
            if (horse.isChestnut())
            {
                return "cremello";
            }
            // Base color is black or bay
            String base = "";
            switch(horse.getMaxAllele("agouti"))
            {
                case HorseAlleles.A_BLACK: base = "smoky_cream";
                case HorseAlleles.A_SEAL:
                case HorseAlleles.A_BROWN: base = "brown_cream";
                case HorseAlleles.A_BAY_DARK:
                case HorseAlleles.A_BAY:
                case HorseAlleles.A_BAY_LIGHT:
                case HorseAlleles.A_BAY_WILD:
                case HorseAlleles.A_BAY_MEALY: base = "perlino";
            }
            // Check for silver dapple
            if (horse.hasAllele("silver", HorseAlleles.SILVER))
            {
                base = "silver_" + base;
            }
            return base;
            
            
        }

        // Single cream dilutes
        else if (horse.hasAllele("cream", HorseAlleles.CREAM))
        {
            // Single cream, no gray. Check for dun.
            if (horse.hasAllele("dun", HorseAlleles.DUN)
                || horse.hasAllele("dun", HorseAlleles.DUN_IBERIAN))
            {
                // Dun + single cream.
                // Check for chestnut before looking for silver.
                if (horse.isChestnut())
                {
                    return "dunalino";
                }
                // Smoky grullo or dunskin
                String base = "";
                switch(horse.getMaxAllele("agouti"))
                {
                    case HorseAlleles.A_BLACK: base = "smoky_grullo";
                    case HorseAlleles.A_SEAL:
                    case HorseAlleles.A_BROWN: base = "smoky_brown_dun";
                    case HorseAlleles.A_BAY_DARK:
                    case HorseAlleles.A_BAY:
                    case HorseAlleles.A_BAY_LIGHT:
                    case HorseAlleles.A_BAY_WILD:
                    case HorseAlleles.A_BAY_MEALY: base = "dunskin";
                }
                // Check for silver dapple
                if (horse.hasAllele("silver", HorseAlleles.SILVER))
                {
                    return "silver_" + base;
                }
                return base;
            }
            // Single cream, no gray, no dun. Check for chestnut base.
            if (horse.isChestnut())
            {
                return "palomino";
            }
            // Base color is smoky black or buckskin
            String base = "";
            switch(horse.getMaxAllele("agouti"))
            {
                case HorseAlleles.A_BLACK: base = "smoky_black";
                case HorseAlleles.A_SEAL:
                case HorseAlleles.A_BROWN: base = "smoky_brown";
                case HorseAlleles.A_BAY_DARK:
                case HorseAlleles.A_BAY:
                case HorseAlleles.A_BAY_LIGHT:
                case HorseAlleles.A_BAY_WILD:
                case HorseAlleles.A_BAY_MEALY: base = "buckskin";
            }
            // Check for silver dapple
            if (horse.hasAllele("silver", HorseAlleles.SILVER))
            {
                return "silver_" + base;
            }
            return base;
        }

        // No cream, no gray. Check for dun.
        if (horse.hasAllele("dun", HorseAlleles.DUN)
            || horse.hasAllele("dun", HorseAlleles.DUN_IBERIAN))
        {
            // Dun. Check for chestnut.
            if (horse.isChestnut())
            {
                // Red dun. Check for liver.
                if (horse.getMaxAllele("liver") == HorseAlleles.LIVER)
                {
                    // Check for flaxen.
                    switch(horse.getPhenotype("flaxen"))
                    {
                        case 0: return "liver_dun";
                        case 1: return "partly_flaxen_liver_dun";
                        case 2: return "flaxen_liver_dun";
                    }
                }
                // Not liver. Check for flaxen.
                switch(horse.getPhenotype("flaxen"))
                {
                    case 0: return "red_dun";
                    case 1: return "partly_flaxen_dun";
                    case 2: return "flaxen_dun";
                }
            }

            // Dun, non-chestnut.
            String base = "";
            switch(horse.getMaxAllele("agouti"))
            {
                case HorseAlleles.A_BLACK: base = "grullo";
                case HorseAlleles.A_SEAL:
                case HorseAlleles.A_BROWN: base = "brown_dun";
                case HorseAlleles.A_BAY_DARK:
                case HorseAlleles.A_BAY:
                case HorseAlleles.A_BAY_LIGHT:
                case HorseAlleles.A_BAY_WILD:
                case HorseAlleles.A_BAY_MEALY: base = "dun";
            }
            // Check for silver
            if (horse.hasAllele("silver", HorseAlleles.SILVER))
            {
                return "silver_" + base;
            }
            return base;
        }

        // No cream, gray, or dun. Check for chestnut.
        if (horse.isChestnut())
        {
            String result = "chestnut";
            // So far just chestnut. Check for liver.
            if (horse.getMaxAllele("liver") == HorseAlleles.LIVER)
            {
                result = "liver_" + result;
            }
            // Check for flaxen.
            switch(horse.getPhenotype("flaxen"))
            {
                case 1:
                    result = "partly_flaxen_" + result;
                    break;
                case 2:
                    result = "flaxen_" + result;
            }


            return result;
        }

        // Non-chestnut with no cream, gray, or dun. Check for silver.
        if (horse.hasAllele("silver", HorseAlleles.SILVER))
        {
            switch(horse.getMaxAllele("agouti"))
            {
                case HorseAlleles.A_BLACK: return "chocolate";
                case HorseAlleles.A_SEAL:
                case HorseAlleles.A_BROWN: return "silver_brown";
                case HorseAlleles.A_BAY_DARK:
                case HorseAlleles.A_BAY:
                case HorseAlleles.A_BAY_LIGHT:
                case HorseAlleles.A_BAY_WILD:
                case HorseAlleles.A_BAY_MEALY: return "silver_bay";
            }
        }

        // Non-chestnut with your basic, undiluted, unmodified coat.
        switch(horse.getMaxAllele("agouti"))
        {
            case HorseAlleles.A_BLACK: return "black";
            case HorseAlleles.A_SEAL:
            case HorseAlleles.A_BROWN: return "seal_brown";
            case HorseAlleles.A_BAY_DARK:
            case HorseAlleles.A_BAY:
            case HorseAlleles.A_BAY_LIGHT:
            case HorseAlleles.A_BAY_WILD:
            case HorseAlleles.A_BAY_MEALY: return "bay";
        }

        // This point should not be reached, but java wants a return to compile.
        System.out.println("[horse_colors]: Texture not found for horse with variant "
            + horse.getHorseVariant("0") + ".");
        return "no texture found";
    }

    public static String getSooty(HorseGeneticEntity horse)
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

    public static String getMealy(HorseGeneticEntity horse)
    {
        // TODO
        return null;
    }

    public static String getLegs(HorseGeneticEntity horse)
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
        // Don't do anything about chestnut non-dun horses or light gray horses
        if ((horse.isChestnut()
                && horse.getPhenotype("dun") == 0)
            || horse.getPhenotype("gray") != 0)
        {
            return null;
        }

        String legs = null;
        if (!horse.isChestnut()
            && (horse.getMaxAllele("agouti") >= HorseAlleles.A_BROWN 
                || horse.hasAllele("dun", HorseAlleles.DUN) 
                || horse.hasAllele("dun", HorseAlleles.DUN_IBERIAN)))
        {
            if (horse.isHomozygous("cream", HorseAlleles.CREAM))
            {
                legs = "perlino_legs";
            }
            else if (horse.hasAllele("silver", HorseAlleles.SILVER))
            {
                legs = "silver_legs";
            }
            else
            {
                legs = "bay_legs";
            }
        }
        else
        {
            // If I do red dun legs separate, here is where they should go
        }
        return legs;
    }

    public static String getGrayMane(HorseGeneticEntity horse)
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

    public static String getFaceMarking(HorseGeneticEntity horse)
    {
        int white = -2;
        if (horse.getPhenotype("white_suppression") != 0)
        {
            white -= 4;
        }

        white += horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS1);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS2);
        white += 2 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS3);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS4);
        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS5);
        white += 3 * horse.getPhenotype("W20");
        white += 4 * horse.countAlleles("KIT", HorseAlleles.KIT_FLASHY_WHITE);

        white += 6 * horse.countAlleles("MITF", HorseAlleles.MITF_SW1);
        white += 5 * horse.countAlleles("MITF", HorseAlleles.MITF_SW3);
        white += 7 * horse.countAlleles("MITF", HorseAlleles.MITF_SW5);

        white += 5 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW2);
        white += 3 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW4);

        white += 2 * horse.countAlleles("white_star", 1);


        // Anything after here doesn't create face white from scratch, but
        // only increases the size
        if (white <= -2) {
            return null;
        }
        white += horse.countAlleles("KIT", HorseAlleles.KIT_WHITE_BOOST);
        white += horse.countAlleles("white_star", 1);
        white += horse.countAlleles("white_forelegs", 1);
        white += horse.countAlleles("white_hindlegs", 1);

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

    public static String[] getLegMarkings(HorseGeneticEntity horse)
    {
        int white = -2;
        if (horse.getPhenotype("white_suppression") != 0)
        {
            white -= 4;
        }

        white += 3 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS1);
        white += 4 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS2);
        white += 5 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS3);
        white += 6 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS4);
        white += 7 * horse.countAlleles("KIT", HorseAlleles.KIT_MARKINGS5);
        white += 8 * horse.getPhenotype("W20");
        white += 9 * horse.countAlleles("KIT", HorseAlleles.KIT_FLASHY_WHITE);

        white += 2 * horse.countAlleles("MITF", HorseAlleles.MITF_SW1);
        white += 3 * horse.countAlleles("MITF", HorseAlleles.MITF_SW3);
        white += 2 * horse.countAlleles("MITF", HorseAlleles.MITF_SW5);

        white += 2 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW2);
        white += 1 * horse.countAlleles("PAX3", HorseAlleles.PAX3_SW4);

        int forelegs = white;
        forelegs += horse.countAlleles("white_forelegs", 1);

        int hindlegs = white;
        hindlegs += horse.countAlleles("white_hindlegs", 1);

        String[] legs = new String[4];

        // Anything after here doesn't create leg white from scratch, but
        // only increases the size
        int white_boost = 0;
        white_boost += horse.countAlleles("KIT", HorseAlleles.KIT_WHITE_BOOST);
        white_boost += horse.countAlleles("white_star", 1);

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

    public static String getPinto(HorseGeneticEntity horse)
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

    public static String getLeopard(HorseGeneticEntity horse)
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
