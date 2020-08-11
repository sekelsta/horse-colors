package sekelsta.horse_colors;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;


public class CreativeTab extends CreativeTabs {
    // Make creative mode tab
    public static final CreativeTab instance = new CreativeTab("tabHorseColors");

    public CreativeTab(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(new ItemBlock(Blocks.HAY_BLOCK));
    }
}
