package sekelsta.horse_colors.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

import sekelsta.horse_colors.entity.genetics.IGeneticEntity;

public class FertilityPotion extends Item {
    protected final boolean value;

    public FertilityPotion(Item.Properties builder, boolean value) {
        super(builder);
        this.value = value;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (target instanceof IGeneticEntity) {
            IGeneticEntity g = (IGeneticEntity)target;
            if (g.isFertile() == value) {
                return InteractionResult.PASS;
            }
            g.setFertile(value);
            if (g.isFertile() != value) {
                // Don't use on mules
                return InteractionResult.PASS;
            }
            if (player != null) {
                target.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (target.level.getRandom().nextFloat() * 0.4F + 0.8F));
            }
            if (player == null || !player.getAbilities().instabuild) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            }
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }
        return InteractionResult.PASS;
    } 

  /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
