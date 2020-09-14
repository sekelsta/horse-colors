package sekelsta.horse_colors.world;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.List;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.ModEntities;
import sekelsta.horse_colors.HorseColors;

@Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
public class Spawns {

    @SubscribeEvent
    public static void onLoadComplete(net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent e) {
        // This needs to happen after the config is read
        changeVillageAnimals();
        // These need to happen after the config file is read and vanilla horse spawns are added
        editSpawnTable();
    }

    //Removes initial vanilla horse spawns
    public static void editSpawnTable() {
        int horsePlainsWeight = (int)Math.round(5 * HorseConfig.SPAWN.horseSpawnMultiplier.get());
        Spawners horsePlainsSpawner = new Spawners(ModEntities.HORSE_GENETIC, horsePlainsWeight, 2, 6);
        int horseSavannaWeight = (int)Math.round(1 * HorseConfig.SPAWN.horseSpawnMultiplier.get());
        Spawners horseSavannaSpawner = new Spawners(ModEntities.HORSE_GENETIC, horseSavannaWeight, 2, 6);
        int donkeyWeight = (int)Math.round(1 * HorseConfig.SPAWN.donkeySpawnMultiplier.get());
        // It seems 1.16.2 has increased donkey max herd size in savannas from 1 to 3, to match the plains
        Spawners donkeySpawner = new Spawners(ModEntities.DONKEY_GENETIC, donkeyWeight, 1, 3);

        Collection<Biome> allBiomes = ForgeRegistries.BIOMES.getValues();
        for (Biome biome : allBiomes) {
            // Get mob spawn info for the biome
            MobSpawnInfo mobSpawnInfo = biome.func_242433_b();
            // Get the creature spawn list
            List<Spawners> originalSpawns = mobSpawnInfo.func_242559_a(EntityClassification.CREATURE);
            // Copy everything that should get kept to this list
            List<Spawners> editedSpawns = new ArrayList(originalSpawns);
            for (Spawners entry : originalSpawns) {
                boolean keep = true;
                // field_242588_c is the entity type
                if (entry.field_242588_c == EntityType.HORSE && HorseConfig.SPAWN.blockVanillaHorseSpawns.get()) {
                    HorseColors.logger.debug("Removing vanilla horse spawn: " + entry + " from biome " + biome);
                    keep = false;
                }
                else if (entry.field_242588_c == EntityType.DONKEY && HorseConfig.SPAWN.blockVanillaDonkeySpawns.get()) {
                    HorseColors.logger.debug("Removing vanilla donkey spawn: " + entry + " from biome " + biome);
                    keep = false;
                }

                if (keep) {
                    editedSpawns.add(entry);
                }
            }

            if (biome.getCategory() == Biome.Category.PLAINS) {
                editedSpawns.add(horsePlainsSpawner);
                editedSpawns.add(donkeySpawner);
            }
            else if (biome.getCategory() == Biome.Category.SAVANNA) {
                editedSpawns.add(horseSavannaSpawner);
                editedSpawns.add(donkeySpawner);
            }

            // Make a new MobSpawnInfo using the new spawner list
            // For other entity classifications, copy over the existing values
            MobSpawnInfo.Builder builder = new MobSpawnInfo.Builder();
            for (EntityClassification classification : EntityClassification.values()) {
                List<Spawners> spawners;
                if (classification == EntityClassification.CREATURE) {
                    spawners = editedSpawns;
                }
                else {
                    spawners = mobSpawnInfo.func_242559_a(classification);
                }
                for (Spawners mobSpawn : spawners) {
                    builder.func_242575_a(classification, mobSpawn);
                }
            }
            // Copy over other fields
            builder.func_242572_a(mobSpawnInfo.func_242557_a());
            if (mobSpawnInfo.func_242562_b()) {
                builder.func_242571_a();
            }
            // Build
            MobSpawnInfo finalSpawnInfo = builder.func_242577_b();
            // Set the builder for the biome
            ObfuscationReflectionHelper.setPrivateValue(Biome.class, biome, finalSpawnInfo, "field_242425_l");
        }

    }

    private static boolean isVanillaVillageHorsePiece(SingleJigsawPiece piece) {
        return piece.toString().contains("minecraft:village/common/animals/horses");
    }

    private static boolean keepJigsawPair(Pair<JigsawPiece, Integer> pair) {
        if (!HorseConfig.SPAWN.blockVanillaHorseSpawns.get()) {
            return true;
        }
        JigsawPiece piece = pair.getFirst();
        if (piece instanceof SingleJigsawPiece) {
            return !isVanillaVillageHorsePiece((SingleJigsawPiece)piece);
        }
        // This code normally won't be reached
        return true;
    }

    public static void changeVillageAnimals() {
        // Force the static block to run
        PlainsVillagePools.init();
        ResourceLocation animalsLoc = new ResourceLocation("village/common/animals");
        java.util.Optional<JigsawPattern> animalsOpt = WorldGenRegistries.field_243656_h.func_241873_b(animalsLoc);
        if (!animalsOpt.isPresent()) {
            System.err.println("Trying to overwrite village spawns too soon");
            return;
        }
        JigsawPattern animals = animalsOpt.get();
        List<Pair<JigsawPiece, Integer>> vanillaList =  ObfuscationReflectionHelper.getPrivateValue(JigsawPattern.class, animals, "field_214952_d");
        List<Pair<JigsawPiece, Integer>> keeperList = new ArrayList<>();
        for (Pair<JigsawPiece, Integer> p : vanillaList) {
            if (keepJigsawPair(p)) {
                keeperList.add(p);
            }
        }
        // Add my own pieces
        List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> customPieces = new ArrayList<>();
        String modloc = HorseColors.MODID + ":";
        customPieces.add(new Pair<>(JigsawPiece.func_242849_a(modloc + "village/common/animals/horses_1"), 1));
        customPieces.add(new Pair<>(JigsawPiece.func_242849_a(modloc + "village/common/animals/horses_2"), 1));
        customPieces.add(new Pair<>(JigsawPiece.func_242849_a(modloc + "village/common/animals/horses_3"), 1));
        customPieces.add(new Pair<>(JigsawPiece.func_242849_a(modloc + "village/common/animals/horses_4"), 1));
        customPieces.add(new Pair<>(JigsawPiece.func_242849_a(modloc + "village/common/animals/horses_5"), 1));
        // Transform from function to jigsaw piece
        JigsawPattern.PlacementBehaviour placementBehaviour = JigsawPattern.PlacementBehaviour.RIGID;
        for(Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer> pair : customPieces) {
            JigsawPiece jigsawPiece = pair.getFirst().apply(placementBehaviour);
            keeperList.add(new Pair<>(jigsawPiece, pair.getSecond()));
        }
        // 1.16 moved jigsawManager.REGISTRY to JigsawPatternRegistry
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(animalsLoc, new ResourceLocation("empty"), keeperList));
        // I don't touch sheep so I can leave "village/common/sheep" alone
        // Likewise for "village/common/cats"
        // Also ignore the cows, pigs, and sheep in "village/common/butcher_animals"
    }
}
