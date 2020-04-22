package sekelsta.horse_colors.renderer;

import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.util.HorseArmorer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.CarpetBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

@OnlyIn(Dist.CLIENT)
public class HorseArmorLayer extends LayerRenderer<AbstractHorseEntity, HorseGeneticModel<AbstractHorseEntity>> {
    private final HorseGeneticModel<AbstractHorseEntity> horseModel = new HorseGeneticModel<>(0.1F);

    public HorseArmorLayer(IEntityRenderer<AbstractHorseEntity, HorseGeneticModel<AbstractHorseEntity>> model) {
       super(model);
    }

    @Override
    // Render function
    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, AbstractHorseEntity entityIn, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (!(entityIn instanceof HorseGeneticEntity)) {
            return;
        }
        HorseGeneticEntity horse = (HorseGeneticEntity)entityIn;
        ItemStack itemstack = horse.getHorseArmor();
        Item armor = itemstack.getItem();
        ResourceLocation textureLocation = HorseArmorer.getTexture(armor);
        if (textureLocation != null) {
            this.getEntityModel().copyModelAttributesTo(this.horseModel);
            this.horseModel.setLivingAnimations(horse, p_225628_5_, p_225628_6_, p_225628_7_);
            this.horseModel.setRotationAngles(horse, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
            float r;
            float g;
            float b;
            int color = 0xffffff;
            if (armor instanceof DyeableHorseArmorItem) {
                color = ((DyeableHorseArmorItem)armor).getColor(itemstack);
            } 
            else if (armor instanceof BlockItem) {
                BlockItem blockItem = (BlockItem)armor;
                if (blockItem.getBlock() instanceof CarpetBlock) {
                    color = ((CarpetBlock)(blockItem.getBlock())).getColor().getColorValue();
                }
            }
            if (color != 0xffffff) {
                r = (float)(color >> 16 & 255) / 255.0F;
                g = (float)(color >> 8 & 255) / 255.0F;
                b = (float)(color & 255) / 255.0F;
            }
            else {
               r = 1.0F;
               g = 1.0F;
               b = 1.0F;
            }

            IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull(textureLocation));
            this.horseModel.render(matrixStack, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        }
    }


    public boolean shouldCombineTextures() {
        return false;
    }
}
