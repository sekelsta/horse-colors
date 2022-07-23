package sekelsta.horse_colors.entity;

import net.minecraft.world.entity.EntityType;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import sekelsta.horse_colors.CreativeTab;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.client.renderer.HorseArmorLayer;
import sekelsta.horse_colors.client.renderer.HorseGeneticModel;
import sekelsta.horse_colors.client.renderer.HorseGeneticRenderer;
import sekelsta.horse_colors.item.ModItems;

@Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_DEFERRED 
        = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HorseColors.MODID);

    public static final RegistryObject<EntityType<HorseGeneticEntity>> HORSE_GENETIC 
        = registerEntity("horse_felinoid", HorseGeneticEntity::new, 1.2F, 1.6F);

    public static RegistryObject<Item> HORSE_SPAWN_EGG 
        = registerSpawnEgg("horse_spawn_egg", HORSE_GENETIC, 0x7F4320, 0x110E0D);

    private static <T extends Animal> RegistryObject<EntityType<T>> registerEntity(
            String name, EntityType.EntityFactory<T> factory, float width, float height) {
        final ResourceLocation registryName = new ResourceLocation(HorseColors.MODID, name);
        return ENTITY_DEFERRED.register(name, 
            () -> EntityType.Builder.of(factory, MobCategory.CREATURE).sized(width, height).build(registryName.toString())
        );
    }

    private static RegistryObject<Item> registerSpawnEgg(String name, RegistryObject<? extends EntityType<? extends Mob>> type, int primary, int secondary) {
        return ModItems.ITEM_DEFERRED.register(name, 
            () -> new ForgeSpawnEggItem(type, primary, secondary, new Item.Properties().tab(CreativeTab.instance))
        );
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HORSE_GENETIC.get(), AbstractHorse.createBaseHorseAttributes().build());
    }

    public static void registerSpawnPlacements() {
        SpawnPlacements.register(HORSE_GENETIC.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(HORSE_GENETIC.get(), renderManager -> new HorseGeneticRenderer(renderManager));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HorseGeneticRenderer.EQUINE_LAYER, HorseGeneticModel::createBodyLayer);
        event.registerLayerDefinition(HorseArmorLayer.HORSE_ARMOR_LAYER, HorseGeneticModel::createArmorLayer);
    }

}
