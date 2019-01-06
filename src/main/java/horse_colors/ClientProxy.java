package felinoid.horse_colors;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.FMLCommonHandler;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.client.model.ModelHorse;

public class ClientProxy extends CommonProxy
{

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderers()
    {
        RenderHorseFelinoid horseRender = new RenderHorseFelinoid(Minecraft.getMinecraft().getRenderManager());
        RenderingRegistry.registerEntityRenderingHandler(EntityHorseFelinoid.class, horseRender);
	}

    @SideOnly(Side.CLIENT)
    @Override
    public void registerEventListeners()
    {
        HorseDebug hd = new HorseDebug();
        MinecraftForge.EVENT_BUS.register(hd);
    }
}
