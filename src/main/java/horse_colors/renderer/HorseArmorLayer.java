package sekelsta.horse_colors.renderer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.util.HorseArmorer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

@OnlyIn(Dist.CLIENT)
public class HorseArmorLayer extends LayerRenderer<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>> {
    private final HorseGeneticModel<AbstractHorseGenetic> horseModel = new HorseGeneticModel<>(0.1F);

    public HorseArmorLayer(IEntityRenderer<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>> model) {
       super(model);
    }

    @Override
    // Render function
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, AbstractHorseGenetic entityIn, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (!(entityIn instanceof HorseGeneticEntity)) {
            return;
        }
        HorseGeneticEntity horse = (HorseGeneticEntity)entityIn;
       ItemStack itemstack = horse.getHorseArmor();
       if (itemstack.getItem() instanceof HorseArmorItem) {
           HorseArmorItem horsearmoritem = (HorseArmorItem)itemstack.getItem();
           this.getEntityModel().copyModelAttributesTo(this.horseModel);
           this.horseModel.setLivingAnimations(horse, p_225628_5_, p_225628_6_, p_225628_7_);
           this.horseModel.setRotationAngles(horse, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
           float f;
           float f1;
           float f2;
           if (horsearmoritem instanceof DyeableHorseArmorItem) {
               int i = ((DyeableHorseArmorItem)horsearmoritem).getColor(itemstack);
               f = (float)(i >> 16 & 255) / 255.0F;
               f1 = (float)(i >> 8 & 255) / 255.0F;
               f2 = (float)(i & 255) / 255.0F;
           } else {
               f = 1.0F;
               f1 = 1.0F;
               f2 = 1.0F;
           }

            IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull(HorseArmorer.getTexture(horsearmoritem)));
            this.horseModel.render(matrixStack, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, f, f1, f2, 1.0F);
        }
    }


    public boolean shouldCombineTextures() {
        return false;
    }
}