package sekelsta.horse_colors.entity.genetics;

public class HorseAlleles
{
    // In earlier genes more recessive alleles are given lower numbers and more
    // dominant ones are given higher numbers.
    // In later genes the most common allele is given 0

    // Extension (MC1R)
    public static final int E_RED = 0;
    public static final int E_BLACK = 1;

    // Agouti (ASIP)
    public static final int A_BLACK = 0;
    public static final int A_SEAL = 1;
    public static final int A_BROWN = 2;
    public static final int A_BAY_DARK = 3;
    public static final int A_BAY = 4;

    // Dun (TBX3)
    public static final int NONDUN2 = 0;
    public static final int NONDUN1 = 1;
    public static final int DUN = 2;
    public static final int DUN_OTHER = 3;

    // Gray (STX17)
    public static final int NONGRAY = 0;
    public static final int GRAY = 1;

    // Cream/pearl (MATP)
    public static final int NONCREAM = 0;
    public static final int SNOWDROP = 1;
    public static final int PEARL = 2;
    public static final int CREAM = 3;
    public static final int MATP_MINOR = 4;

    // Silver
    public static final int NONSILVER = 0;
    public static final int SILVER = 1;

    // Liver
    public static final int LIVER = 0;
    public static final int NONLILVER = 1;

    // Flaxen (for all flaxen genes)
    public static final int FLAXEN = 0;
    public static final int NONFLAXEN = 1;

    // Mealy (for all mealy genes)
    public static final int NONMEALY = 0;
    public static final int MEALY = 1;

    // KIT
    public static final int KIT_WILDTYPE = 0;
    public static final int KIT_WHITE_BOOST = 1;
    public static final int KIT_MARKINGS1 = 2;
    public static final int KIT_MARKINGS2 = 3;
    public static final int KIT_MARKINGS3 = 4;
    public static final int KIT_MARKINGS4 = 5;
    public static final int KIT_MARKINGS5 = 6;
    public static final int KIT_W20 = 7;
    public static final int KIT_UNUSED = 8;
    public static final int KIT_FLASHY_WHITE = 9;
    public static final int KIT_UNUSED1 = 10;
    public static final int KIT_TOBIANO = 11;
    public static final int KIT_SABINO1 = 12;
    public static final int KIT_TOBIANO_W20 = 13;
    public static final int KIT_ROAN = 14;
    public static final int KIT_DOMINANT_WHITE = 15;

    // Leopard complex
    public static final int NONLEOPARD = 0;
    public static final int LEOPARD = 1;

    // All PATN genes
    public static final int NONPATN = 0;
    public static final int PATN = 1;

    // Frame overo
    public static final int NONFRAME = 0;
    public static final int FRAME = 1;

    // Splashed white
    public static final int MITF_SW1 = 0;
    public static final int MITF_SW3 = 1;
    public static final int MITF_SW5 = 2;
    public static final int MITF_WILDTYPE = 3;

    public static final int PAX3_WILDTYPE = 0;
    public static final int PAX3_SW2 = 1;
    public static final int PAX3_SW4 = 2;

    // Champagne
    public static final int NONCHAMPAGNE = 0;
    public static final int CHAMPAGNE = 1;

    // Tiger eye
    public static final int TIGER_EYE = 1;

    // Cameo (donkey)
    public static final int NONCAMEO = 0;
    public static final int CAMEO = 1;

    // Ivory (donkey)
    public static final int NONIVORY = 0;
    public static final int IVORY = 1;

    // Donkey dun
    public static final int DONKEY_DUN = 0;
    public static final int DONKEY_NONDUN_CROSS = 1;
    public static final int DONKEY_NONDUN = 2;

    public static boolean isTobianoAllele(int allele) {
        return allele == KIT_TOBIANO || allele == KIT_TOBIANO_W20;
    }
};
