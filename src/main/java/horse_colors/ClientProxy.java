package felinoid.horse_colors;

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

public class ClientProxy extends CommonProxy {

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderers() {
        RenderHorseFelinoid horseRender = new RenderHorseFelinoid(Minecraft.getMinecraft().getRenderManager());
        RenderingRegistry.registerEntityRenderingHandler(EntityHorseFelinoid.class, horseRender);
	}
}
