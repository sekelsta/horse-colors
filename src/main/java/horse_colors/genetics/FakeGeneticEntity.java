package sekelsta.horse_colors.genetics;

import java.util.HashMap;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class FakeGeneticEntity implements IGeneticEntity {
    private Genome genome; 
    private HashMap<String, Integer> map;

    public FakeGeneticEntity() {
         map = new HashMap<String, Integer>();
    }

    public Genome getGenes() {
        return genome;
    }

    public int getChromosome(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return 0;
    }

    public void setChromosome(String name, int val) {
        map.put(name, val);
    }

    public java.util.Random getRand() {
        return new java.util.Random();
    }
}
