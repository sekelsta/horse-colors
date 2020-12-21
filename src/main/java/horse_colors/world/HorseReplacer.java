package sekelsta.horse_colors.world;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.*;

public class HorseReplacer {

    @SubscribeEvent
	public static void replaceHorses(EntityJoinWorldEvent event)
    {
        // We don't want to replace subclasses of horses
        if (event.getEntity().getClass() == HorseEntity.class
            && !event.getWorld().isRemote
            && HorseConfig.SPAWN.convertVanillaHorses.get())
        {
            HorseEntity horse = (HorseEntity)event.getEntity();
            if (!horse.getPersistentData().contains("converted")) {
                HorseGeneticEntity newHorse = ModEntities.HORSE_GENETIC.create(event.getWorld());
                newHorse.copyAbstractHorse(horse);
                // Spawn the new horse
                // Normally this is done by calling world.addEntity, which
                // makes sure to load the chunk first. However calling that
                // from chunk loading creates a deadlock. Minecraft's chunk 
                // loading code calls ServerWorld.addEntityIfNotDuplicate
                // instead, which assumes the chunk is already loaded.
                World world = event.getWorld();
                if (world instanceof ServerWorld) {
                    ((ServerWorld)world).addEntityIfNotDuplicate(newHorse);
                }
                // Don't convert the same horse twice
                horse.getPersistentData().putBoolean("converted", true);
            }
            // Cancel the event regardless
            event.setCanceled(true);
        }
	}
}
