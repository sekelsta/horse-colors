package felinoid.horse_colors;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.*;


public class ModItems {
    // Make creative mode tab
    /* TODO:
    static final CreativeTabs tabHorseColors = (new CreativeTabs("tabHorseColors")
    {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(item_horse_book);
        }		
    });

    public static Item item_horse_book = new ItemHorseBook();
	*/
	public static void preInit() {}

	public static void init() {}

	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
        // TODO: 
		// event.getRegistry().register(item_horse_book);
	}

	private static void registerItemModel(Item item)
    {
		ModelLoader.setCustomModelResourceLocation(item, 0, 
            new ModelResourceLocation(item.getRegistryName(), "normal"));
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
    {
        // TODO:
		// registerItemModel(item_horse_book);
    }
}
