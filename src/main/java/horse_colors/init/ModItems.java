package sekelsta.horse_colors.init;

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

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        geneBookItem = new GeneBookItem((new Item.Properties()).maxStackSize(1).group(CreativeTab.instance));
        geneBookItem.setRegistryName("gene_book");
        ForgeRegistries.ITEMS.register(geneBookItem);
    }
}
