package sekelsta.horse_colors.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.pools.*;

import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.List;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ModEntities;
import sekelsta.horse_colors.HorseColors;

@Mod.EventBusSubscriber(modid = HorseColors.MODID)
public class Spawns {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_DEFERRED = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, HorseColors.MODID);

    private static final RegistryObject<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZER = RegistryObject.create(new ResourceLocation(HorseColors.MODID, "biome_spawn_serializer"), ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, HorseColors.MODID);

    public record EquineBiomeModifier(SpawnerData plainsSpawner, SpawnerData savannaSpawner) implements BiomeModifier {
        @Override
        public void modify(Holder<Biome> biomeHolder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase == Phase.ADD) {
                if (biomeHolder.containsTag(Tags.Biomes.IS_PLAINS)) {
                    builder.getMobSpawnSettings().addSpawn(plainsSpawner.type.getCategory(), plainsSpawner);
                }
                else if (biomeHolder.containsTag(Tags.Biomes.IS_SAVANNA)) {
                    builder.getMobSpawnSettings().addSpawn(savannaSpawner.type.getCategory(), savannaSpawner);
                }
            }
            else if (phase == Phase.REMOVE) {
                List<SpawnerData> spawns = builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE);
                spawns.removeIf(EquineBiomeModifier::shouldRemove);
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return BIOME_MODIFIER_SERIALIZER.get();
        }

        private static boolean shouldRemove(SpawnerData spawner) {
            return spawner.type == EntityType.HORSE || spawner.type == EntityType.DONKEY;
        }
    }

    public static void registerBiomeModifiers() {
        Codec<EquineBiomeModifier> codec = 
            RecordCodecBuilder.create(builder -> builder.group(
                    SpawnerData.CODEC.fieldOf("plains_spawner").forGetter(EquineBiomeModifier::plainsSpawner),
                    SpawnerData.CODEC.fieldOf("savanna_spawner").forGetter(EquineBiomeModifier::savannaSpawner)
            ).apply(builder, EquineBiomeModifier::new));
        BIOME_MODIFIER_DEFERRED.register("equine_spawn", () -> codec);
    }

    @SubscribeEvent
    // This is not registered to the event queue by the Mod.EventBusSubscriber 
    // annotation because that only registers Forge events, not mod lifecycle 
    // events. Instead it is subscribed from the HorseColors class.
    public static void onLoadComplete(net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent e) {
        // This needs to happen after the config is read
        changeVillageAnimals();
    }

/*
    // TODO: move logic
    public static void addBiomeSpawns(BiomeLoadingEvent event) {
        int horsePlainsWeight = (int)Math.round(5 * HorseConfig.SPAWN.horseSpawnMultiplier.get());
        SpawnerData horsePlainsSpawner = new SpawnerData(ModEntities.HORSE_GENETIC.get(), horsePlainsWeight, 2, 6);
        int horseSavannaWeight = (int)Math.round(1 * HorseConfig.SPAWN.horseSpawnMultiplier.get());
        SpawnerData horseSavannaSpawner = new SpawnerData(ModEntities.HORSE_GENETIC.get(), horseSavannaWeight, 2, 6);
        int donkeyWeight = (int)Math.round(1 * HorseConfig.SPAWN.donkeySpawnMultiplier.get());
        // It seems 1.16.2 has increased donkey max herd size in savannas from 1 to 3, to match the plains
        SpawnerData donkeySpawner = new SpawnerData(ModEntities.DONKEY_GENETIC.get(), donkeyWeight, 1, 3);

        // Add to the spawn list according to biome type
        List<SpawnerData> spawns = event.getSpawns().getSpawner(MobCategory.CREATURE);
        if (event.getCategory() == Biome.BiomeCategory.PLAINS && horsePlainsWeight > 0) {
            spawns.add(horsePlainsSpawner);
            spawns.add(donkeySpawner);
        }
        else if (event.getCategory() == Biome.BiomeCategory.SAVANNA && horseSavannaWeight > 0) {
            spawns.add(horseSavannaSpawner);
            spawns.add(donkeySpawner);
        }
    }

    // TODO: move logic
    public static void removeBiomeSpawns(BiomeLoadingEvent event) {
        List<SpawnerData> entriesToRemove = new ArrayList<>();
        List<SpawnerData> originalSpawns = event.getSpawns().getSpawner(MobCategory.CREATURE);
        for (SpawnerData entry : originalSpawns) {
            if (entry.type == EntityType.HORSE && HorseConfig.SPAWN.blockVanillaHorseSpawns.get()) {
                HorseColors.logger.debug("Removing vanilla horse spawn: " + entry);
                entriesToRemove.add(entry);
            }
            else if (entry.type == EntityType.DONKEY && HorseConfig.SPAWN.blockVanillaDonkeySpawns.get()) {
                HorseColors.logger.debug("Removing vanilla donkey spawn: " + entry);
                entriesToRemove.add(entry);
            }
        }

        for (SpawnerData entry : entriesToRemove) {
            originalSpawns.remove(entry);
        }
    }
*/

    private static boolean isVanillaVillageHorsePiece(SinglePoolElement piece) {
        return piece.toString().contains("minecraft:village/common/animals/horses");
    }

    private static boolean keepJigsawPair(Pair<StructurePoolElement, Integer> pair) {
        if (!HorseConfig.SPAWN.blockVanillaHorseSpawns.get()) {
            return true;
        }
        StructurePoolElement piece = pair.getFirst();
        if (piece instanceof SinglePoolElement) {
            return !isVanillaVillageHorsePiece((SinglePoolElement)piece);
        }
        // This code normally won't be reached
        return true;
    }

    public static void changeVillageAnimals() {
        // Force the static block to run
        PlainVillagePools.bootstrap();
        HorseColors.logger.debug("Modifying village animal spawns");
        ResourceLocation animalsLoc = new ResourceLocation("village/common/animals");
        java.util.Optional<StructureTemplatePool> animalsOpt = BuiltinRegistries.TEMPLATE_POOL.getOptional(animalsLoc);
        if (!animalsOpt.isPresent()) {
            System.err.println("Trying to overwrite village spawns too soon");
            return;
        }
        StructureTemplatePool animals = animalsOpt.get();
        // f_210559_ = rawTemplates
        List<Pair<StructurePoolElement, Integer>> vanillaList =  ObfuscationReflectionHelper.getPrivateValue(StructureTemplatePool.class, animals, "f_210559_");
        List<Pair<StructurePoolElement, Integer>> keeperList = new ArrayList<>();
        for (Pair<StructurePoolElement, Integer> p : vanillaList) {
            if (keepJigsawPair(p)) {
                keeperList.add(p);
            }
        }
        // Add my own pieces
        List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> customPieces = new ArrayList<>();
        String modloc = HorseColors.MODID + ":";
        customPieces.add(new Pair<>(StructurePoolElement.legacy(modloc + "village/common/animals/horses_1"), 1));
        customPieces.add(new Pair<>(StructurePoolElement.legacy(modloc + "village/common/animals/horses_2"), 1));
        customPieces.add(new Pair<>(StructurePoolElement.legacy(modloc + "village/common/animals/horses_3"), 1));
        customPieces.add(new Pair<>(StructurePoolElement.legacy(modloc + "village/common/animals/horses_4"), 1));
        customPieces.add(new Pair<>(StructurePoolElement.legacy(modloc + "village/common/animals/horses_5"), 1));
        // Transform from function to jigsaw piece
        StructureTemplatePool.Projection placementBehaviour = StructureTemplatePool.Projection.RIGID;
        for(Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> pair : customPieces) {
            StructurePoolElement jigsawPiece = pair.getFirst().apply(placementBehaviour);
            keeperList.add(new Pair<>(jigsawPiece, pair.getSecond()));
        }
        // 1.16 moved jigsawManager.REGISTRY to JigsawPatternRegistry
        Pools.register(new StructureTemplatePool(animalsLoc, new ResourceLocation("empty"), keeperList));
        // I don't touch sheep so I can leave "village/common/sheep" alone
        // Likewise for "village/common/cats"
        // Also ignore the cows, pigs, and sheep in "village/common/butcher_animals"
    }
}
