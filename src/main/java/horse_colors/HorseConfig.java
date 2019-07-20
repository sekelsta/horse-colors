package felinoid.horse_colors;

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

@Config(modid = "horse_colors")
public class HorseConfig
{
    @Comment({
        "If enabled, horses' speed, jump, and health will be determined",
        "through genetics instead of the default Minecraft way"
    })
    public static boolean useGeneticStats = false;

    @Comment({
        "If enabled, debugging information will appear on the screen when the",
        "player is holding a stick in their left hand and looks at a horse.",
        "For most users, it is probably better to leave this as false."
    })
    public static boolean horseDebugInfo = false;

    @Comment({
        "If set to true, only horses created by this mod will spawn."
    })
    public static boolean blockVanillaHorseSpawns = true;

    @Comment({
        "If enabled, each vanilla horse will be replaced by a random modded horse.",
        "This is for loading a vanilla world. For creating a new world, the",
        "recommended setting is false.",
        "If blockVanillaHorseSpawns is set to false, this option won't do",
        "anything."
    })
    public static boolean convertVanillaHorses = true;

    @Comment("What size groups horses will spawn in")
    @RangeInt(min = 0)
    public static int minHerdSize = 4;
    @RangeInt(min = 0)
    public static int maxHerdSize = 8;

    @Comment ("How often horses will spawn")
    public static int spawnWeight = 10;
    


}
