package sekelsta.horse_colors.client.renderer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.util.HorseArmorer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import sekelsta.horse_colors.HorseColors;

@OnlyIn(Dist.CLIENT)
public class HorseSaddleLayer extends RenderLayer<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>> {
    private final HorseGeneticModel<AbstractHorseGenetic> horseModel;

    public HorseSaddleLayer(RenderLayerParent<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>> model, EntityModelSet modelSet) {
        super(model);
        this.horseModel = new HorseGeneticModel<>(modelSet.bakeLayer(HorseGeneticRenderer.EQUINE_LAYER));
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int packedLight, AbstractHorseGenetic entityIn, float f1, float f2, float f3, float f4, float f5, float f6) {
        ResourceLocation textureLocation = HorseArmorer.getSaddleTexture(entityIn);
        int color = HorseArmorer.getSaddleTint(entityIn);
        if (textureLocation != null) {
            this.getParentModel().copyPropertiesTo(this.horseModel);
            this.horseModel.prepareMobModel(entityIn, f1, f2, f3);
            this.horseModel.setupAnim(entityIn, f1, f2, f4, f5, f6);
            float r = (float)(color >> 16 & 255) / 255.0F;
            float g = (float)(color >> 8 & 255) / 255.0F;
            float b = (float)(color & 255) / 255.0F;

            VertexConsumer ivertexbuilder;
            if (HorseArmorer.isSaddleEnchanted(entityIn)) {
                ivertexbuilder = VertexMultiConsumer.create(renderTypeBuffer.getBuffer(RenderType.armorEntityGlint()), renderTypeBuffer.getBuffer(RenderType.armorCutoutNoCull(textureLocation)));
            }
            else {
                ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(textureLocation));
            }
            this.horseModel.renderToBuffer(matrixStack, ivertexbuilder, packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        }
    }


    public boolean shouldCombineTextures() {
        return false;
    }
}
