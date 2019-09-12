package sekelsta.horse_colors;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseArmorLayer extends LayerRenderer<HorseGeneticEntity, HorseGeneticModel<HorseGeneticEntity>> {
   private final HorseGeneticModel<HorseGeneticEntity> field_215341_a = new HorseGeneticModel<>();

   public HorseArmorLayer(IEntityRenderer<HorseGeneticEntity, HorseGeneticModel<HorseGeneticEntity>> p_i50937_1_) {
      super(p_i50937_1_);
   }

   public void render(HorseGeneticEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      ItemStack itemstack = entityIn.getHorseArmor();
      if (itemstack.getItem() instanceof HorseArmorItem) {
         HorseArmorItem horsearmoritem = (HorseArmorItem)itemstack.getItem();
         this.getEntityModel().setModelAttributes(this.field_215341_a);
         this.field_215341_a.setLivingAnimations(entityIn, p_212842_2_, p_212842_3_, p_212842_4_);
         this.bindTexture(HorseArmorer.getTexture(horsearmoritem));
         if (horsearmoritem instanceof DyeableHorseArmorItem) {
            int i = ((DyeableHorseArmorItem)horsearmoritem).getColor(itemstack);
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            GlStateManager.color4f(f, f1, f2, 1.0F);
            this.field_215341_a.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
            return;
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_215341_a.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
