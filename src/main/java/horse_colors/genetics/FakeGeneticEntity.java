package sekelsta.horse_colors.genetics;

import java.util.HashMap;
import net.minecraft.entity.AgeableEntity;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class FakeGeneticEntity implements IGeneticEntity {
    private Genome genome; 
    private HashMap<String, Integer> map;
    private boolean gender;

    public FakeGeneticEntity() {
         map = new HashMap<String, Integer>();
    }

    @Override
    public Genome getGenes() {
        return genome;
    }

    @Override
    public int getChromosome(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return 0;
    }

    @Override
    public void setChromosome(String name, int val) {
        map.put(name, val);
    }

    @Override
    public java.util.Random getRand() {
        return new java.util.Random();
    }

    @Override
    public boolean isMale() {
        return gender;
    }

    @Override
    public void setMale(boolean gender) {
        this.gender = gender;
    }

    @Override
    public int getRebreedTicks() {
        return 0;
    }

    @Override
    public int getBirthAge() {
        return 0;
    }

    @Override
    public boolean setPregnantWith(AgeableEntity child, AgeableEntity otherParent) {
        return false;
    }
}
