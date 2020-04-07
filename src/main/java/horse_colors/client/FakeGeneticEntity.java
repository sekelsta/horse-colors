package sekelsta.horse_colors.client;

import java.util.HashMap;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class FakeGeneticEntity implements IGeneticEntity {
    private Genome genome; 
    private HashMap<String, Integer> map = new HashMap<String, Integer>();

    public Genome getGenes() {
        return genome;
    }

    public int getChromosome(String name) {
        return map.get(name);
    }

    public void setChromosome(String name, int val) {
        map.put(name, val);
    }

    public java.util.Random getRand() {
        return new java.util.Random();
    }
}
