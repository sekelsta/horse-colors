package sekelsta.horse_colors;

import java.util.Iterator;

import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;

public class HorseReplacer {
	public static void preInit() {}

	public static void init() {}

    //Removes initial spawns
	@SubscribeEvent
	public void onEntitySpawn(WorldEvent.PotentialSpawns event) {
		for(Iterator<SpawnListEntry> iter = event.getList().iterator(); iter.hasNext(); )
		{
			String className = iter.next().entityType.getClass().getName();
            if(HorseConfig.COMMON.blockVanillaHorseSpawns.get() 
               && className.equals(EntityType.HORSE.getClass().getName()))
            {
				iter.remove();
            }
		}
	}

	@SubscribeEvent
	public static void replaceHorses(EntityJoinWorldEvent event)
    {
        // We don't want to replace subclasses of horses
        if (event.getEntity().getClass() == HorseEntity.class
            && !event.getWorld().isRemote 
            && HorseConfig.COMMON.convertVanillaHorses.get())
        {
            HorseEntity horse = (HorseEntity)event.getEntity();
            if (!horse.getPersistantData().contains("converted")) {
                HorseGeneticEntity newHorse = ModEntities.HORSE_GENETIC.spawn(event.getWorld(), null, null, null, new BlockPos(horse), SpawnReason.CONVERSION, false, false);
                newHorse.copyAbstractHorse(horse);
                newHorse.randomize();
                
                // Don't convert the same horse twice
                horse.getPersistantData().putBoolean("converted", true);
            }
            // Cancel the event regardless
            event.setCanceled(true);
        }
	}

}
