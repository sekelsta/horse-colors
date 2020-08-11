package sekelsta.horse_colors.util;
import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.genetics.HorseColorCalculator;

import net.minecraft.block.BlockCarpet;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HorseArmorer
{
    private static ResourceLocation getVanillaLocation(EntityLiving wearer, ItemStack armorStack)
    {
        if (armorStack == null || armorStack.isEmpty() || armorStack.getItem() == null) {
            return null;
        }
        return new ResourceLocation(armorStack.getItem().getHorseArmorTexture(wearer, armorStack));
    }

    @SideOnly(Side.CLIENT)
    public static ResourceLocation getTexture(EntityLiving wearer, ItemStack armorStack)
    {
        Item armor = armorStack.getItem();
        if (armor instanceof ItemBlock) {
            if (((ItemBlock)armor).getBlock() instanceof BlockCarpet) {
                return new ResourceLocation(HorseColorCalculator.fixPath("armor/carpet"));
            }
        }
        ResourceLocation vanilla = getVanillaLocation(wearer, armorStack);

        // Only use my own version of textures in the minecraft namespace
        if (vanilla == null) {
            return vanilla;
        }
        String namespace = vanilla.getNamespace();
        if (namespace == null || namespace.equals("minecraft")) {
            return new ResourceLocation(HorseColors.MODID, vanilla.getPath());
        }
        return vanilla;
    }
}
