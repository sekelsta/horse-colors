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
        if (event.getEntity() instanceof EntityHorse
            && HorseConfig.blockVanillaHorseSpawns)
        {
            if (!event.getWorld().isRemote && HorseConfig.convertVanillaHorses)
            {
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
