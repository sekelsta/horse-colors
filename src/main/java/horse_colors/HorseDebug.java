package sekelsta.horse_colors;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.IGeneticEntity;

// Class for putting horse info on the debug screen
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "horse_colors", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HorseDebug {
    // Determines when to print horse debug info on the screen
    public static boolean showDebug(PlayerEntity player)
    {
        if (!HorseConfig.COMMON.horseDebugInfo.get())
        {
            return false;
        }
        ItemStack itemStack = player.getHeldItemOffhand();
        return itemStack != null 
            && itemStack.getItem() == Items.STICK;
    }

    public static ArrayList<String> debugNamedGenes(Genome genome) {
        ArrayList<String> list = new ArrayList<String>();
        for (String gene : genome.listGenes()) {
            String s = gene + ": ";
            s += genome.getAllele(gene, 0) + ", ";
            s += genome.getAllele(gene, 1);
            list.add(s);
        }
        return list;
    }

    public static ArrayList<String> debugStatGenes(Genome genome) {
        ArrayList<String> list = new ArrayList<String>();
        for (String stat : genome.listStats()) {
            String s = stat;
            s += ": " + genome.getStat(stat);
            s += " (";
            int val = genome.getChromosome(stat);
            for (int i = 16; i >0; i--) {
                s += (val >>> (2 * i - 1)) & 1;
                s += (val >>> (2 * i - 2)) & 1;
                if (i > 1) {
                    s += " ";
                }
            }
            s += ")";
            list.add(s);
        }
        return list;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderOverlayEvent(RenderGameOverlayEvent.Text event)
    {
        // If the player is looking at a horse and all conditions are met, add 
        // genetic information about that horse to the debug screen

        PlayerEntity player = Minecraft.getInstance().player;
        if (showDebug(player))
        {
            // Check if we're looking at a horse
            RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
            if (mouseOver != null
                && mouseOver instanceof EntityRayTraceResult
                && ((EntityRayTraceResult)mouseOver).getEntity() != null
                //&& Minecraft.getInstance().objectMouseOver.getType == RayTraceResult.Type.ENTITY
                && ((EntityRayTraceResult)mouseOver).getEntity() instanceof IGeneticEntity)
            {
                // If so, print information about it to the debug screen
                IGeneticEntity horse = (IGeneticEntity)((EntityRayTraceResult)mouseOver).getEntity();
                // I thought I would need this to make everything fit on debug 
                // mode, but it fits if I make the GUI smaller
                // event.getRight().clear();
                for (String s : debugStatGenes(horse.getGenes())) {
                    event.getLeft().add(s);
                }
                for (String s : debugNamedGenes(horse.getGenes())) {
                    event.getRight().add(s);
                }
            }
        }
    }
}
