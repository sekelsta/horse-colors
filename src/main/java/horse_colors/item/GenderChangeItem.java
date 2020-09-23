package sekelsta.horse_colors.item;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.config.HorseConfig;
import sekelsta.horse_colors.entity.genetics.IGeneticEntity;
import sekelsta.horse_colors.HorseColors;

public class GenderChangeItem extends Item {
    public GenderChangeItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target instanceof IGeneticEntity) {
            IGeneticEntity g = (IGeneticEntity)target;
            g.setMale(!g.isMale());
            if (player != null) {
                target.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
            }
            if (player == null || !player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
            return ActionResultType.func_233537_a_(player.world.isRemote);
        }
        return ActionResultType.PASS;
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

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!HorseConfig.isGenderEnabled()) {
            String translation = HorseColors.MODID + ".gender_change_item.gender_disabled_warning";
            tooltip.add(new TranslationTextComponent(translation).mergeStyle(TextFormatting.GRAY));
        }
    }

}
