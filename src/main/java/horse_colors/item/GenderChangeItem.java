package sekelsta.horse_colors.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import sekelsta.horse_colors.genetics.IGeneticEntity;

public class GenderChangeItem extends Item {
    public GenderChangeItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target instanceof IGeneticEntity) {
            IGeneticEntity g = (IGeneticEntity)target;
            g.setMale(!g.isMale());
            if (player != null) {
                target.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
            }
            if (player == null || !player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
            return true;
        }
        return false;
    } 

  /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

}
