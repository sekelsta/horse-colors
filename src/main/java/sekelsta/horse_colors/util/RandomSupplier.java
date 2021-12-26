package sekelsta.horse_colors.util;

import java.util.*;

public class RandomSupplier {
    List<String> keys = new ArrayList<>();

    public RandomSupplier(List keys) {
        this.keys = keys;
    }

    public int getVal(String keyRequested, int seed) {
        Random rand = new Random(seed);
        int current = seed;
        for (String key : keys) {
            if (key.equals(keyRequested)) {
                return current;
            }
            current = rand.nextInt();
        }  
        System.err.println("Key not found in RandomSupplier: " + keyRequested);
        return 0; 
    }
}
