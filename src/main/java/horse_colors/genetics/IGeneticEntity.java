package sekelsta.horse_colors.genetics;

import sekelsta.horse_colors.item.GeneBookItem;
public interface IGeneticEntity {
    Genome getGenes();
    int getChromosome(String name);
    void setChromosome(String name, int val);
    java.util.Random getRand();
    default GeneBookItem.Species getSpecies() {
        return null;
    }
}
