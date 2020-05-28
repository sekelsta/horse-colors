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
    public static final Genetics GENETICS = new Genetics(BUILDER);

    public static class Common {
        public static BooleanValue horseDebugInfo;
        public static BooleanValue enableGroundTie;
        public static IntValue yearLength;
        public static DoubleValue maxAge;
        public static BooleanValue autoEquipSaddle;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Common config settings")
                    .push("common");

            enableGroundTie = builder
                    .comment("If enabled, horses will not wander off if they are wearing a saddle.")
                    .translation("horse_colors.config.common.enableGroundTie")
                    .define("enableGroundTie", false);

            horseDebugInfo = builder
                    .comment("If enabled, debugging information will appear on the screen when the",
            "player is holding a stick in their left hand and looks at a horse.",
            "For most users, it is probably better to leave this as false.")
                    .translation("horse_colors.config.common.horseDebugInfo")
                    .define("horseDebugInfo", false);

            yearLength = builder
                    .comment("How long a year lasts in ticks, for the purposes of graying.",
                            "The default 48000 ticks is two minecraft days.")
                    .defineInRange("yearLength", 48000, 1, Integer.MAX_VALUE / 1024);

            maxAge = builder
                    .comment("How many years a horse will age, for the purposes of graying.")
                    .defineInRange("maxAge", 15.0, 0.0, 25.0);

            autoEquipSaddle = builder
                    .comment("If enabled, right clicking a horse while holding a saddle or horse armor", 
                             "will equip it (as long as the horse isn't already wearing something in that slot)",
                             "instead of opening the inventory.")
                    .define("autoEquipSaddle", true);

            builder.pop();
        }
    }

    public static class Genetics {
        public static BooleanValue useGeneticStats;
        public static BooleanValue enableHealthEffects;
        public static DoubleValue mutationChance;
        public static BooleanValue bookShowsGenes;
        public static BooleanValue bookShowsTraits;

        Genetics(final ForgeConfigSpec.Builder builder) {
            builder.comment("Config settings for genetics")
                    .push("genetics");

            useGeneticStats = builder
                    .comment("If enabled, horses' speed, jump, and health will be determined",
            "through genetics instead of the vanilla Minecraft way")
                    .translation("horse_colors.config.common.useGeneticStats")
                    .define("useGeneticStats", true);

            enableHealthEffects = builder
                    .comment("If enabled, certain genes will have a small impact on health,",
                             "as they do in real life. This config option does not affect Overo",
                             "Lethal White Syndrome.")
                    .translation("horse_colors.config.common.enableHealthEffects")
                    .define("enableHealthEffects", true);

            mutationChance = builder
                    .comment("The chance for each allele to mutate. The recommended range",
                             "is between 0.0001 and 0.01.",
                              "To disable mutations, set this value to 0.")
                    .defineInRange("mutationChance", 0.0005, 0.0, 1.0);

            bookShowsGenes = builder
                    .comment("Enable or disable genetic testing.")
                    .define("bookShowsGenes", true);

            bookShowsTraits = builder
                    .comment("Enable or disable physical inspection (rough information about health, ",
                            "speed, and jump).")
                    .define("bookShowsTraits", true);

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
                    .defineInRange("minHerdSize", 2, 0, Integer.MAX_VALUE);

            maxHerdSize = builder
                    .comment("")
                    .translation("horse_colors.config.spawn.maxHerdSize")
                    .defineInRange("maxHerdSize", 6, 0, Integer.MAX_VALUE);

            spawnBiomeWeights = builder
                    .comment("A list of biomes horses can spawn in, and how often they spawn there.")
                    .<String>defineList("spawnBiomeWeights", 
                                Arrays.asList((new BiomeWeight(BiomeDictionary.Type.PLAINS.toString(), 5)).toString(), 
                                              (new BiomeWeight(BiomeDictionary.Type.SAVANNA.toString(), 1)).toString()), 
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
