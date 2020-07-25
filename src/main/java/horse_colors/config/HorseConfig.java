package sekelsta.horse_colors.config;

import java.util.List;
import java.util.*;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.common.BiomeDictionary;

import sekelsta.horse_colors.HorseColors;

@Config(modid = "horse_colors")
public class HorseConfig
{

    @Comment({
        "If enabled, horses' speed, jump, and health will be determined",
        "through genetics instead of the default Minecraft way"
    })
    @Config.LangKey("horse_colors.config.common.useGeneticStats")
    public static boolean useGeneticStats = true;

    @Comment({
            "If enabled, certain genes will have a small impact on health,",
            "as they do in real life. This does not prevent Overo Lethal",
            "White Syndrome."
    })
    @Config.LangKey("horse_colors.config.common.enableHealthEffects")
    public static boolean enableHealthEffects = true;

    @Comment("If enabled, horses will not wander off if they are wearing a saddle.")
    @Config.LangKey("horse_colors.config.common.enableGroundTie")
    public static boolean enableGroundTie = false;

    @Comment({
            "If enabled, debugging information will appear on the screen when the",
            "player is holding a stick in their left hand and looks at a horse.",
            "For most users, it is probably better to leave this as false."
    })
    @Config.LangKey("horse_colors.config.common.horseDebugInfo")
    public static boolean horseDebugInfo = false;

    @Comment({
            "The chance for each allele to mutate.",
            "There are at least 80 genes, each with 2 alleles, and each mutation",
            "has about half a chance of having no effect, so with the default",
            "value of 0.0005, each foal has a 5% chance of having at least",
            "one mutation. For mutationChance = 0.001, that chance becomes about 7%,",
            "and for mutationChance = 0.01 a foal has about a 45% chance of having at",
            "least one mutation. Any higher is not recommended.",
            "To disable mutations, set this value to 0."
    })
    @Config.RangeDouble(min = 0.0, max =  1.0)
    public static double mutationChance = 0.0005;

    @Comment({
            "How long a year lasts in ticks, for the purposes of graying.",
            "The default 24000 ticks is one minecraft day."
    })
    @RangeDouble(min = 2/24000, max = 10000)
    public static double yearLength = 2.0;

    @Comment("How many years a horse will age, for the purposes of graying.")
    @Config.RangeDouble(min = 0.0, max = 25.0)
    public static double maxAge = 15.0;

    @Comment({
        "If set to true, only horses created by this mod will spawn.",
        "This mainly affects newly generated areas."
    })
    public static boolean blockVanillaHorseSpawns = true;

    @Comment({
        "If enabled, each vanilla horse will be replaced by a horse",
        "from this mod.",
        "This matters for worlds where vanilla horses have already spawned",
        "or will spawn."
    })
    public static boolean convertVanillaHorses = false;

    @Comment("What size groups horses will spawn in")
    @RangeInt(min = 0)
    public static int minHerdSize = 4;
    @RangeInt(min = 0)
    public static int maxHerdSize = 6;

    @Comment ("How often horses will spawn")
    public static int spawnWeight = 5;
    
	@Mod.EventBusSubscriber(modid = HorseColors.MODID)
	private static class EventHandler {

		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final OnConfigChangedEvent event) {
			if (event.getModID().equals(HorseColors.MODID)) {
				ConfigManager.sync(HorseColors.MODID, Config.Type.INSTANCE);
			}
		}
	}

    public static boolean getEnableGroundTie() {
        return enableGroundTie;
    }

    public static boolean getGrowsGradually() {
        return false;
    }

    public static boolean getUseGeneticStats() {
        return useGeneticStats;
    }

    public static boolean getEnableHealthEffects() {
        return enableHealthEffects;
    }

    public static boolean getBookShowsGenes() {
        return true;
    }

    public static boolean getBookShowsTraits() {
        return true;
    }

    public static boolean blockVanillaHorseSpawns() {
        return blockVanillaHorseSpawns;
    }

    public static boolean blockVanillaDonkeySpawns() {
        return false;
    }

    public static int getYearLength() {
        return (int)(yearLength * 24000);
    }

    public static int getMaxAge() {
        //return (int)(HorseConfig.GROWTH.maxAge.get() * getYearLength());
        return (int)(maxAge * getYearLength());
    }

    public static boolean enableDebugInfo() {
        return horseDebugInfo;
    }

    public static double getMutationChance() {
        return mutationChance;
    }

    public static int getMinAge() {
        return -24000;
    }

    public static double getMaxChildGrowth() {
        return 0;
    }

    public static boolean getAutoEquipSaddle() {
        return true;
    }

    public static boolean convertVanillaHorses() {
        return convertVanillaHorses;
    }
}
