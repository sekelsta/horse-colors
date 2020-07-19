package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.genetics.Species;
public interface IGeneticEntity {
    Genome getGenes();
    int getChromosome(String name);
    void setChromosome(String name, int val);
    java.util.Random getRand();

    default Species getSpecies() {
        return null;
    }

    boolean isMale();

    void setMale(boolean gender);

    int getRebreedTicks();

    int getBirthAge();
}
