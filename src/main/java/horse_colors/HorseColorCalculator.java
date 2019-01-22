package felinoid.horse_colors;

public class HorseColorCalculator
{
    public static String getBaseTexture(EntityHorseFelinoid horse)
    {
        // First handle double cream dilutes
        if (horse.getPhenotype("cream") == 2)
        {
            // Gray could change the hairs to properly white
            if (horse.getPhenotype("gray") != 0)
            {
                return "white";
            }
            // Not gray, so check if base color is chestnut
            if (horse.getPhenotype("extension") == 0)
            {
                return "cremello";
            }
            // Base color is black or bay. Check for silver.
            if (horse.getPhenotype("silver") != 0)
            {
                switch(horse.getPhenotype("agouti"))
                {
                    case 0: return "silver_smoky_cream";
                    case 1:
                    case 2: return "silver_brown_cream";
                    case 3:
                    case 4: return "silver_perlino";
                }
            }
            // Just a regular double cream. 
            switch(horse.getPhenotype("agouti"))
            {
                case 0: return "smoky_cream";
                case 1:
                case 2: return "brown_cream";
                case 3:
                case 4: return "perlino";
            }
            
            
        }
        // Single cream dilutes
        else if (horse.getPhenotype("cream") == 1)
        {
            // Check for gray
            if (horse.getPhenotype("gray") != 0)
            {
                return "light_gray";
            }
            // Single cream, no gray. Check for dun.
            if (horse.getPhenotype("dun") == 3)
            {
                // Dun + single cream.
                // Check for chestnut before looking for silver.
                if (horse.getPhenotype("extension") == 0)
                {
                    return "dunalino";
                }
                // Black-based, so check for silver.
                if (horse.getPhenotype("silver") != 0)
                {
                    switch(horse.getPhenotype("agouti"))
                    {
                        case 0: return (horse.getPhenotype("liver") == 0? "dark_" : "") + "silver_smoky_grullo";
                        case 1:
                        case 2: return "silver_smoky_brown_dun";
                        case 3:
                        case 4: return "silver_dunskin";
                    }
                }
                switch(horse.getPhenotype("agouti"))
                {
                    case 0: return "smoky_grullo";
                    case 1:
                    case 2: return "smoky_brown_dun";
                    case 3:
                    case 4: return "dunskin";
                }
            }
            // Single cream, no gray, no dun. Check for chestnut base.
            if (horse.getPhenotype("extension") == 0)
            {
                return "palomino";
            }
            // Non-chestnut, so check for silver.
            if (horse.getPhenotype("silver") != 0)
            {
                switch(horse.getPhenotype("agouti"))
                {
                    case 0: return (horse.getPhenotype("liver") == 0? "dark_" : "") + "silver_grullo";
                    case 1:
                    case 2: return "silver_smoky_brown";
                    case 3:
                    case 4: return "silver_buckskin";
                }
            }
            // Single cream, non-chestnut, with nothing else.
            switch(horse.getPhenotype("agouti"))
            {
                case 0: return "smoky_black";
                case 1:
                case 2: return "smoky_brown";
                case 3:
                case 4: return "buckskin";
            }
        }
        // No cream, check for gray
        if (horse.getPhenotype("gray") != 0)
        {
            // TODO: I have more than one gray and need to decide which to use.
            return "light_gray";
        }

        // No cream, no gray. Check for dun.
        if (horse.getPhenotype("dun") == 3)
        {
            // Dun. Check for chestnut.
            if (horse.getPhenotype("extension") == 0)
            {
                // Red dun. Check for liver.
                if (horse.getPhenotype("liver") == 0)
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

            // Dun, non-chestnut. Check for silver.
            if (horse.getPhenotype("silver") != 0)
            {
                switch(horse.getPhenotype("agouti"))
                {
                    case 0: return "silver_grullo";
                    case 1:
                    case 2: return "silver_brown_dun";
                    case 3:
                    case 4: return "silver_dun";
                }
            }

            // Dun, non-chestnut, no other dilutions.
            switch(horse.getPhenotype("agouti"))
            {
                case 0: return "grullo";
                case 1:
                case 2: return "brown_dun";
                case 3:
                case 4: return "dun";
            }
        }

        // No cream, gray, or dun. Check for chestnut.
        if (horse.getPhenotype("extension") == 0)
        {
            String result = "chestnut";
            // So far just chestnut. Check for liver.
            if (horse.getPhenotype("liver") == 0)
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

        // Non-chestnut with no cream, gray, or dun. check for silver.
        if (horse.getPhenotype("silver") != 0)
        {
            switch(horse.getPhenotype("agouti"))
            {
                case 0: return "chocolate";
                case 1:
                case 2: return "silver_brown";
                case 3:
                case 4: return "silver_bay";
            }
        }

        // Non-chestnut with your basic, undiluted, unmodified coat.
        switch(horse.getPhenotype("agouti"))
        {
            case 0: return "black";
            case 1:
            case 2: return "seal_brown";
            case 3:
            case 4: return "bay";
        }

        // This point should not be reached, but java wants a return to compile.
        System.out.println("[horse_colors]: Texture not found for horse with variant "
            + horse.getHorseVariant("0") + ".");
        return "no texture found";
    }

    public static String getSooty(EntityHorseFelinoid horse, String base_color)
    {
        if (horse.getPhenotype("cream") == 2)
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

        String type = "countershaded";
        boolean is_chestnut = horse.getPhenotype("extension") == 0
            && horse.getPhenotype("cream") == 0
            && horse.getPhenotype("liver") != 0;
        if (horse.getPhenotype("dapple") != 0)
        {
            type = is_chestnut? "even" : "dappled";
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
        if ((horse.getPhenotype("extension") == 0 
                && horse.getPhenotype("dun") == 0)
            || horse.getPhenotype("gray") != 0)
        {
            return null;
        }

        String legs = null;
        if (horse.getPhenotype("extension") != 0 
            && (horse.getPhenotype("agouti") >= 3 
                || horse.getPhenotype("dun") != 0))
        {
            if (horse.getPhenotype("cream") == 2)
            {
                legs = "perlino_legs";
            }
            else if (horse.getPhenotype("silver") != 0)
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
            // TODO: red-based dun legs
        }
        return legs;
    }

    public static String getGray(EntityHorseFelinoid horse)
    {
        // TODO
        return null;
    }

    public static String getGrayMane(EntityHorseFelinoid horse, String gray)
    {
        // TODO
        return null;
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
        int random = (horse.getHorseVariant("random") << 1) >>> 1;
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

    public static String getLegMarking(EntityHorseFelinoid horse, int leg)
    {
        // TODO
        return null;
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
