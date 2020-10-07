package sekelsta.horse_colors.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    public static CompatibleHorseArmor netheriteHorseArmor;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        geneBookItem = new GeneBookItem((new Item.Properties()).maxStackSize(1));
        geneBookItem.setRegistryName("gene_book");
        ForgeRegistries.ITEMS.register(geneBookItem);

        genderChangeItem = new GenderChangeItem((new Item.Properties()).maxStackSize(64).group(CreativeTab.instance));
        genderChangeItem.setRegistryName("gender_change_item");
        ForgeRegistries.ITEMS.register(genderChangeItem);

        netheriteHorseArmor = new CompatibleHorseArmor(13, "netherite", (new Item.Properties()).maxStackSize(1).group(CreativeTab.instance).isBurnable());
        netheriteHorseArmor.setRegistryName("netherite_horse_armor");
        ForgeRegistries.ITEMS.register(netheriteHorseArmor);
        registerDispenserBehaviour();
    }

    private static void registerDispenserBehaviour() {
        DefaultDispenseItemBehavior dispenseHorseArmor = new OptionalDispenseBehavior() {
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

                for(AbstractHorseEntity abstracthorseentity : source.getWorld().getEntitiesWithinAABB(AbstractHorseEntity.class, new AxisAlignedBB(blockpos), (horse) -> {
                    return horse.isAlive() && horse.func_230276_fq_();
                })) {
                    if (abstracthorseentity.isArmor(stack) && !abstracthorseentity.func_230277_fr_() && abstracthorseentity.isTame()) {
                        abstracthorseentity.replaceItemInInventory(401, stack.split(1));
                        this.setSuccessful(true);
                        return stack;
                    }
                }

                return super.dispenseStack(source, stack);
            }
        };
        DispenserBlock.registerDispenseBehavior(netheriteHorseArmor, dispenseHorseArmor);
    }
}
