package sekelsta.horse_colors;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sekelsta.horse_colors.breed.BreedManager;
import sekelsta.horse_colors.client.HorseGui;
import sekelsta.horse_colors.entity.ModEntities;
import sekelsta.horse_colors.item.ModItems;
import sekelsta.horse_colors.network.*;
import sekelsta.horse_colors.world.HorseReplacer;
import sekelsta.horse_colors.world.Spawns;

@Mod(HorseColors.MODID)
public class HorseColors
{
    public static final String MODID = "horse_colors";

    public static HorseColors instance;

    public static Logger logger = LogManager.getLogger(MODID);

    public HorseColors(IEventBus modEventBus)
    {
        instance = this;

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        NeoForge.EVENT_BUS.addListener(ContainerEventHandler::editContainer);
        NeoForge.EVENT_BUS.register(HorseReplacer.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HorseConfig.spec);
        NeoForge.EVENT_BUS.addListener(BreedManager::addReloadListener);
        modEventBus.addListener(ModItems::addToCreativeTab);

        Spawns.registerBiomeModifiers();

        registerDeferredRegistries(modEventBus);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        NeoForge.EVENT_BUS.addListener(HorseGui::replaceGui);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModEntities::registerSpawnPlacements);
        event.enqueueWork(ModItems::registerDispenseBehaviour);
        event.enqueueWork(ModItems::registerPotionRecipes);
        HorsePacketHandler.registerPackets();
    }

    public static void registerDeferredRegistries(IEventBus modBus) {
        ModEntities.ENTITY_DEFERRED.register(modBus);
        ModItems.ITEM_DEFERRED.register(modBus);
        Spawns.BIOME_MODIFIER_DEFERRED.register(modBus);
    }
}
