package sekelsta.horse_colors;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(HorseColors.MODID)
public class HorseColors
{
    public static final String MODID = "horse_colors";

    public static HorseColors instance;

    public static Logger logger = LogManager.getLogger(MODID);

    public HorseColors()
    {
        instance = this;

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        HorseConfig.register(ModLoadingContext.get());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(ModEntities.class);
        MinecraftForge.EVENT_BUS.register(HorseReplacer.class);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ModEntities.registerRenders();
    }
}
