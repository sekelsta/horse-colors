package felinoid.horse_colors;

import java.util.Iterator;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HorseReplacer {
	public static void preInit() {}

	public static void init() {}

    //Removes initial spawns
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onEntitySpawn(WorldEvent.PotentialSpawns event) {
		for(Iterator<SpawnListEntry> iter = event.getList().iterator(); iter.hasNext(); )
		{
			String className = iter.next().entityClass.getName();
            if(HorseConfig.blockVanillaHorseSpawns && className.equals("net.minecraft.entity.passive.EntityHorse"))
            {
				iter.remove();
            }
		}
	}

	@SubscribeEvent
	public static void replaceHorses(EntityJoinWorldEvent event)
    {
        // We don't want to replace subclasses of horses
        if (event.getEntity().getClass() == EntityHorse.class
            && HorseConfig.blockVanillaHorseSpawns 
            && !event.getWorld().isRemote 
            && HorseConfig.convertVanillaHorses)
        {
            EntityHorse horse = (EntityHorse)event.getEntity();
            EntityHorseFelinoid newHorse = new EntityHorseFelinoid(event.getWorld());
            newHorse.copyAbstractHorse(horse);
            newHorse.randomize();
            // Spawn the new horse
            event.getWorld().spawnEntity(newHorse);
            // This will remove it from any save file it might be part of
            horse.setDead();
            // And cancel the event just for good measure. I'm not sure this
            // is actaully necessary anymore.
            event.setCanceled(true);
        }
	}

}
