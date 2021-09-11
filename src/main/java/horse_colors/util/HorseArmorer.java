package sekelsta.horse_colors.util;

import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.genetics.HorseColorCalculator;
import sekelsta.horse_colors.item.CompatibleHorseArmor;

public class HorseArmorer
{
    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getTexture(Item armor)
    {
        if (armor instanceof CompatibleHorseArmor) {
            return ((CompatibleHorseArmor)armor).getAlternateTexture();
        }
        if (armor instanceof HorseArmorItem) {
            ResourceLocation vanilla = ((HorseArmorItem)armor).getTexture();
            // Only use my own version of textures in the minecraft namespace
            if (vanilla != null && vanilla.getNamespace().equals("minecraft")) {
                return new ResourceLocation(HorseColors.MODID, vanilla.getPath());
            }

            return vanilla;
        }
        if (armor instanceof BlockItem) {
            if (((BlockItem)armor).getBlock() instanceof WoolCarpetBlock) {
                return new ResourceLocation(HorseColorCalculator.fixPath("armor/carpet"));
            }
        }
        return null;
    }
}
