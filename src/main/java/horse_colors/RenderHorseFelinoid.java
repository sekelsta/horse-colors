package felinoid.horse_colors;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.Render;

@SideOnly(Side.CLIENT)
public class RenderHorseFelinoid extends RenderLiving<EntityHorseFelinoid>
{
    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.<String, ResourceLocation>newHashMap();

    public RenderHorseFelinoid(RenderManager renderManager)
    {
        super(renderManager, new ModelHorse(), 0.75F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(EntityHorseFelinoid entity)
    {
        String s = entity.getHorseTexture();
        ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s);
            Minecraft.getMinecraft().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(entity.getVariantTexturePaths()));
            LAYERED_LOCATION_CACHE.put(s, resourcelocation);
        }

        return resourcelocation;
    }
}
