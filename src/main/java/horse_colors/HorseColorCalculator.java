package felinoid.horse_colors;

public class HorseColorCalculator
{
    private static final int GRAY_LEG_BITS = 2;
    private static final int FACE_MARKING_BITS = 2;
    public static String getBaseTexture(EntityHorseFelinoid horse)
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

    public static String getSooty(EntityHorseFelinoid horse)
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
        String type = "dappled";
        boolean is_chestnut = horse.isChestnut()
            && !horse.hasAllele("cream", HorseAlleles.CREAM)
            && horse.getPhenotype("liver") != 0;
        if (horse.getPhenotype("dapple") == 0)
        {
            type = is_chestnut? "even" : "countershaded";
        }

        String prefix = is_gray? "gray" : "sooty_" + type;
        return prefix + suffix;
    }

    public static String getMealy(EntityHorseFelinoid horse)
    {
        // TODO
        return null;
    }

    public static String getLegs(EntityHorseFelinoid horse)
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

    public static String getGrayMane(EntityHorseFelinoid horse)
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

    public static String getFaceMarking(EntityHorseFelinoid horse)
    {
        if (horse.getPhenotype("white_suppression") != 0)
        {
            return null;
        }

        if (horse.getPhenotype("draft_sabino") != 0
            || horse.getPhenotype("W20") == 2)
        {
            return "blaze";
        }

        int face_marking = 0;
        // Turn a signed integer into unsigned, also drop a few bits 
        // used elsewhere
        int random = (horse.getHorseVariant("random") << 1) 
                        >>> (1 + GRAY_LEG_BITS);
        if (horse.getPhenotype("tobiano") == 2)
        {
            // 1/4 chance none, 1/2 chance star, 1/8 chance strip, 1/8 for blaze
            if (random % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 2 + ((random >> 2) % 2);
            }
            random >>= 3;
        }
        else if (horse.getPhenotype("tobiano") != 0)
        {
            // 1/4 chance star, 1/8 chance strip, the rest none
            if (random % 4 == 0)
            {
                face_marking += 1 + ((random >> 2) % 2);
            }
            random >>= 3;
        }

        if (horse.getPhenotype("flashy_white") != 0)
        {
            // 1/2 chance blaze, 1/4 chance strip, 1/4 chance star
            if (random % 2 == 0)
            {
                face_marking += 3;
            }
            else
            {
                face_marking += 1 + ((random >> 1) % 2);
            }
            random >>= 2;
        }

        assert horse.getPhenotype("W20") != 2;
        if (horse.getPhenotype("W20") == 1)
        {
            // 1/2 chance strip, 1/4 chance star, 1/8 chance blaze or none
            if (random % 2 == 0)
            {
                face_marking += 2;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 2) % 2 == 0)
            {
                face_marking += 3;
            }
            random >>= 3;
        }

        if (horse.getPhenotype("markings") != 0)
        {
            // 1/2 chance strip, 1/4 chance star, 1/4 chance blaze
            if (random % 2 == 0)
            {
                face_marking += 2;
            }
            else
            {
                face_marking += 1 + 2 * ((random >> 1) % 2);
            }
            random >>= 2;
        }

        if (horse.getPhenotype("half-socks") != 0)
        {
            face_marking += random % 8 == 0? 1 : 0;
            random >>= 3;
        }

        if (horse.getPhenotype("strip") != 0)
        {
            // 1/2 chance strip, 1/4 chance star, 1/8 chance blaze or none
            if (random % 2 == 0)
            {
                face_marking += 2;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 2) % 2 == 0)
            {
                face_marking += 3;
            }
            random >>= 3;
        }
        else if (horse.getPhenotype("star") != 0)
        {
            // 1/4 chance strip, 1/2 chance star, 1/4 chance none
            if (random % 2 == 0)
            {
                face_marking += 1;
            }
            else if ((random >> 1) % 2 == 0)
            {
                face_marking += 2;
            }
            random >>= 2;
        }

        if (horse.getPhenotype("white_boost") != 0)
        {
            // 1/2 chance star, 1/2 chance none
            face_marking += random % 2;
            random >>= 1;
        }
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

    public static String[] getLegMarkings(EntityHorseFelinoid horse)
    {
        // TODO
        return new String[4];
    }

    public static String getPinto(EntityHorseFelinoid horse)
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
        else if (horse.getPhenotype("W20") == 2 
            || horse.getPhenotype("draft_sabino") != 0)
        {
            pinto = "stockings";
        }
        return pinto;
    }

    public static String getLeopard(EntityHorseFelinoid horse)
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
