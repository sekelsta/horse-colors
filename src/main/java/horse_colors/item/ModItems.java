package sekelsta.horse_colors.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import sekelsta.horse_colors.item.*;
import sekelsta.horse_colors.CreativeTab;
import sekelsta.horse_colors.HorseColors;

public class ModItems {
    public static GeneBookItem geneBookItem;
    public static GenderChangeItem genderChangeItem;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        geneBookItem = new GeneBookItem();
        geneBookItem.setRegistryName(HorseColors.MODID, "gene_book");
        geneBookItem.setTranslationKey(geneBookItem.getRegistryName().toString());
        event.getRegistry().register(geneBookItem);

        genderChangeItem = new GenderChangeItem();
        genderChangeItem.setRegistryName(HorseColors.MODID, "gender_change_item");
        genderChangeItem.setTranslationKey(genderChangeItem.getRegistryName().toString());
        genderChangeItem.setCreativeTab(CreativeTabs.MISC);
        event.getRegistry().register(genderChangeItem);
    }

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(geneBookItem, 0, 
            new ModelResourceLocation(geneBookItem.getRegistryName(), "normal"));
        ModelLoader.setCustomModelResourceLocation(genderChangeItem, 0, 
            new ModelResourceLocation(genderChangeItem.getRegistryName(), "normal"));
    }
}
