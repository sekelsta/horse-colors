package sekelsta.horse_colors.entity.genetics;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.AgeableEntity;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.breed.Breed;

public interface IGeneticEntity {
    Genome getGenome();

    String getGeneData();
    void setGeneData(String genes);

    int getSeed();
    void setSeed(int seed);

    Random getRand();

    boolean isMale();
    void setMale(boolean gender);

    int getRebreedTicks();

    int getBirthAge();
    int getAge();

    default float getFractionGrown() {
        int age = getAge();
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
    boolean setPregnantWith(AgeableEntity child, AgeableEntity otherParent);

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

    default List<Breed> getBreeds() {
        return ImmutableList.of(getDefaultBreed());
    }

    default Breed getRandomBreed() {
        int r = getRand().nextInt(getPopulation());
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
