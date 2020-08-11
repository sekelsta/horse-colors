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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.common.BiomeDictionary;

import sekelsta.horse_colors.HorseColors;

@Config(modid = "horse_colors")
public class HorseConfig
{
    public static final Common COMMON = new Common();
    public static final Growth GROWTH = new Growth();
    public static final Breeding BREEDING = new Breeding();
    public static final Genetics GENETICS = new Genetics();

    public static class Common {
        @Comment({
                "If enabled, debugging information will appear on the screen when the",
                "player is holding a stick in their left hand and looks at a horse.",
                "For most users, it is probably better to leave this as false."
        })
        @Config.LangKey("horse_colors.config.common.horseDebugInfo")
        public boolean horseDebugInfo = false;

        @Comment("If enabled, horses will not wander off if they are wearing a saddle.")
        @Config.LangKey("horse_colors.config.common.enableGroundTie")
        public boolean enableGroundTie = false;

        @Comment({"If enabled, right clicking a horse while holding a saddle or horse armor", 
                                 "will equip it (as long as the horse isn't already wearing something in that slot)",
                                 "instead of opening the inventory."})
        @Config.LangKey("horse_colors.config.common.autoEquipSaddle")
        public boolean autoEquipSaddle = true;

        @Comment({
            "If set to true, only horses created by this mod will spawn.",
            "This mainly affects newly generated areas."
        })
        public boolean blockVanillaHorseSpawns = true;

        @Comment({
            "If enabled, each vanilla horse will be replaced by a horse",
            "from this mod.",
            "This matters for worlds where vanilla horses have already spawned",
            "or will spawn."
        })
        public boolean convertVanillaHorses = false;
    }

    public static class Growth {
        @Comment({
                "How long a year lasts in twenty minute Minecraft days, for the purposes of graying.",
                "Internally this number will be converted to ticks before it is used."
        })
        @RangeDouble(min = 2/24000, max = 10000)
        public double yearLength = 2.0;

        @Comment("How many years a horse will age, for the purposes of graying.")
        @Config.RangeDouble(min = 0.0, max = 25.0)
        public double maxAge = 15.0;

        @Comment({"If enabled, foals will slowly get bigger as they grow into adults."})
        public boolean foalsGrowGradually = false;

        @Comment({"The number of twenty minute Minecraft days that it takes for a foal to become an adult."})
        public double growTime = 1;

        public int getMinAge() {
            return (int)(growTime * -24000);
        }

        public int getMaxAge() {
            return 15 * 24000;
        }
    }

    public static class Breeding {
        @Comment({"Enables or disables all features relating to gender."})
        public boolean enableGenders = false;

        @Comment({"The number of ticks until horses can breed again, when genders are disabled.",
                            "The vanilla value is 6000 (or at 20 ticks per second, 5 minutes,",
                            "or at 24000 ticks per minecraft day, 1/4 day)"})
        @Config.RangeInt(min = 0)
        public int genderlessBreedingCooldown = 6000;

        @Comment({"The number of ticks until male horses can breed again.",
                            "The default value is 240 ticks (12 seconds)."})
        @Config.RangeInt(min = 0)
        public int maleBreedingCooldown = 240;

        @Comment({"The number of ticks until female horses can breed again.",
                            "The default value is 24000 ticks (20 minutes, or 1 minecraft day).",
                            "This must always be at least as long as pregnancyLength."})
        @Config.RangeInt(min = 0)
        public int femaleBreedingCooldown = 24000;

        @Comment({"If genders are enabled, females will be pregnant for this many ticks.",
                        "The default value is 24000 ticks (20 minutes, or 1 minecraft day).",
                        "To disable pregnancy altogether, set this number to 0.",
                        "Lowering this will not let female horses breed again sooner unless you",
                        "also lower femaleRebreedTicks"})
        @Config.RangeInt(min = 0)
        public int pregnancyLength = 24000;
    }

    public static class Genetics {
        @Comment({
            "If enabled, horses' speed, jump, and health will be determined",
            "through genetics instead of the default Minecraft way"
        })
        @Config.LangKey("horse_colors.config.common.useGeneticStats")
        public boolean useGeneticStats = true;

        @Comment({
                "If enabled, certain genes will have a small impact on health,",
                "as they do in real life. This does not prevent Overo Lethal",
                "White Syndrome."
        })
        @Config.LangKey("horse_colors.config.common.enableHealthEffects")
        public boolean enableHealthEffects = true;

        @Comment({
                "The chance for each allele to mutate. The recommended range",
                             "is between 0.0001 and 0.01.",
                              "To disable mutations, set this value to 0."
        })
        @Config.RangeDouble(min = 0.0, max =  1.0)
        public double mutationChance = 0.0005;

        @Comment({
                "Enable or disable genetic testing."
        })
        public boolean bookShowsGenes = true;

        @Comment({
                "Enable or disable physical inspection (rough information about health, ",
                            "speed, and jump)."
        })
        public boolean bookShowsTraits = true;
    }

    public static boolean getEnableGroundTie() {
        return COMMON.enableGroundTie;
    }

    public static boolean getGrowsGradually() {
        return GROWTH.foalsGrowGradually;
    }

    public static boolean getUseGeneticStats() {
        return GENETICS.useGeneticStats;
    }

    public static boolean getEnableHealthEffects() {
        return GENETICS.enableHealthEffects;
    }

    public static boolean getBookShowsGenes() {
        return GENETICS.bookShowsGenes;
    }

    public static boolean getBookShowsTraits() {
        return GENETICS.bookShowsTraits;
    }

    public static boolean blockVanillaHorseSpawns() {
        return COMMON.blockVanillaHorseSpawns;
    }

    public static boolean blockVanillaDonkeySpawns() {
        return false;
    }

    public static int getYearLength() {
        return (int)(GROWTH.yearLength * 24000);
    }

    public static int getMaxAge() {
        //return (int)(HorseConfig.GROWTH.maxAge.get() * getYearLength());
        return (int)(GROWTH.maxAge * getYearLength());
    }

    public static boolean enableDebugInfo() {
        return COMMON.horseDebugInfo;
    }

    public static double getMutationChance() {
        return GENETICS.mutationChance;
    }

    public static int getMinAge() {
        return GROWTH.getMinAge();
    }

    public static double getMaxChildGrowth() {
        return 1;
    }

    public static boolean getAutoEquipSaddle() {
        return COMMON.autoEquipSaddle;
    }

    public static boolean convertVanillaHorses() {
        return COMMON.convertVanillaHorses;
    }
    
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

    public static boolean isGenderEnabled() {
        return BREEDING.enableGenders;
    }

    public static int getHorseRebreedTicks(boolean isMale) {
        if (!isGenderEnabled()) {
            return BREEDING.genderlessBreedingCooldown;
        }
        if (isMale) {
            return BREEDING.maleBreedingCooldown;
        }
        return Math.max(BREEDING.femaleBreedingCooldown, getHorsePregnancyLength());
    }

    public static int getHorseBirthAge() {
        return GROWTH.getMinAge();
    }

    public static boolean isPregnancyEnabled() {
        return isGenderEnabled();
    }

    public static int getHorsePregnancyLength() {
        return BREEDING.pregnancyLength;
    }
}
