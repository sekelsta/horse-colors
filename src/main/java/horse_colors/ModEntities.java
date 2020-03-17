package felinoid.horse_colors;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.*;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary; 
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.entity.passive.EntityHorse;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class ModEntities {
    private static int ID = 0;

    public static EntityEntry HORSE_GENETIC;

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        // Horse
        String horse_name = "horse_felinoid";
		HORSE_GENETIC = EntityEntryBuilder.create()
            .entity(EntityHorseFelinoid.class)
            // Last parameter is network ID, which needs to be unique per mod.
            .id(new ResourceLocation(HorseColors.MODID, horse_name), ID++)
            .name(horse_name)
            .egg(0x7F4320, 0x110E0D)
            .tracker(64, 2, false)
            .spawn(EnumCreatureType.CREATURE, HorseConfig.spawnWeight, 
                HorseConfig.minHerdSize, HorseConfig.maxHerdSize, 
                BiomeDictionary.getBiomes(BiomeDictionary.Type.PLAINS))
            .spawn(EnumCreatureType.CREATURE, HorseConfig.spawnWeight, 
                HorseConfig.minHerdSize, HorseConfig.maxHerdSize, 
                BiomeDictionary.getBiomes(BiomeDictionary.Type.SAVANNA))
            .build();
        event.getRegistry().register(HORSE_GENETIC);
	}

    //Removes initial spawns
	public static void removeVanillaHorseSpawns() {
        if (!HorseConfig.blockVanillaHorseSpawns) {
            return;
        }
        List<Biome> horseBiomes = ImmutableList.of(Biomes.PLAINS, Biomes.SAVANNA,
                                Biomes.SAVANNA_PLATEAU, Biomes.MUTATED_SAVANNA, 
                                Biomes.MUTATED_SAVANNA_ROCK);
        for (Biome biome : horseBiomes) {
                List<Biome.SpawnListEntry> spawns = biome.getSpawnableList(EnumCreatureType.CREATURE);
                if (spawns.isEmpty()) {
                    continue;
                }
                ArrayList<Biome.SpawnListEntry> horseSpawns = new ArrayList<Biome.SpawnListEntry>();
                for (Biome.SpawnListEntry entry : spawns) {
                    if (entry.entityClass == EntityHorse.class) {
                        horseSpawns.add(entry);
                    }
                }
                if (!horseSpawns.isEmpty()) {
                    System.out.println(horseSpawns);
                }
                for (Biome.SpawnListEntry horseSpawn : horseSpawns) {
                    spawns.remove(horseSpawn);
                }
        }
	}

}
