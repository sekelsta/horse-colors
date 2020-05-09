package sekelsta.horse_colors.genetics;

public interface IGeneticEntity {
    Genome getGenes();
    int getChromosome(String name);
    void setChromosome(String name, int val);
    java.util.Random getRand();
}
