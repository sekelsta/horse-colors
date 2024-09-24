package sekelsta.horse_colors.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.entity.ModEntities;

public class ModItems {
    public static final DeferredRegister.Items ITEM_DEFERRED = DeferredRegister.createItems(HorseColors.MODID);

    public static final DeferredItem<GeneBookItem> geneBookItem = ITEM_DEFERRED.register("gene_book", 
        () -> new GeneBookItem((new Item.Properties()).stacksTo(1))
    );
    public static final DeferredItem<GenderChangeItem> genderChangePotion = ITEM_DEFERRED.register("gender_change_item",
        () -> new GenderChangeItem((new Item.Properties()).stacksTo(64))
    );
    public static final DeferredItem<FertilityPotion> fertilityPotion = ITEM_DEFERRED.register("fertility_potion",
        () -> new FertilityPotion((new Item.Properties()).stacksTo(64), true)
    );
    public static final DeferredItem<FertilityPotion> infertilityPotion = ITEM_DEFERRED.register("infertility_potion",
        () -> new FertilityPotion((new Item.Properties()).stacksTo(64), false)
    );
    public static final DeferredItem<CompatibleHorseArmor> netheriteHorseArmor = ITEM_DEFERRED.register("netherite_horse_armor", 
        () -> new CompatibleHorseArmor(13, "netherite", (new Item.Properties()).stacksTo(1).fireResistant())
    );

    public static void registerDispenseBehaviour() {
        DefaultDispenseItemBehavior dispenseHorseArmor = new OptionalDispenseItemBehavior() {
            /**
             * Dispense the specified stack, play the dispense sound and spawn particles.
             */
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));

                for(AbstractHorse abstracthorseentity : source.level().getEntitiesOfClass(AbstractHorse.class, new AABB(blockpos), (horse) -> {
                    return horse.isAlive();
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

    public static void registerPotionRecipes() {
        BrewingRecipeRegistry.addRecipe(new InfertilityPotionBrewingRecipe());
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        // Skip gene book item
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(genderChangePotion);
            event.accept(fertilityPotion);
            event.accept(infertilityPotion);
        }
        else if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            event.accept(netheriteHorseArmor);
        }
        ModEntities.addToCreativeTab(event);
    }
}
