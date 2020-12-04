package sekelsta.client.horse_colors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.AgeableEntity;
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

import sekelsta.horse_colors.client.renderer.TextureLayer;
import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.genetics.Genome;
import sekelsta.horse_colors.entity.genetics.IGeneticEntity;

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
        return showBasicDebug(player) || showGeneDebug(player);
    }

    public static boolean showBasicDebug(PlayerEntity player) {
        ItemStack itemStack = player.getHeldItemOffhand();
        if (itemStack != null && itemStack.getItem() == Items.STICK) {
            return true;
        }
        ItemStack inHand = player.getHeldItemMainhand();
        return inHand != null && inHand.getItem() == Items.STICK;
    }

    public static boolean showGeneDebug(PlayerEntity player) {
        ItemStack itemStack = player.getHeldItemOffhand();
        if (itemStack != null && itemStack.getItem() == Items.DEBUG_STICK) {
            return true;
        }
        ItemStack inHand = player.getHeldItemMainhand();
        return inHand != null && inHand.getItem() == Items.DEBUG_STICK;
    }

    public static ArrayList<String> debugGenes(Genome genome) {
        ArrayList<String> list = new ArrayList<String>();
        for (String gene : genome.listGenes()) {
            String s = gene + ": ";
            s += genome.getAllele(gene, 0) + ", ";
            s += genome.getAllele(gene, 1);
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
        if (!showDebug(player))
        {
            return;
        }

        // Check if we're looking at a horse
        RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
        if (mouseOver != null
            && mouseOver instanceof EntityRayTraceResult
            && ((EntityRayTraceResult)mouseOver).getEntity() != null
            //&& Minecraft.getInstance().objectMouseOver.getType == RayTraceResult.Type.ENTITY
            && ((EntityRayTraceResult)mouseOver).getEntity() instanceof IGeneticEntity)
        {
            // If so, print information about it to the debug screen
            IGeneticEntity entity = (IGeneticEntity)((EntityRayTraceResult)mouseOver).getEntity();
            if (showBasicDebug(player) && entity instanceof AgeableEntity) {
                event.getLeft().add("Growing age: " + ((AgeableEntity)entity).getGrowingAge());
            }
            if (showBasicDebug(player) && entity instanceof AbstractHorseGenetic) {
                event.getLeft().add("Display age: " + ((AbstractHorseGenetic)entity).getDisplayAge());
                event.getLeft().add("Pregnant since: " + ((AbstractHorseGenetic)entity).getPregnancyStart());
            }
            if (showBasicDebug(player)) {
                event.getLeft().add(entity.getGenome().getTexture());
                event.getLeft().add("Layers:");
                for (String s : entity.getGenome().getTexturePaths().getDebugStrings()) {
                    event.getLeft().add(s);
                }
            }
            if (showGeneDebug(player)) {
                List<String> strings = debugGenes(entity.getGenome());
                for (int i = 0; i < strings.size() / 2; ++i) {
                    event.getRight().add(strings.get(i));
                }
                for (int i = strings.size() / 2; i < strings.size(); ++i) {
                    event.getLeft().add(strings.get(i));
                }
            }
        }
    }
}
