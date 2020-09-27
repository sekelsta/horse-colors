package sekelsta.horse_colors.renderer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.genetics.IGeneticEntity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.genetics.HorseColorCalculator;

// Can't inherit from AbstractHorseRenderer because that uses HorseModel
@OnlyIn(Dist.CLIENT)
public class HorseGeneticRenderer extends MobRenderer<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>>
{
    protected void preRenderCallback(AbstractHorseGenetic horse, float partialTickTime) {
        float scale = horse.getProportionalScale();
        GlStateManager.scalef(scale, scale, scale);
        super.preRenderCallback(horse, partialTickTime);
    }

    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();

    public HorseGeneticRenderer(EntityRendererManager renderManager)
    {
        super(renderManager, new HorseGeneticModel<AbstractHorseGenetic>(), 0.75F);
        this.addLayer(new HorseArmorLayer(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call EntityRenderer.bindEntityTexture.
     */
    @Override
    public ResourceLocation getEntityTexture(AbstractHorseGenetic entity)
    {
        if (entity instanceof IGeneticEntity) {
            String s = ((IGeneticEntity)entity).getGenes().getTexture();
            ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);

            if (resourcelocation == null)
            {
                resourcelocation = new ResourceLocation(s);
                Minecraft.getInstance().getTextureManager().loadTexture(
                    resourcelocation, 
                    new CustomLayeredTexture(((IGeneticEntity)entity).getGenes().getVariantTexturePaths())
                );
                LAYERED_LOCATION_CACHE.put(s, resourcelocation);
            }

            return resourcelocation;
        }
        System.out.println("Trying to render an ineligible entity");
        return null;
    }
}
