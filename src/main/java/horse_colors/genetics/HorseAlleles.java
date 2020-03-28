package sekelsta.horse_colors.genetics;

public class HorseAlleles
{
    // In general more recessive alleles are given lower numbers and more
    // dominant ones are given higher numbers

    // Extension (MC1R)
    // There are 3 extra reds and 3 extra blacks. 
    // This is in case new alleles are found, or very
    // closely linked genes are discovered.
    public static final int E_RED = 0;
    public static final int E_RED2 = 1;
    public static final int E_RED3 = 2;
    public static final int E_RED4 = 3;
    public static final int E_BLACK3 = 4;
    public static final int E_BLACK2 = 5;
    public static final int E_BLACK1 = 6;
    public static final int E_BLACK = 7;

    // Agouti (ASIP)
    public static final int A_BLACK = 0;
    public static final int A_SEAL = 1;
    public static final int A_BROWN = 2;
    public static final int A_BAY_DARK = 3;
    public static final int A_BAY = 4;
    public static final int A_BAY_LIGHT = 5;
    public static final int A_BAY_WILD = 6;
    public static final int A_BAY_MEALY = 7;

    // Dun (TBX3)
    public static final int NONDUN2 = 0;
    public static final int NONDUN1 = 1;
    public static final int DUN = 2;
    public static final int DUN_UNUSED = 3;

    // Gray (STX17)
    public static final int NONGRAY = 0;
    public static final int GRAY = 1;

    // Cream/pearl (MATP)
    public static final int NONCREAM = 0;
    public static final int NONCREAM2 = 1;
    public static final int PEARL = 2;
    public static final int CREAM = 3;

    // Silver
    public static final int NONSILVER = 0;
    public static final int SILVER = 1;

    // Liver
    public static final int LIVER = 0;
    public static final int NONLILVER = 1;

    // Flaxen (for all flaxen genes)
    public static final int FLAXEN = 0;
    public static final int NONFLAXEN = 1;

    // Sooty (for all sooty genes)
    public static final int NONSOOTY = 0;
    public static final int SOOTY = 1;

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
    // In case I add rabbicano, which I'd probably put on KIT
    // unless the actual location is found
    public static final int KIT_RESERVED_RABICANO = 8;
    public static final int KIT_FLASHY_WHITE = 9;
    public static final int KIT_UNUSED = 10;
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
    public static final int PAX3_UNUSED = 3;
  /*  TODO
        "dapple",  
        "white_suppression", 
        "gray_suppression",
        "gray_mane", 
        "slow_gray1", 
        "slow_gray2"*/
};
