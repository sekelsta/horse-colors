package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

// I'm taking a broad view of "tarpan" here, so these are all the 
// pre-domestic horses more closely related to domestic horses than to takhis.
public class TarpanIberia {
    public static Breed breed;

    static {
        Tarpan.init();
        breed = new Breed(Tarpan.breed);
        breed.name = "tarpan";
        Map<String, List<Float>> COLORS = breed.colors;


        COLORS.put("agouti", ImmutableList.of(
            0.6f,       // Black
            0.65f,      // Seal
            0.65f,      // Seal unused
            0.65f,      // Bay unused
            1f,         // Bay
            1f,         // Bay unused
            1f,         // Bay unused
            1f          // Bay unused
        ));
    }

    public static void init() {}
}
