package sekelsta.horse_colors.entity.genetics.breed.horse;

import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

// I'm taking a broad view of "tarpan" here, so these are all the 
// pre-domestic horses more closely related to domestic horses than to takhis.
public class TarpanAsia {
    public static Breed breed;

    static {
        Tarpan.init();
        breed = new Breed(Tarpan.breed);
        breed.name = "tarpan";
        Map<String, List<Float>> COLORS = breed.colors;
    }

    public static void init() {}
}
