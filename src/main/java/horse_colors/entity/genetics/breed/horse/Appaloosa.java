package sekelsta.horse_colors.entity.genetics.breed.horse;

import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.entity.genetics.breed.*;

public class Appaloosa {
    public static Breed breed = new Breed(DefaultHorse.breed);

    static {
        breed.name = "appaloosa";
        Map<String, List<Float>> APPALOOSA = breed.colors;
        APPALOOSA.put("leopard", ImmutableList.of(
            0.5f,     // Non-leopard
            1f        // Leopard
        ));
        APPALOOSA.put("PATN1", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        APPALOOSA.put("PATN2", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
        APPALOOSA.put("PATN3", ImmutableList.of(
            0.5f,  // Non-PATN
            1f          // PATN
        ));
    }

    public static void init() {}
}
