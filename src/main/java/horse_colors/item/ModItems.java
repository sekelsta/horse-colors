package sekelsta.horse_colors.item;


import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import sekelsta.horse_colors.CreativeTab;
import sekelsta.horse_colors.HorseColors;

public class ModItems {
    public static final DeferredRegister<Item> ITEM_DEFERRED
        = DeferredRegister.create(ForgeRegistries.ITEMS, HorseColors.MODID);

    public static final RegistryObject<GeneBookItem> geneBookItem = ITEM_DEFERRED.register("gene_book", 
        () -> new GeneBookItem((new Item.Properties()).stacksTo(1))
    );
    public static final RegistryObject<GenderChangeItem> genderChangeItem = ITEM_DEFERRED.register("gender_change_item",
        () -> new GenderChangeItem((new Item.Properties()).stacksTo(64).tab(CreativeTab.instance))
    );
    public static final RegistryObject<CompatibleHorseArmor> netheriteHorseArmor = ITEM_DEFERRED.register("netherite_horse_armor", 
        () -> new CompatibleHorseArmor(13, "netherite", (new Item.Properties()).stacksTo(1).tab(CreativeTab.instance).fireResistant())
    );

    public static void registerDispenseBehaviour() {
        DefaultDispenseItemBehavior dispenseHorseArmor = new OptionalDispenseItemBehavior() {
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));

                for(AbstractHorse abstracthorseentity : source.getLevel().getEntitiesOfClass(AbstractHorse.class, new AABB(blockpos), (horse) -> {
                    return horse.isAlive() && horse.canWearArmor();
                })) {
                    if (abstracthorseentity.isArmor(stack) && !abstracthorseentity.isWearingArmor() && abstracthorseentity.isTamed()) {
                        abstracthorseentity.getSlot(401).set(stack.split(1));
                        this.setSuccess(true);
                        return stack;
                    }
                }

                return super.execute(source, stack);
            }
        };
        DispenserBlock.registerBehavior(netheriteHorseArmor.get(), dispenseHorseArmor);
    }
}
