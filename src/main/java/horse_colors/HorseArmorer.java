package sekelsta.horse_colors;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HorseArmorer
{/*
    private ResourceLocation getVanillaLocation(ItemStack armorStack)
    {
        if (armorStack == ItemStack.EMPTY)
        {
            return null;
        }
        Item item = armorStack.getItem();
        if (!(item instanceof HorseArmorItem))
        {
            return null;
        }
        HorseArmorItem armor = (HorseArmorItem)item;
        // func_219976_d = public ResourceLocation getTexture()
        return armor.func_219976_d();
    }*/

    private static ResourceLocation getVanillaLocation(HorseArmorItem armor)
    {
        // func_219976_d = public ResourceLocation getTexture()
        return armor.func_219976_d();
    }



    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation getTexture(HorseArmorItem armor)
    {
        ResourceLocation vanilla = getVanillaLocation(armor);
        return vanilla == null? 
            null 
          : new ResourceLocation(HorseColors.MODID, vanilla.getPath());
    }

/*    @OnlyIn(Dist.CLIENT)
    public static String getTextureName(ItemStack armorStack)
    {
        return getTexture(armorStack).toString();
    }*/

/*    @OnlyIn(Dist.CLIENT)
    public static String getHash(ItemStack armorStack)
    {
        String texture = getTextureName(armorStack);
        if (texture == null)
        {
            return "";
        }
        else if (texture.contains("iron"))
        {
            return "iron";
        }
        else if (texture.contains("diamond"))
        {
            return "diamond";
        }
        else if (texture.contains("gold"))
        {
            return "gold";
        }
        else if (texture.contains("leather"))
        {
            return "leather";
        }
        else
        {
            System.out.println("Unknown horse armor type: " + texture);
        }
        return null;
    }*/
}
