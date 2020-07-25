package sekelsta.horse_colors;

import java.util.Iterator;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.*;

public class HorseReplacer {
	public static void preInit() {}

	public static void init() {}

	@SubscribeEvent
	public static void replaceHorses(EntityJoinWorldEvent event)
    {
        // We don't want to replace subclasses of horses
        if (event.getEntity().getClass() == EntityHorse.class
            && !event.getWorld().isRemote 
            && HorseConfig.convertVanillaHorses())
        {
            EntityHorse horse = (EntityHorse)event.getEntity();
            if (!horse.getEntityData().hasKey("converted")) {
                HorseGeneticEntity newHorse = new HorseGeneticEntity(event.getWorld());
                newHorse.copyAbstractHorse(horse);
                // Spawn the new horse
                event.getWorld().spawnEntity(newHorse);
                // Don't convert the same horse twice
                horse.getEntityData().setBoolean("converted", true);
            }
            // Cancel the event regardless
            event.setCanceled(true);
        }
	}

}
