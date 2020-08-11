package sekelsta.horse_colors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.math.RayTraceResult;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.IGeneticEntity;
import sekelsta.horse_colors.renderer.TextureLayer;

// Class for putting horse info on the debug screen
public class HorseDebug {
    // Determines when to print horse debug info on the screen
    public static boolean showDebug(EntityPlayer player)
    {
        if (!HorseConfig.enableDebugInfo())
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
        for (String stat : genome.listGenericChromosomes()) {
            String s = stat;
            s += ": " + genome.countBits(genome.getChromosome(stat));
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
        // Uncomment to show substats
        // addSubStats(genome, list);
        return list;
    }

    private void addSubStats(Genome genome, List<String> list) {
        for (String stat : genome.listStats()) {
            String s = stat;
            s += ": " + genome.getStatValue(stat);
            s += " (";
            int val = genome.getRawStat(stat);
            s += Genome.chrToStr(val);
            s += ")";
            list.add(s);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderOverlayEvent(RenderGameOverlayEvent.Text event)
    {
        // If the player is looking at a horse and all conditions are met, add 
        // genetic information about that horse to the debug screen

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (showDebug(player))
        {
            // Check if we're looking at a horse
            RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
            if (mouseOver != null
                && mouseOver.entityHit != null
                && mouseOver.entityHit instanceof IGeneticEntity)
            {
                // If so, print information about it to the debug screen
                IGeneticEntity entity = (IGeneticEntity)mouseOver.entityHit;
                // I thought I would need this to make everything fit on debug 
                // mode, but it fits if I make the GUI smaller
                // event.getRight().clear();
                for (String s : debugStatGenes(entity.getGenes())) {
                    event.getLeft().add(s);
                }
                if (entity instanceof EntityAgeable) {
                    event.getLeft().add("Growing age: " + ((EntityAgeable)entity).getGrowingAge());
                }
                if (entity instanceof AbstractHorseGenetic) {
                    event.getLeft().add("Display age: " + ((AbstractHorseGenetic)entity).getDisplayAge());
                    event.getLeft().add("Pregnant since: " + ((AbstractHorseGenetic)entity).getPregnancyStart());
                }
                for (TextureLayer l : entity.getGenes().getVariantTexturePaths()) {
                    if (l != null) {
                        event.getLeft().add(l.toString());
                    }
                }
                for (String s : debugNamedGenes(entity.getGenes())) {
                    event.getRight().add(s);
                }
            }
        }
    }
}
