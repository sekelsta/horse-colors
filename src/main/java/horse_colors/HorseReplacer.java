package sekelsta.horse_colors;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.world.biome.Biome;
//import net.minecraftforge.event.world.WorldEvent;
import net.minecraft.entity.EntityClassification;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.ChunkStatus;

public class HorseReplacer {
    public static void preInit() {}

    public static void init() {}

    //Removes initial spawns
    public static void editSpawnTable() {
        System.out.println("Going through spawn list");
        Set<Biome> allBiomes = Biome.BIOMES;
        for (Biome biome : allBiomes) {
                List<Biome.SpawnListEntry> spawns = biome.getSpawns(EntityClassification.CREATURE);
                if (spawns.isEmpty()) {
                    continue;
                }
                ArrayList<Biome.SpawnListEntry> horseSpawns = new ArrayList<Biome.SpawnListEntry>();
                for (Biome.SpawnListEntry entry : spawns) {
                    if (entry.entityType == EntityType.HORSE) {
                        horseSpawns.add(entry);
                    }
                }
                if (!horseSpawns.isEmpty()) {
                    System.out.println(horseSpawns);
                }
                if (HorseConfig.COMMON.blockVanillaHorseSpawns.get()) {
                    for (Biome.SpawnListEntry horseSpawn : horseSpawns) {
                        spawns.remove(horseSpawn);
                    }
                }
        }

    }

}


