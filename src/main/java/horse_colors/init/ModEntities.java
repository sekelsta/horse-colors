package sekelsta.horse_colors.init;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.CreativeTab;
import sekelsta.horse_colors.config.HorseConfig;

import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.renderer.HorseGeneticRenderer;
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
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
            HORSE_GENETIC = EntityType.Builder.create(HorseGeneticEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).build(horseRegistryName.toString());
            HORSE_GENETIC.setRegistryName(horseRegistryName);

            final ResourceLocation donkeyRegistryName = new ResourceLocation(HorseColors.MODID, "donkey");
            DONKEY_GENETIC = EntityType.Builder.create(DonkeyGeneticEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).build(donkeyRegistryName.toString());
            DONKEY_GENETIC.setRegistryName(donkeyRegistryName);

            final ResourceLocation muleRegistryName = new ResourceLocation(HorseColors.MODID, "mule");
            MULE_GENETIC = EntityType.Builder.create(MuleGeneticEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).build(muleRegistryName.toString());
            MULE_GENETIC.setRegistryName(muleRegistryName);

            HORSE_SPAWN_EGG = new SpawnEggItem(HORSE_GENETIC, horseEggPrimary, horseEggSecondary, (new Item.Properties()).group(CreativeTab.instance));
            HORSE_SPAWN_EGG.setRegistryName(new ResourceLocation(HorseColors.MODID, "horse_spawn_egg"));

            DONKEY_SPAWN_EGG = new SpawnEggItem(DONKEY_GENETIC, 0x726457, 0xcdc0b5, (new Item.Properties()).group(CreativeTab.instance));
            DONKEY_SPAWN_EGG.setRegistryName(new ResourceLocation(HorseColors.MODID, "donkey_spawn_egg"));

            MULE_SPAWN_EGG = new SpawnEggItem(MULE_GENETIC, 0x4b3a30, 0xcdb9a8, (new Item.Properties()).group(CreativeTab.instance));
            MULE_SPAWN_EGG.setRegistryName(new ResourceLocation(HorseColors.MODID, "mule_spawn_egg"));
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

    }
    // This needs to be called AFTER registerEntities()
    // Also after the config is parsed
    public static void addSpawns()
    {
        if (HORSE_GENETIC == null) {
            HorseColors.logger.error("Attempting to add horse spawns with a null horse.");
        }
        if (DONKEY_GENETIC == null) {
            HorseColors.logger.error("Attempting to add horse spawns with a null donkey.");
        }
        addSpawn(HORSE_GENETIC, HorseConfig.HORSE_SPAWN);
        addSpawn(DONKEY_GENETIC, HorseConfig.DONKEY_SPAWN);
    }

    private static void addSpawn(EntityType<? extends LivingEntity> entity, HorseConfig.Spawn spawn) {
        List<? extends String> excludeList = spawn.excludeBiomes.get();
        HashSet<Biome> excludeBiomes = new HashSet();
        for (String rawBiome : excludeList) {
            for (Biome b : getBiomes(HorseConfig.Spawn.BiomeWeight.getType(rawBiome))) {
                excludeBiomes.add(b);
            }
        }

        List<? extends String> rawList = spawn.spawnBiomeWeights.get();
        for (String rawBiomeWeight : rawList) {
            HorseConfig.Spawn.BiomeWeight bw = new HorseConfig.Spawn.BiomeWeight(rawBiomeWeight);
            BiomeDictionary.Type type = HorseConfig.Spawn.BiomeWeight.getType(bw.biome);
            for (Biome biome : getBiomes(type)) {
                if (excludeBiomes.contains(biome)) {
                    HorseColors.logger.debug("Skipping horse spawn for " + biome);
                    continue;
                }
                HorseColors.logger.debug("Adding horse spawn to " + biome);
                List<Biome.SpawnListEntry> spawns = biome.getSpawns(EntityClassification.CREATURE);
                spawns.add(new Biome.SpawnListEntry(entity, bw.weight, spawn.minHerdSize.get(), spawn.maxHerdSize.get()));
            }
        }
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
/*
            // Try to find an existing entry for the entity type
            spawns.stream()
                    .filter(entry -> entry.entityType == entityType)
                    .findFirst()
                    .ifPresent(spawns::remove); // If there is one, remove it*/

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
        assert(type != null);
        assert(BiomeDictionary.getBiomes(type) != null);
        return BiomeDictionary.getBiomes(type).toArray(new Biome[0]);
    }

    //Removes initial vanilla horse spawns
    public static void editSpawnTable() {
        Set<Biome> allBiomes = Biome.BIOMES;
        for (Biome biome : allBiomes) {
                List<Biome.SpawnListEntry> spawns = biome.getSpawns(EntityClassification.CREATURE);
                if (spawns.isEmpty()) {
                    continue;
                }
                ArrayList<Biome.SpawnListEntry> horseSpawns = new ArrayList<Biome.SpawnListEntry>();
                for (Biome.SpawnListEntry entry : spawns) {
                    if (entry.entityType == EntityType.HORSE && HorseConfig.HORSE_SPAWN.blockVanillaSpawns.get()) {
                        HorseColors.logger.info("Removing vanilla horse spawn: " + entry + " from biome " + biome);
                        horseSpawns.add(entry);
                    }
                    else if (entry.entityType == EntityType.DONKEY && HorseConfig.DONKEY_SPAWN.blockVanillaSpawns.get()) {
                        HorseColors.logger.info("Removing vanilla donkey spawn: " + entry + " from biome " + biome);
                        horseSpawns.add(entry);
                    }
                }
                for (Biome.SpawnListEntry horseSpawn : horseSpawns) {
                    spawns.remove(horseSpawn);
                }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenders()
    {
        RenderingRegistry.registerEntityRenderingHandler(HORSE_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(DONKEY_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(MULE_GENETIC, renderManager -> new HorseGeneticRenderer(renderManager));
    }


    @SubscribeEvent
    public static void onLoadComplete(net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent e) {
        // These need to happen after the config file is read and vanilla horse spawns are added
        editSpawnTable();
        addSpawns();
    }
}
