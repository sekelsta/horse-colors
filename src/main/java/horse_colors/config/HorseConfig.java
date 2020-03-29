package sekelsta.horse_colors.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

import java.util.List;
import java.util.*;

import net.minecraftforge.common.BiomeDictionary;

public class HorseConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Common COMMON = new Common(BUILDER);
    public static final Spawn SPAWN = new Spawn(BUILDER);

    public static class Common {
        public static BooleanValue useGeneticStats;
        public static BooleanValue enableHealthEffects;
        public static BooleanValue horseDebugInfo;
        public static DoubleValue mutationChance;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Common config settings")
                    .push("common");

            useGeneticStats = builder
                    .comment("If enabled, horses' speed, jump, and health will be determined",
            "through genetics instead of the default Minecraft way")
                    .translation("horse_colors.config.common.useGeneticStats")
                    .define("useGeneticStats", false);

            enableHealthEffects = builder
                    .comment("If enabled, certain genes will have a small impact on health,",
                             "as they do in real life. This does not prevent Overo Lethal",
                             "White Syndrome.")
                    .translation("horse_colors.config.common.enableHealthEffects")
                    .define("enableHealthEffects", true);

            horseDebugInfo = builder
                    .comment("If enabled, debugging information will appear on the screen when the",
            "player is holding a stick in their left hand and looks at a horse.",
            "For most users, it is probably better to leave this as false.")
                    .translation("horse_colors.config.common.horseDebugInfo")
                    .define("horseDebugInfo", false);

            mutationChance = builder
                    .comment("The chance for each allele to mutate.",
                             "There are at least 80 genes, each with 2 alleles, and each mutation",
                             "has about half a chance of having no effect, so with the default",
                             "value of 0.0005, each foal has a 5% chance of having at least",
                             "one mutation. For mutationChance = 0.001, that chance becomes about 7%,", 
                              "and for mutationChance = 0.01 a foal has about a 45% chance of having at", 
                              "least one mutation. Any higher is not recommended.",
                              "To disable mutations, set this value to 0.")
                    .defineInRange("mutationChance", 0.0005, 0.0, 1.0);

            builder.pop();
        }
    }

    public static class Spawn {
        public static BooleanValue blockVanillaHorseSpawns;
        public static IntValue minHerdSize;
        public static IntValue maxHerdSize;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> spawnBiomeWeights;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> excludeBiomes;
        public static class BiomeWeight {
            BiomeWeight(String b, int w) {
                this.biome = b;
                this.weight = w;
            }

            public BiomeWeight(String s) {
                
                s = s.trim();
                int comma = s.indexOf(",");
                String rawBiome = s.substring(0, comma);
                this.biome = rawBiome.trim();
                String rawWeight = s.substring(comma + 1, s.length());
                this.weight = Integer.parseInt(rawWeight.trim());
            }
            public int weight;
            public String biome;

            public String toString() {
                return biome + ", " + String.valueOf(weight);
            }

            public static boolean isValid(Object o) {
                if (!(o instanceof String)) {
                    return false;
                }
                // Check basic format
                String s = (String)o;
                s = s.trim();
                if (s.length() < 3) {
                    System.out.println(s + " not valid: Too short");
                    return false;
                }
                int comma = s.indexOf(",");
                if (comma == -1) {
                    System.out.println(s + " not valid: No comma");
                    return false;
                }
                String rawBiome = s.substring(0, comma);
                // Check that the int is an int
                String rawWeight = s.substring(comma + 1, s.length());
                rawWeight = rawWeight.trim();
                try {
                    int weight = Integer.parseInt(rawWeight);
                    if (weight < 0) {
                        System.out.println(s + " not valid: Negative weight" + rawWeight);
                        return false;
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println(s + " not valid: Unparseable weight" + rawWeight);
                    return false;
                }
                // Check that the biome is a biome
                if (getType(rawBiome) == null) {
                    System.out.println(s + "not valid: " + rawBiome + " is not a biome type. Make sure you enter a biome type, not a biome.");
                }
                return getType(rawBiome) != null;
            }

            public static BiomeDictionary.Type getType(String s) {
                for (BiomeDictionary.Type biome : BiomeDictionary.Type.getAll()) {
                    if (s.compareToIgnoreCase(biome.toString()) == 0) {
                        return biome;
                    }
                }
                return null;
            }
        }

        Spawn(final ForgeConfigSpec.Builder builder) {
            builder.comment("Horse spawning settings")
                    .push("spawn");

            blockVanillaHorseSpawns = builder
                    .comment("If set to true, only horses created by this mod will spawn.",
            "This mainly affects newly generated areas.")
                    .translation("horse_colors.config.spawn.blockVanillaHorseSpawns")
                    .define("blockVanillaHorseSpawns", true);

            minHerdSize = builder
                    .comment("What size groups horses will spawn in")
                    .translation("horse_colors.config.spawn.minHerdSize")
                    .defineInRange("minHerdSize", 4, 0, Integer.MAX_VALUE);

            maxHerdSize = builder
                    .comment("")
                    .translation("horse_colors.config.spawn.maxHerdSize")
                    .defineInRange("maxHerdSize", 8, 0, Integer.MAX_VALUE);

            spawnBiomeWeights = builder
                    .comment("A list of biomes horses can spawn in, and how often they spawn there.")
                    .<String>defineList("spawnBiomeWeights", 
                                Arrays.asList((new BiomeWeight(BiomeDictionary.Type.PLAINS.toString(), 10)).toString(), 
                                              (new BiomeWeight(BiomeDictionary.Type.SAVANNA.toString(), 10)).toString()), 
                                BiomeWeight::isValid);

            excludeBiomes = builder
                    .comment("A list of biome types that should not spawn horses.",
                             "For instance, set to [\"MOUNTAIN\", \"HILLS\", \"PLATEAU\"]",
                             "to prevent horses from spawning in non-flat biomes.")
                    .<String>defineList("excludeBiomes", 
                                Arrays.asList(),
                                // Lambda function to check if the biome is valid
                                o -> BiomeDictionary.Type.getAll().contains(BiomeWeight.getType(String.valueOf(o))));

            builder.pop();
        }
    }


    public static final ForgeConfigSpec spec = BUILDER.build();
}
