package sekelsta.horse_colors.world;

import java.util.*;
import java.util.stream.Stream;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;

import sekelsta.horse_colors.HorseConfig;
import sekelsta.horse_colors.entity.*;

public class HorseReplacer {
    private static void loadDuringWorldGen(ServerLevel world, Entity entity) {
        world.addWorldGenChunkEntities(Stream.of(entity));
    }


    @SubscribeEvent
	public static void replaceHorses(EntityJoinLevelEvent event)
    {
        if (event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!HorseConfig.shouldConvert(entity)) {
            return;
        }
        if (entity.getPersistentData().contains("converted")) {
            event.setCanceled(true);
            return;
        }

        AbstractHorseGenetic newHorse = null;
        if (entity.getClass() == Horse.class)
        {
            newHorse = ModEntities.HORSE_GENETIC.get().create(event.getLevel());
        }
        else if (entity.getClass() == Donkey.class)
        {
            newHorse = ModEntities.DONKEY_GENETIC.get().create(event.getLevel());
        }
        else if (entity.getClass() == Mule.class)
        {
            newHorse = ModEntities.MULE_GENETIC.get().create(event.getLevel());
        }
        newHorse.copyAbstractHorse((AbstractHorse)entity);

        // Spawn the new horse
        // Normally this is done by calling world.addEntity, which
        // makes sure to load the chunk first. However calling that
        // from chunk loading creates a deadlock. Minecraft's chunk 
        // loading code calls ServerWorld.loadFromChunk
        // instead, which assumes the chunk is already loaded.
        Level world = event.getLevel();
        if (world instanceof ServerLevel) {
            loadDuringWorldGen((ServerLevel)world, newHorse);
        }

        // Don't convert the same horse twice
        entity.getPersistentData().putBoolean("converted", true);
        // Cancel the event regardless
        event.setCanceled(true);
	}
}
