package sekelsta.horse_colors.util;

import net.minecraft.block.CarpetBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.genetics.HorseColorCalculator;
import sekelsta.horse_colors.item.CompatibleHorseArmor;

public class HorseArmorer
{
    private static ResourceLocation getVanillaLocation(HorseArmorItem armor)
    {
        return armor.getArmorTexture();
    }

    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getTexture(Item armor)
    {
        if (armor instanceof CompatibleHorseArmor) {
            return ((CompatibleHorseArmor)armor).getAlternateTexture();
        }
        if (armor instanceof HorseArmorItem) {
            ResourceLocation vanilla = getVanillaLocation((HorseArmorItem)armor);
            // Only use my own version of textures in the minecraft namespace
            if (vanilla != null && vanilla.getNamespace().equals("minecraft")) {
                return new ResourceLocation(HorseColors.MODID, vanilla.getPath());
            }

            return vanilla;
        }
        if (armor instanceof BlockItem) {
            if (((BlockItem)armor).getBlock() instanceof CarpetBlock) {
                return new ResourceLocation(HorseColorCalculator.fixPath("armor/carpet"));
            }
        }
        return null;
    }
}
