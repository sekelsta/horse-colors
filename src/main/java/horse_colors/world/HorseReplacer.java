package sekelsta.horse_colors.world;

import java.util.*;
import java.util.stream.Stream;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.*;

public class HorseReplacer {
    private static void loadDuringWorldGen(ServerLevel world, Entity entity) {
        world.addWorldGenChunkEntities(Stream.of(entity));
    }


    @SubscribeEvent
	public static void replaceHorses(EntityJoinWorldEvent event)
    {
        // We don't want to replace subclasses of horses
        if (event.getEntity().getClass() == Horse.class
            && !event.getWorld().isClientSide
            && HorseConfig.SPAWN.convertVanillaHorses.get())
        {
            Horse horse = (Horse)event.getEntity();
            if (!horse.getPersistentData().contains("converted")) {
                HorseGeneticEntity newHorse = ModEntities.HORSE_GENETIC.create(event.getWorld());
                newHorse.copyAbstractHorse(horse);
                // Spawn the new horse
                // Normally this is done by calling world.addEntity, which
                // makes sure to load the chunk first. However calling that
                // from chunk loading creates a deadlock. Minecraft's chunk 
                // loading code calls ServerWorld.loadFromChunk
                // instead, which assumes the chunk is already loaded.
                Level world = event.getWorld();
                if (world instanceof ServerLevel) {
                    loadDuringWorldGen((ServerLevel)world, newHorse);
                }
                // Don't convert the same horse twice
                horse.getPersistentData().putBoolean("converted", true);
            }
            // Cancel the event regardless
            event.setCanceled(true);
        }
	}
}
