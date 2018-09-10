package felinoid.horse_colors;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.SidedProxy;


@Mod(modid = HorseColors.MODID, name = HorseColors.NAME, version = HorseColors.VERSION)

public class HorseColors
{
	public static final String NAME = "Realistic Horse Colors";
	public static final String MODID = "horse_colors";
	public static final String VERSION = "1.12.2-1.0.0";


    @SidedProxy(clientSide="felinoid.horse_colors.ClientProxy",  serverSide="felinoid.horse_colors.CommonProxy")
    public static CommonProxy proxy;
	
	@Mod.Instance("horse_colors")
	public static HorseColors instance;
	
	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) 
	{
		MinecraftForge.EVENT_BUS.register(HorseReplacer.class);
		MinecraftForge.EVENT_BUS.register(ModEntities.class);
	}
	
	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) 
    {
        proxy.registerRenderers();
    }
	
	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event) {}
}
