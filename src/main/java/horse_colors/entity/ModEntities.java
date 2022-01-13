package sekelsta.horse_colors.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import sekelsta.horse_colors.CreativeTab;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.client.renderer.HorseArmorLayer;
import sekelsta.horse_colors.client.renderer.HorseGeneticModel;
import sekelsta.horse_colors.client.renderer.HorseGeneticRenderer;

@Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
public class ModEntities {
    public static EntityType<HorseGeneticEntity> HORSE_GENETIC = null;
    public static EntityType<DonkeyGeneticEntity> DONKEY_GENETIC = null;
    public static EntityType<MuleGeneticEntity> MULE_GENETIC = null;
    public static SpawnEggItem HORSE_SPAWN_EGG = null;
    public static SpawnEggItem DONKEY_SPAWN_EGG = null;
    public static SpawnEggItem MULE_SPAWN_EGG = null;

    public static final int horseEggPrimary = 0x7F4320;
    public static final int horseEggSecondary = 0x110E0D;
    static
    {
        // Default tracker is fine, or could use .tracker(64, 2, false)
        final ResourceLocation horseRegistryName = new ResourceLocation(HorseColors.MODID, "horse_felinoid");
        // Vanilla size in 1.12 was 1.3964844F, 1.6F
        // Size numbers are width, height
        HORSE_GENETIC = EntityType.Builder.of(HorseGeneticEntity::new, MobCategory.CREATURE).sized(1.2F, 1.6F).build(horseRegistryName.toString());
        HORSE_GENETIC.setRegistryName(horseRegistryName);

        final ResourceLocation donkeyRegistryName = new ResourceLocation(HorseColors.MODID, "donkey");
        DONKEY_GENETIC = EntityType.Builder.of(DonkeyGeneticEntity::new, MobCategory.CREATURE).sized(1.2F, 1.6F).build(donkeyRegistryName.toString());
        DONKEY_GENETIC.setRegistryName(donkeyRegistryName);

        final ResourceLocation muleRegistryName = new ResourceLocation(HorseColors.MODID, "mule");
        MULE_GENETIC = EntityType.Builder.of(MuleGeneticEntity::new, MobCategory.CREATURE).sized(1.2F, 1.6F).build(muleRegistryName.toString());
        MULE_GENETIC.setRegistryName(muleRegistryName);

        HORSE_SPAWN_EGG = new SpawnEggItem(HORSE_GENETIC, horseEggPrimary, horseEggSecondary, (new Item.Properties()).tab(CreativeTab.instance));
        HORSE_SPAWN_EGG.setRegistryName(new ResourceLocation(HorseColors.MODID, "horse_spawn_egg"));

        DONKEY_SPAWN_EGG = new SpawnEggItem(DONKEY_GENETIC, 0x726457, 0xcdc0b5, (new Item.Properties()).tab(CreativeTab.instance));
        DONKEY_SPAWN_EGG.setRegistryName(new ResourceLocation(HorseColors.MODID, "donkey_spawn_egg"));

        MULE_SPAWN_EGG = new SpawnEggItem(MULE_GENETIC, 0x4b3a30, 0xcdb9a8, (new Item.Properties()).tab(CreativeTab.instance));
        MULE_SPAWN_EGG.setRegistryName(new ResourceLocation(HorseColors.MODID, "mule_spawn_egg"));

        SpawnPlacements.register(ModEntities.HORSE_GENETIC, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.DONKEY_GENETIC, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(HORSE_GENETIC, AbstractHorse.createBaseHorseAttributes().build());
        event.put(DONKEY_GENETIC, AbstractChestedHorse.createBaseChestedHorseAttributes().build());
        event.put(MULE_GENETIC, AbstractChestedHorse.createBaseChestedHorseAttributes().build());
    }

    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(
                HORSE_GENETIC,
                DONKEY_GENETIC,
                MULE_GENETIC
        );
    }

    @SubscribeEvent
    public static void registerSpawnEggs(RegistryEvent.Register<Item> event) {

        event.getRegistry().register(HORSE_SPAWN_EGG);
        event.getRegistry().register(DONKEY_SPAWN_EGG);
        event.getRegistry().register(MULE_SPAWN_EGG);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(HORSE_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
        event.registerEntityRenderer(DONKEY_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
        event.registerEntityRenderer(MULE_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HorseGeneticRenderer.EQUINE_LAYER, HorseGeneticModel::createBodyLayer);
        event.registerLayerDefinition(HorseArmorLayer.HORSE_ARMOR_LAYER, HorseGeneticModel::createArmorLayer);
    }

}
