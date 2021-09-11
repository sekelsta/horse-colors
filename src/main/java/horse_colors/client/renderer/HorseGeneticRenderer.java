package sekelsta.horse_colors.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.genetics.IGeneticEntity;
import sekelsta.horse_colors.entity.genetics.HorseColorCalculator;

// Can't inherit from AbstractHorseRenderer because that uses HorseModel
@OnlyIn(Dist.CLIENT)
public class HorseGeneticRenderer extends MobRenderer<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>>
{
    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();
    public static final ModelLayerLocation EQUINE_LAYER = new ModelLayerLocation(new ResourceLocation(HorseColors.MODID, "equine"), "equine");

    public HorseGeneticRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new HorseGeneticModel<AbstractHorseGenetic>(renderManager.bakeLayer(EQUINE_LAYER)), 0.75F);
        this.addLayer(new HorseArmorLayer(this, renderManager.getModelSet()));
    }

    @Override
    protected void scale(AbstractHorseGenetic horse, PoseStack matrixStackIn, float partialTickTime) {
        float scale = horse.getProportionalAgeScale();
        matrixStackIn.scale(scale, scale, scale);
        this.shadowRadius = 0.75F * scale;
        super.scale(horse, matrixStackIn, partialTickTime);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call EntityRenderer.bindEntityTexture.
     */
    @Override
    public ResourceLocation getTextureLocation(AbstractHorseGenetic entity)
    {
        if (entity instanceof IGeneticEntity) {
            String s = ((IGeneticEntity)entity).getGenome().getTexture();
            ResourceLocation resourcelocation = LAYERED_LOCATION_CACHE.get(s);

            if (resourcelocation == null)
            {
                resourcelocation = new ResourceLocation(s);
                Minecraft.getInstance().getTextureManager().register(
                    resourcelocation, 
                    new CustomLayeredTexture(((IGeneticEntity)entity).getGenome().getTexturePaths())
                );
                LAYERED_LOCATION_CACHE.put(s, resourcelocation);
            }

            return resourcelocation;
        }
        System.out.println("Trying to render an ineligible entity");
        return null;
    }
}
