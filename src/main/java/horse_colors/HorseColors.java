package sekelsta.horse_colors;

import sekelsta.horse_colors.client.HorseGui;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ModEntities;
import sekelsta.horse_colors.item.ModItems;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = HorseColors.MODID, name = HorseColors.NAME, version = HorseColors.VERSION)
public class HorseColors
{
    public static final String NAME = "Realistic Horse Colors";
    public static final String MODID = "horse_colors";
    public static final String VERSION = "1.12.2-1.2.6";

    @Mod.Instance("horse_colors")
    public static HorseColors instance;

    public static Logger logger = LogManager.getLogger(MODID);

    public HorseColors()
    {
        instance = this;

        //ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HorseConfig.spec);
    }

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) 
    {
        MinecraftForge.EVENT_BUS.register(ModEntities.class);
        MinecraftForge.EVENT_BUS.register(ModEntities.RegistrationHandler.class);
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        MinecraftForge.EVENT_BUS.register(HorseReplacer.class);
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) 
    {
        ModEntities.registerRenderers();
        registerEventListeners();
    }
    
    @Mod.EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        ModEntities.onLoadComplete();
    }

    @SideOnly(Side.CLIENT)
    public void registerEventListeners()
    {
        HorseDebug hd = new HorseDebug();
        MinecraftForge.EVENT_BUS.register(hd);
        MinecraftForge.EVENT_BUS.register(HorseGui.class);
    }
/*
    public void fixMissingRegistries(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
            if (mapping.key.equals(new ResourceLocation("horse_colors:horse_felinoid_spawn_egg"))) {
                mapping.remap(ModEntities.HORSE_SPAWN_EGG);
            }
        }
    }*/
}
