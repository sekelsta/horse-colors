package sekelsta.horse_colors.client.renderer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.util.HorseArmorer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import sekelsta.horse_colors.HorseColors;

@OnlyIn(Dist.CLIENT)
public class HorseArmorLayer extends RenderLayer<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>> {
    public static final ModelLayerLocation HORSE_ARMOR_LAYER = new ModelLayerLocation(new ResourceLocation(HorseColors.MODID, "horse_armor"), "horse_armor");
    private final HorseGeneticModel<AbstractHorseGenetic> horseModel;

    public HorseArmorLayer(RenderLayerParent<AbstractHorseGenetic, HorseGeneticModel<AbstractHorseGenetic>> model, EntityModelSet modelSet) {
        super(model);
        this.horseModel = new HorseGeneticModel<>(modelSet.bakeLayer(HORSE_ARMOR_LAYER));
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int p_225628_3_, AbstractHorseGenetic entityIn, float f1, float f2, float f3, float f4, float f5, float f6) {
        ItemStack itemstack = entityIn.getArmor();
        Item armor = itemstack.getItem();
        ResourceLocation textureLocation = HorseArmorer.getTexture(armor);
        if (textureLocation != null) {
            this.getParentModel().copyPropertiesTo(this.horseModel);
            this.horseModel.prepareMobModel(entityIn, f1, f2, f3);
            this.horseModel.setupAnim(entityIn, f1, f2, f4, f5, f6);
            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F;
            if (armor instanceof DyeableHorseArmorItem) {
                int color = ((DyeableHorseArmorItem)armor).getColor(itemstack);
                r = (float)(color >> 16 & 255) / 255.0F;
                g = (float)(color >> 8 & 255) / 255.0F;
                b = (float)(color & 255) / 255.0F;
            }
            else if (armor instanceof BlockItem) {
                BlockItem blockItem = (BlockItem)armor;
                if (blockItem.getBlock() instanceof WoolCarpetBlock) {
                    float[] colors = ((WoolCarpetBlock)(blockItem.getBlock())).getColor().getTextureDiffuseColors();
                    r = colors[0];
                    g = colors[1];
                    b = colors[2];
                }
            }

            VertexConsumer ivertexbuilder;
            if (itemstack.hasFoil()) {
                ivertexbuilder = VertexMultiConsumer.create(renderTypeBuffer.getBuffer(RenderType.armorEntityGlint()), renderTypeBuffer.getBuffer(RenderType.armorCutoutNoCull(textureLocation)));
            }
            else {
                ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.entityCutoutNoCull(textureLocation));
            }
            this.horseModel.renderToBuffer(matrixStack, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        }
    }


    public boolean shouldCombineTextures() {
        return false;
    }
}
