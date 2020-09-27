package sekelsta.horse_colors.genetics.breed.horse;

import java.util.List;
import java.util.Map;

import sekelsta.horse_colors.genetics.breed.*;

// Also called the Prezwalski's Wild horse, they are related to the domesticated
// horses of the Botai culture
public class Takhi {
    public static Breed breed = new Breed(BaseHorse.breed);

    static {
        breed.name = "takhi";
        Map<String, List<Float>> COLORS = breed.colors;
    }
}
