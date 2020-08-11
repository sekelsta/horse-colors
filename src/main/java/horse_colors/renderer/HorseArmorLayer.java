package sekelsta.horse_colors.renderer;

import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.HorseGeneticEntity;
import sekelsta.horse_colors.util.HorseArmorer;

import net.minecraft.block.BlockCarpet;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
//import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class HorseArmorLayer implements LayerRenderer<AbstractHorseGenetic> {
    private final HorseGeneticModel horseModel = new HorseGeneticModel(0.1f);
    private final HorseGeneticRenderer renderer;

    public HorseArmorLayer(HorseGeneticRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    // Render function
    public void doRenderLayer(AbstractHorseGenetic entityIn, float limbSwing, float limbSwingAmount, float partialTickTime, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!(entityIn instanceof HorseGeneticEntity)) {
            return;
        }
        HorseGeneticEntity horse = (HorseGeneticEntity)entityIn;
        ItemStack itemstack = horse.getHorseArmor();
        Item armor = itemstack.getItem();
        ResourceLocation textureLocation = HorseArmorer.getTexture(horse, itemstack);
        if (textureLocation != null) {
            this.horseModel.setModelAttributes(this.renderer.getMainModel());
            this.horseModel.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTickTime);
            this.renderer.bindTexture(textureLocation);
            float r = 1;
            float g = 1;
            float b = 1;
/*
            if (armor instanceof DyeableHorseArmorItem) {
                color = ((DyeableHorseArmorItem)armor).getColor(itemstack);
                r = (float)(color >> 16 & 255) / 255.0F;
                g = (float)(color >> 8 & 255) / 255.0F;
                b = (float)(color & 255) / 255.0F;
            } 
            else */if (armor instanceof ItemBlock) {
                ItemBlock blockItem = (ItemBlock)armor;
                if (blockItem.getBlock() instanceof BlockCarpet) {
                    // func_196057_c() == getSwappedColorValue()
                    // I don't know why there's a method to get the inverted color
                    // but not one to get the regular color
                    EnumDyeColor dyeColor = EnumDyeColor.byMetadata(itemstack.getMetadata());
                    float[] colors = dyeColor.getColorComponentValues();
                    b = colors[2];
                    g = colors[1];
                    r = colors[0];
                }
            }

            GlStateManager.color(r, g, b, 1.0F);
            this.horseModel.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }


    public boolean shouldCombineTextures() {
        return false;
    }
}
