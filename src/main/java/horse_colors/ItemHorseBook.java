package felinoid.horse_colors;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ItemHorseBook extends Item
{/*TODO
    public ItemHorseBook()
    {
        String name = "ItemHorseBook";
        this.setRegistryName(HorseColors.MODID, name);
        this.setUnlocalizedName(this.getRegistryName().toString());
        this.setCreativeTab(ModItems.tabHorseColors);
    }
*/
    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     *//*
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        if (!(target instanceof EntityHorseFelinoid))
        {
            return false;
        }

        if (stack.hasDisplayName())
        {
            target.setCustomNameTag(stack.getDisplayName());
            // Honestly we probably don't need this, but it can't hurt
            if (target instanceof EntityLiving)
            {
                ((EntityLiving)target).enablePersistence();
            }
        }

        stack.shrink(1);
        return true;
    }*/
}
