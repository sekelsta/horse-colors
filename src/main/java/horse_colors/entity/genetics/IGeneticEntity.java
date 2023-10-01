package sekelsta.horse_colors.entity.genetics;

import java.util.Collection;
import java.util.Random;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import sekelsta.horse_colors.breed.Breed;
import sekelsta.horse_colors.config.HorseConfig;

public interface IGeneticEntity<T extends Enum<T>> {
    Genome getGenome();

    String getGeneData();
    void setGeneData(String genes);

    int getSeed();
    void setSeed(int seed);

    RandomSource getRand();

    boolean isMale();
    void setMale(boolean gender);

    boolean isFertile();
    void setFertile(boolean fertile);

    int getRebreedTicks();

    int getBirthAge();
    int getTrueAge();

    default float getFractionGrown() {
        int age = getTrueAge();
        if (age < 0) {
            if (HorseConfig.GROWTH.growGradually.get()) {
                int minAge = getBirthAge();
                float fractionGrown = (minAge - age) / (float)minAge;
                return Math.max(0, fractionGrown);
            }
            return 0;
        }
        return 1;
    }

    // Return true if successful, false otherwise
    // Reasons for returning false could be if the animal is male or the mate is female
    // (This prevents spawn eggs from starting a pregnancy.)
    boolean setPregnantWith(AgeableMob child, AgeableMob otherParent);

    default Breed getDefaultBreed() {
        return new Breed();
    }

    default int getPopulation() {
        int count = 0;
        for (Breed breed : getBreeds()) {
            count += breed.population;
        }
        return count;
    }

    Collection<Breed<T>> getBreeds();

    default Breed getRandomBreed() {
        int r = getRand().nextInt(Math.max(1, getPopulation()));
        int count = 0;
        for (Breed breed : getBreeds()) {
            count += breed.population;
            if (r < count) {
                return breed;
            }
        }
        return getDefaultBreed();
    }

    default Breed getBreed(String name) {
        for (Breed breed : getBreeds()) {
            if (name.equals(breed.name)) {
                return breed;
            }
        }
        return null;
    }

    float getMotherSize();
    void setMotherSize(float size);
}
