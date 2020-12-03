package sekelsta.horse_colors.item;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

import sekelsta.horse_colors.item.*;
import sekelsta.horse_colors.CreativeTab;
import sekelsta.horse_colors.HorseColors;

@Mod.EventBusSubscriber(modid = HorseColors.MODID, bus = Bus.MOD)
public class ModItems {
    public static GeneBookItem geneBookItem;
    public static GenderChangeItem genderChangeItem;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        geneBookItem = new GeneBookItem((new Item.Properties()).maxStackSize(1));
        geneBookItem.setRegistryName("gene_book");
        ForgeRegistries.ITEMS.register(geneBookItem);

        genderChangeItem = new GenderChangeItem((new Item.Properties()).maxStackSize(64).group(CreativeTab.instance));
        genderChangeItem.setRegistryName("gender_change_item");
        ForgeRegistries.ITEMS.register(genderChangeItem);
    }
}
