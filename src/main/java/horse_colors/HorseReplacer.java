package felinoid.horse_colors;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class HorseReplacer {
	public static void preInit() {}

	public static void init() {}

	
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
                event.getWorld().spawnEntity(newHorse);
            }
            event.setCanceled(true);
        }
	}

}
