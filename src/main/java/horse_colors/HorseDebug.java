package felinoid.horse_colors;

import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Class for putting horse info on the debug screen
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "horse_colors", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HorseDebug {
    // Determines when to print horse debug info on the screen
    public static boolean showDebug(EntityPlayer player)
    {
        if (!HorseConfig.COMMON.horseDebugInfo.get())
        {
            return false;
        }
        ItemStack itemStack = player.getHeldItemOffhand();
        return itemStack != null 
            && itemStack.getItem() == Items.STICK;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderOverlayEvent(RenderGameOverlayEvent.Text event)
    {
        // If the player is looking at a horse and all conditions are met, add 
        // genetic information about that horse to the debug screen

        EntityPlayer player = Minecraft.getInstance().player;
        if (showDebug(player))
        {
            // Check if we're looking at a horse
            if (Minecraft.getInstance().objectMouseOver != null
                && Minecraft.getInstance().objectMouseOver.entity != null
                && Minecraft.getInstance().objectMouseOver.entity instanceof EntityHorseFelinoid)
            {
                // If so, print information about it to the debug screen
                EntityHorseFelinoid horse = (EntityHorseFelinoid)Minecraft.getInstance().objectMouseOver.entity;
                // I thought I would need this to make everything fit on debug 
                // mode, but it fits if I make the GUI smaller
                // event.getRight().clear();
                for (String gene : EntityHorseFelinoid.genes)
                {
                    event.getRight().add(gene + ": " + horse.getPhenotype(gene) 
                        + " (" + horse.getAllele(gene, 1) + ", "
                        + horse.getAllele(gene, 0) + ")");
                }
                event.getLeft().add("speed: " + horse.getStat("speed") + "-"
                    + Integer.toBinaryString(horse.getHorseVariant("speed")));
                event.getLeft().add("health: "  + horse.getStat("health") + "-"
                    + Integer.toBinaryString(horse.getHorseVariant("health")));
                event.getLeft().add("jump: "  + horse.getStat("jump") + "-"
                    + Integer.toBinaryString(horse.getHorseVariant("jump")));
                event.getLeft().add("random: " 
                    + Integer.toHexString(horse.getHorseVariant("random")));
            }
        }
    }
}
