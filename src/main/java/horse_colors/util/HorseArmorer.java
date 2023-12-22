package sekelsta.horse_colors.util;

import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.AbstractHorseGenetic;
import sekelsta.horse_colors.entity.genetics.HorseColorCalculator;
import sekelsta.horse_colors.item.CompatibleHorseArmor;

public class HorseArmorer
{
    private static boolean shouldOverwriteResource(ResourceLocation location) {
        if (location == null) {
            return false;
        }
        String namespace = location.getNamespace();
        return namespace.equals("minecraft") || namespace.equals("byg");
    }

    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getTexture(Item armor)
    {
        if (armor instanceof CompatibleHorseArmor) {
            return ((CompatibleHorseArmor)armor).getAlternateTexture();
        }
        if (armor instanceof HorseArmorItem) {
            ResourceLocation textureLocation = ((HorseArmorItem)armor).getTexture();
            // Only use my own version of textures in the minecraft namespace
            if (shouldOverwriteResource(textureLocation)) {
                return new ResourceLocation(HorseColors.MODID, textureLocation.getPath());
            }

            return textureLocation;
        }
        if (armor instanceof BlockItem) {
            if (((BlockItem)armor).getBlock() instanceof WoolCarpetBlock) {
                return new ResourceLocation(HorseColorCalculator.fixPath("armor/carpet"));
            }
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getSaddleTexture(AbstractHorseGenetic horse) {
        if (!horse.isSaddled()) {
            return null;
        }
        return new ResourceLocation(HorseColorCalculator.fixPath("saddle"));
    }

    @OnlyIn(Dist.CLIENT)
    public static int getSaddleTint(AbstractHorseGenetic horse) {
        return 0xffffff;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isSaddleEnchanted(AbstractHorseGenetic horse) {
        return false;
    }
}
