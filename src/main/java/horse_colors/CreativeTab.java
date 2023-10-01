package sekelsta.horse_colors;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CreativeTab extends CreativeModeTab {
    // Make creative mode tab
    public static final CreativeTab instance = new CreativeTab(CreativeModeTab.TABS.length, "tabHorseColors");

    public CreativeTab(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(Items.HAY_BLOCK);
    }
}
