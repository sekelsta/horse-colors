package sekelsta.horse_colors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

public class ModEntities {
    //@ObjectHolder("horse_colors:horse_felinoid")
    public static EntityType<HorseGeneticEntity> HORSE_GENETIC = null;
    //@ObjectHolder("horse_colors:horse_felinoid_spawn_egg")
    public static Item HORSE_SPAWN_EGG = null;

    public static final int horseEggPrimary = 0x7F4320;
    public static final int horseEggSecondary = 0x110E0D;
    static
    {
            // Default tracker is fine, or could use .tracker(64, 2, false)
            final ResourceLocation registryName = new ResourceLocation(HorseColors.MODID, "horse_felinoid");/*
public static final EntityType<HorseEntity> HORSE = register("horse", EntityType.Builder.create(HorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));*/
            HORSE_GENETIC = EntityType.Builder.create(HorseGeneticEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).build(registryName.toString());
            HORSE_GENETIC.setRegistryName(registryName);

            assert(HORSE_GENETIC != null);
            Item spawnEgg = new SpawnEggItem(HORSE_GENETIC, horseEggPrimary, horseEggSecondary, (new Item.Properties()).group(ItemGroup.MISC));
            spawnEgg.setRegistryName(new ResourceLocation(HorseColors.MODID, "horse_genetic_spawn_egg"));
            HORSE_SPAWN_EGG = spawnEgg;
    }
/*
   private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder) {
      return Registry.register(Registry.ENTITY_TYPE, key, builder.build(key));
   }*/

    @Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Entity} types.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {

            event.getRegistry().registerAll(
                    HORSE_GENETIC
            );
            addSpawns();
        }

        @SubscribeEvent
        public static void registerSpawnEggs(RegistryEvent.Register<Item> event) {

            event.getRegistry().register(HORSE_SPAWN_EGG);
        }

        // This needs to be called AFTER registerEntities()
        public static void addSpawns()
        {
            assert(HORSE_GENETIC != null);
            addSpawn(HORSE_GENETIC, HorseConfig.SPAWN.spawnWeight.get(), HorseConfig.SPAWN.minHerdSize.get(), HorseConfig.SPAWN.maxHerdSize.get(), EntityClassification.CREATURE, getBiomes(BiomeDictionary.Type.PLAINS));
            addSpawn(HORSE_GENETIC, HorseConfig.SPAWN.spawnWeight.get(), HorseConfig.SPAWN.minHerdSize.get(), HorseConfig.SPAWN.maxHerdSize.get(), EntityClassification.CREATURE, getBiomes(BiomeDictionary.Type.SAVANNA));
        }

        /**
         * Add a spawn entry for the supplied entity in the supplied {@link Biome} list.
         * <p>
         * Adapted from Forge's {@code EntityRegistry.addSpawn} method in 1.12.2.
         *
         * @param entityType     The entity type
         * @param itemWeight     The weight of the spawn list entry (higher weights have a higher chance to be chosen)
         * @param minGroupCount  Min spawn count
         * @param maxGroupCount  Max spawn count
         * @param classification The entity classification
         * @param biomes         The biomes to add the spawn to
         */
        private static void addSpawn(final EntityType<? extends LivingEntity> entityType, final int itemWeight, final int minGroupCount, final int maxGroupCount, final EntityClassification classification, final Biome... biomes) {
            for (final Biome biome : biomes) {
                final List<Biome.SpawnListEntry> spawns = biome.getSpawns(classification);

                // Try to find an existing entry for the entity type
                spawns.stream()
                        .filter(entry -> entry.entityType == entityType)
                        .findFirst()
                        .ifPresent(spawns::remove); // If there is one, remove it

                // Add a new one
                spawns.add(new Biome.SpawnListEntry(entityType, itemWeight, minGroupCount, maxGroupCount));
            }
        }

        /**
         * Get an array of {@link Biome}s with the specified {@link BiomeDictionary.Type}.
         *
         * @param type The Type
         * @return An array of Biomes
         */
        private static Biome[] getBiomes(final BiomeDictionary.Type type) {
            return BiomeDictionary.getBiomes(type).toArray(new Biome[0]);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(HORSE_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
    }
}
