package sekelsta.horse_colors;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class HorseConfig
{
    private static Set<String> equineFoodsSet = null;
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final Common COMMON = new Common(BUILDER);
    public static final Growth GROWTH = new Growth(BUILDER);
    public static final Breeding BREEDING = new Breeding(BUILDER);
    public static final Spawn SPAWN = new Spawn(BUILDER);
    public static final Genetics GENETICS = new Genetics(BUILDER);

    public static class Common {
        public static BooleanValue horseDebugInfo;
        public static BooleanValue enableGroundTie;
        public static BooleanValue spookyHorses;
        public static BooleanValue jealousStallions;
        public static BooleanValue enableSizes;
        public static BooleanValue herdsFollowRidden;
        public static BooleanValue herdingWhenTame;
        public static BooleanValue useGeneticAnimalsIcons;
        public static BooleanValue rideSmallEquines;
        public static ConfigValue<ArrayList<String>> equineFoods;

        Common(final ModConfigSpec.Builder builder) {
            ArrayList<String> DEFAULT_EQUINE_FOODS = new ArrayList<>(Arrays.asList("hay_block",  "wheat", "sugar", 
                "carrot", "apple", "pumpkin", "carved_pumpkin", "melon_slice", "glistening_melon_slice", "grass", 
                "tall_grass", "dandelion", "cornflower", "oxeye_daisy", "dried_kelp", "sugar_cane", "beetroot", 
                "sweet_berries", "brown_mushroom", "bamboo", "honeycomb", "banana", "cherry", "coconut", 
                "date", "dragonfruit", "fig", "grapefruit", "mango", "nectarine", "orange", "peach", "pear", "pecan", 
                "persimmon", "plum", "walnut", "artichoke", "barley", "basil", "bellpepper", "blackberry", "blueberry", 
                "broccoli", "cabbage", "cantalope", "cauliflower", "celery", "corn", "cranberry", "cucumber", "currant", 
                "grape", "greenbean", "honeydew", "hops", "kale", "kiwi", "lettuce", "oat", "olive", "peanut", 
                "pineapple", "radish", "raspberry", "rice", "rutabaga", "squash", "strawberry", "sweetpotato", 
                "tea_leaves", "turnip", "zucchini", "molasses", "caramel", "raisins", "corn_husk", "rice_bale", 
                "straw_bale", "straw", "blueberries", "okra", "mandarin", "pomelo", "yuzu", "sugar_cube", 
                "timothy_bushel", "oat_bushel", "alfalfa_bushel", "timothy_bale", "timothy_bale_slab", "alfalfa_bale", 
                "alfalfa_bale_slab", "oat_bale", "oat_bale_slab", "quality_bale", "quality_bale_slab", "scoop_sweet", 
                "scoop_rose", "cabbage_leaf", "pumpkin_slice"));

            builder.comment("Common config settings")
                    .push("common");

            enableGroundTie = builder
                    .comment("If enabled, horses will not wander off if they are wearing a saddle.")
                    .translation("horse_colors.config.common.enableGroundTie")
                    .define("enableGroundTie", false);

            spookyHorses = builder
                    .comment("If enabled, horses will be spooked by monsters and throw their rider.")
                    .define("spookyHorses", false);

            jealousStallions = builder
                    .comment("If enabled, stallions will chase each other away from mares.")
                    .define("jealousStallions", true);

            enableSizes = builder
                    .comment("If enabled, horses can be different sizes.")
                    .define("enableSizes", true);

            herdsFollowRidden = builder
                    .comment("If enabled, horse herds can follow a horse even if it is ridden or on a lead.")
                    .define("herdsFollowRidden", true);

            herdingWhenTame = builder
                    .comment("If enabled, horses will exhibit herding behavior when tame as well as when wild.")
                    .define("herdingWhenTame", true);

            useGeneticAnimalsIcons = builder
                    .comment("Whether to use the gray themed gender icons from Genetic Animals (by mokiyoki,",
                             "used by permission) in place of the default pink and blue icons.")
                    .define("useGeneticAnimalsIcons", false);

            rideSmallEquines = builder
                    .comment("Whether the player can ride horses that are realistically too small to carry an adult human")
                    .define("rideSmallEquines", false);

            equineFoods = builder
                    .comment("Foods that can be eaten by horses and donkeys to restore health, increase tameness, and speed foal growth.")
                    .define("equineFoods", DEFAULT_EQUINE_FOODS);

            builder.pop();
        }
    }

    public static class Growth {
        public static DoubleValue yearLength;
        public static BooleanValue grayGradually;
        public static BooleanValue growGradually;
        public static DoubleValue growTime;

        Growth(final ModConfigSpec.Builder builder) {
            builder.comment("Config settings related to growth and aging")
                    .push("growth");

            yearLength = builder
                    .comment("How long a year lasts in twenty minute Minecraft days, for age-dependent colors such as gray.")
                    .defineInRange("yearLength", 4.0, 2/24000., 10000);

            grayGradually = builder
                    .comment("If enabled, gray horses will be born colored and their fur will gradually turn white.",
                    "Otherwise, all gray horses will render as if they were about 6-8 years old.")
                    .define("grayGradually", true);

            growGradually = builder
                    .comment("If enabled, foals will slowly get bigger as they grow into adults.")
                    .define("foalsGrowGradually", false);

            growTime = builder
                    .comment("The number of twenty minute Minecraft days that it takes for a foal to become an adult.")
                    .defineInRange("growTime", 5.0, 2/24000., 10000);

            builder.pop();
        }

        public int getMinAge() {
            return (int)(growTime.get() * -24000);
        }

        public int getMaxAge() {
            return (int)(15f * 24000 * yearLength.get());
        }
    }

    public static class Breeding {
        public static BooleanValue enableGenders;
        public static BooleanValue autobreeding;
        public static IntValue genderlessBreedingCooldown;
        public static IntValue maleBreedingCooldown;
        public static IntValue femaleBreedingCooldown;
        public static IntValue pregnancyLength;
        public static ConfigValue<ArrayList<String>> horseBreedingFoods;
        public static ConfigValue<ArrayList<String>> donkeyBreedingFoods;

        public Breeding(final ModConfigSpec.Builder builder) {
            ArrayList<String> DEFAULT_HORSE_BREEDABLE = new ArrayList<>(Arrays.asList("golden_carrot", 
                "golden_apple", "enchanted_golden_apple", "hay_block"));
            ArrayList<String> DEFAULT_DONKEY_BREEDABLE = new ArrayList<>(Arrays.asList("golden_carrot", 
                "golden_apple", "enchanted_golden_apple", "apple", "hay_block"));

            builder.comment("Config settings related to breeding and gender")
                    .push("breeding");

            enableGenders = builder
                    .comment("Enables or disables all features relating to gender.")
                    .define("enableGenders", true);

            autobreeding = builder
                    .comment("If true, horses can breed automatically if wild or if owner allows it.")
                    .define("autobreeding", false);

            genderlessBreedingCooldown = builder
                    .comment("The number of ticks until horses can breed again, when genders are disabled.",
                            "The vanilla value is 6000 (or at 20 ticks per second, 5 minutes,",
                            "or at 24000 ticks per minecraft day, 1/4 day)")
                    .defineInRange("genderlessBreedingCooldown", 6000, 0, Integer.MAX_VALUE);

            maleBreedingCooldown = builder
                    .comment("The number of ticks until male horses can breed again.",
                            "The default value is 240 ticks (12 seconds).")
                    .defineInRange("maleBreedingCooldown", 240, 0, Integer.MAX_VALUE);

            femaleBreedingCooldown = builder
                    .comment("The number of ticks until female horses can breed again.",
                            "The default value is 96000 ticks (80 minutes, or 4 minecraft days).",
                            "This must always be at least as long as pregnancyLength.")
                    .defineInRange("femaleBreedingCooldown", 96000, 0, Integer.MAX_VALUE);

            pregnancyLength = builder
                    .comment("If genders are enabled, females will be pregnant for this many ticks.",
                            "The default value is 48000 ticks (40 minutes, or 2 minecraft days).",
                            "To disable pregnancy altogether, set this number to 0.",
                            "Lowering this will not let female horses breed again sooner unless you",
                            "also lower femaleBreedingCooldown")
                    .defineInRange("pregnancyLength", 48000, 0, Integer.MAX_VALUE);

            horseBreedingFoods = builder
                    .comment("Foods which put horses in love mode, along with the usual benefits to health, growth, and tameness.")
                    .define("horseBreedingFoods", DEFAULT_HORSE_BREEDABLE);

            donkeyBreedingFoods = builder
                    .comment("Foods which put donkeys in love mode, along with the usual benefits to health, growth, and tameness.")
                    .define("donkeyBreedingFoods", DEFAULT_DONKEY_BREEDABLE);

            builder.pop();
        }
    }

    public static class Genetics {
        public static BooleanValue useGeneticStats;
        public static BooleanValue enableHealthEffects;
        public static DoubleValue mutationChance;
        public static BooleanValue bookShowsGenes;
        public static BooleanValue bookShowsTraits;

        Genetics(final ModConfigSpec.Builder builder) {
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
                    .defineInRange("mutationChance", 0.0001, 0.0, 1.0);

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
        public static BooleanValue blockVanillaDonkeySpawns;
        public static BooleanValue convertVanillaHorses;
        public static BooleanValue convertVanillaDonkeys;
        public static BooleanValue convertVanillaMules;

        Spawn(final ModConfigSpec.Builder builder) {
            builder.comment("Settings to configure spawning. Note as of Minecraft 1.19 spawn weights are now controlled via json data.")
                   .push("spawn");

            blockVanillaHorseSpawns =  builder
                    .comment("Whether to allow new vanilla horses to spawn. This will not affect any",
                             "horses that already exist.")
                    .define("blockVanillaHorseSpawns", true);

            blockVanillaDonkeySpawns =  builder
                    .comment("Whether to allow new vanilla donkeys to spawn. This will not affect any",
                             "horses that already exist.")
                    .define("blockVanillaDonkeySpawns", true);

            convertVanillaHorses = builder
                    .comment("If this is set to true, existing horses will be turned into horses with genetics")
                    .define("convertVanillaHorses", false);

            convertVanillaDonkeys = builder
                    .comment("If this is set to true, existing donkeys will be turned into donkeys with genetics")
                    .define("convertVanillaDonkeys", false);

            convertVanillaMules = builder
                    .comment("If this is set to true, existing mules will be turned into mules with genetics")
                    .define("convertVanillaMules", false);

            builder.pop();
        }

    }

    public static final ModConfigSpec spec = BUILDER.build();

    public static boolean isGenderEnabled() {
        return BREEDING.enableGenders.get();
    }

    public static int getHorseRebreedTicks(boolean isMale) {
        if (!isGenderEnabled()) {
            return BREEDING.genderlessBreedingCooldown.get();
        }
        if (isMale) {
            return BREEDING.maleBreedingCooldown.get();
        }
        return Math.max(BREEDING.femaleBreedingCooldown.get(), getHorsePregnancyLength());
    }

    public static int getHorseBirthAge() {
        return GROWTH.getMinAge();
    }

    public static boolean isPregnancyEnabled() {
        return isGenderEnabled();
    }

    public static int getHorsePregnancyLength() {
        return BREEDING.pregnancyLength.get();
    }

    public static boolean isEquineFood(ItemStack stack) {
        if (equineFoodsSet == null) {
            equineFoodsSet = new HashSet<>(COMMON.equineFoods.get());
        }
        return equineFoodsSet.contains(getName(stack));
    }

    public static boolean isHorseBreedingFood(ItemStack stack) {
        return BREEDING.horseBreedingFoods.get().contains(getName(stack));
    }

    public static boolean isDonkeyBreedingFood(ItemStack stack) {
        return BREEDING.donkeyBreedingFoods.get().contains(getName(stack));
    }

    private static String getName(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    }

    public static boolean shouldConvert(Entity entity) {
        Class c = entity.getClass();
        // We don't want to replace subclasses of horses
        return (c == Horse.class && SPAWN.convertVanillaHorses.get())
            || (c == Donkey.class && SPAWN.convertVanillaDonkeys.get())
            || (c == Mule.class && SPAWN.convertVanillaMules.get());
    }
}
