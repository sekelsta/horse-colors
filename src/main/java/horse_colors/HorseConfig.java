package sekelsta.horse_colors;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
//import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import org.apache.commons.lang3.tuple.Pair;

public class HorseConfig
{/*
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final Common COMMON = new Common(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();
*/
    public static class Common {
        public final BooleanValue useGeneticStats;
        public static BooleanValue horseDebugInfo;
        public static BooleanValue blockVanillaHorseSpawns;
        //public static BooleanValue convertVanillaHorses;

        public static IntValue minHerdSize;
        public static IntValue maxHerdSize;
        public static IntValue spawnWeight;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("Common config settings")
                    .push("common");

            useGeneticStats = builder
                    .comment("If enabled, horses' speed, jump, and health will be determined",
            "through genetics instead of the default Minecraft way")
                    .translation("horse_colors.config.common.useGeneticStats")
                    .define("useGeneticStats", false);

            horseDebugInfo = builder
                    .comment("If enabled, debugging information will appear on the screen when the",
            "player is holding a stick in their left hand and looks at a horse.",
            "For most users, it is probably better to leave this as false.")
                    .translation("horse_colors.config.common.horseDebugInfo")
                    .define("horseDebugInfo", false);

            blockVanillaHorseSpawns = builder
                    .comment("If set to true, only horses created by this mod will spawn.",
            "This mainly affects newly generated areas.")
                    .translation("horse_colors.config.common.blockVanillaHorseSpawns")
                    .define("blockVanillaHorseSpawns", true);

            /*convertVanillaHorses = builder
                    .comment(
            "If enabled, each vanilla horse will be replaced by a horse",
            "from this mod.",
            "This matters for already generated areas, or if blockVanillaHorseSpawns is set to false.")
                    .translation("horse_colors.config.common.convertVanillaHorses")
                    .define("convertVanillaHorses", true);*/

            minHerdSize = builder
                    .comment("What size groups horses will spawn in")
                    .translation("horse_colors.config.common.minHerdSize")
                    .defineInRange("minHerdSize", 4, 0, Integer.MAX_VALUE);

            maxHerdSize = builder
                    .comment("")
                    .translation("horse_colors.config.common.maxHerdSize")
                    .defineInRange("maxHerdSize", 8, 0, Integer.MAX_VALUE);

            spawnWeight = builder
                    .comment("How often horses will spawn")
                    .translation("horse_colors.config.common.spawnWeight")
                    .defineInRange("spawnWeight", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    // I have no idea what this does
    private static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void register(final ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
    }
}
