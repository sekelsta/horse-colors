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
            && HorseConfig.blockVanillaHorseSpawns)
        {
            if (!event.getWorld().isRemote && HorseConfig.convertVanillaHorses)
            {
                // TODO: also copy over tameness and equipment
                EntityHorse horse = (EntityHorse)event.getEntity();
                EntityHorseFelinoid newHorse = new EntityHorseFelinoid(event.getWorld());
                newHorse.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, event.getEntity().rotationYaw, event.getEntity().rotationPitch);
                newHorse.randomize();
                // Need to set tamed
                // Need to transfer inventory
                event.getWorld().spawnEntity(newHorse);
                // This will remove it from any save file it might be part of
                horse.setDead();
            }
            event.setCanceled(true);
        }
	}

}
