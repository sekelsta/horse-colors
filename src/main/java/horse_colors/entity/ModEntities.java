package sekelsta.horse_colors.entity;

// TODO: Remove unused imports
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.client.renderer.HorseArmorLayer;
import sekelsta.horse_colors.client.renderer.HorseGeneticModel;
import sekelsta.horse_colors.client.renderer.HorseGeneticRenderer;
import sekelsta.horse_colors.item.ModItems;

@Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_DEFERRED 
        = DeferredRegister.create(Registries.ENTITY_TYPE, HorseColors.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<HorseGeneticEntity>> HORSE_GENETIC 
        = registerEntity("horse_felinoid", HorseGeneticEntity::new, 1.2F, 1.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<DonkeyGeneticEntity>> DONKEY_GENETIC 
        = registerEntity("donkey", DonkeyGeneticEntity::new, 1.2F, 1.6F);
    public static final DeferredHolder<EntityType<?>, EntityType<MuleGeneticEntity>> MULE_GENETIC 
        = registerEntity("mule", MuleGeneticEntity::new, 1.2F, 1.6F);

    public static DeferredItem<Item> HORSE_SPAWN_EGG = registerSpawnEgg("horse_spawn_egg", HORSE_GENETIC, 0x7F4320, 0x110E0D);
    public static DeferredItem<Item> DONKEY_SPAWN_EGG = registerSpawnEgg("donkey_spawn_egg", DONKEY_GENETIC, 0x726457, 0xcdc0b5);
    public static DeferredItem<Item> MULE_SPAWN_EGG = registerSpawnEgg("mule_spawn_egg", MULE_GENETIC, 0x4b3a30, 0xcdb9a8);

    private static <T extends Animal> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(
            String name, EntityType.EntityFactory<T> factory, float width, float height) {
        final ResourceLocation registryName = new ResourceLocation(HorseColors.MODID, name);
        return ENTITY_DEFERRED.register(name, 
            () -> EntityType.Builder.of(factory, MobCategory.CREATURE).sized(width, height).build(registryName.toString())
        );
    }

    private static DeferredItem<Item> registerSpawnEgg(String name, DeferredHolder<EntityType<?>, ? extends EntityType<? extends Mob>> type, int primary, int secondary) {
        return ModItems.ITEM_DEFERRED.register(name, 
            () -> new DeferredSpawnEggItem(type, primary, secondary, new Item.Properties())
        );
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HORSE_GENETIC.get(), AbstractHorse.createBaseHorseAttributes().build());
        event.put(DONKEY_GENETIC.get(), AbstractChestedHorse.createBaseChestedHorseAttributes().build());
        event.put(MULE_GENETIC.get(), AbstractChestedHorse.createBaseChestedHorseAttributes().build());
    }

    public static void registerSpawnPlacements() {
        SpawnPlacements.register(HORSE_GENETIC.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(DONKEY_GENETIC.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(HORSE_GENETIC.get(), renderManager -> new HorseGeneticRenderer(renderManager));
        event.registerEntityRenderer(DONKEY_GENETIC.get(), renderManager -> new HorseGeneticRenderer(renderManager));
        event.registerEntityRenderer(MULE_GENETIC.get(), renderManager -> new HorseGeneticRenderer(renderManager));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HorseGeneticRenderer.EQUINE_LAYER, HorseGeneticModel::createBodyLayer);
        event.registerLayerDefinition(HorseArmorLayer.HORSE_ARMOR_LAYER, HorseGeneticModel::createArmorLayer);
    }

    // Called from ModItems
    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            event.accept(HORSE_SPAWN_EGG);
            event.accept(DONKEY_SPAWN_EGG);
            event.accept(MULE_SPAWN_EGG);
        }
    }
}
