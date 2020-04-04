package sekelsta.horse_colors.item;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.genetics.IGeneticEntity;
import sekelsta.horse_colors.init.ModEntities;

public class GeneBookItem extends Item {
    public GeneBookItem(Item.Properties properties) {
        super(properties);
    }

    public static boolean validBookTagContents(CompoundNBT nbt) {
        if (nbt == null) {
            return false;
        }
        // 8 is string type
        if (!nbt.contains("species", 8)) {
            return false;
        }
        if (!nbt.contains("genes", 8)) {
            return false;
        }
        try {
            Species sp = Species.valueOf(nbt.getString("species"));
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            String s = compoundnbt.getString("title");
            if (!StringUtils.isNullOrEmpty(s)) {
                return new StringTextComponent(s);
            }
        }

        return super.getDisplayName(stack);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            String s = compoundnbt.getString("species");
            if (!StringUtils.isNullOrEmpty(s)) {
                String translation = null;
                switch (Species.valueOf(s)) {
                    case HORSE:
                        translation = ModEntities.HORSE_GENETIC.getTranslationKey();
                        break;
                    case DONKEY:
                        translation = ModEntities.DONKEY_GENETIC.getTranslationKey();
                        break;
                    case MULE:
                        translation = ModEntities.MULE_GENETIC.getTranslationKey();
                        break;
                }
                if (translation != null) {
                    tooltip.add(new TranslationTextComponent(translation).applyTextStyle(TextFormatting.GRAY));
                }
            }
        }
    }


    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, net.minecraft.entity.player.PlayerEntity playerIn, LivingEntity entity, net .minecraft.util.Hand hand) {
        if (entity.world.isRemote) return false;
        if (!playerIn.abilities.isCreativeMode) {
            return false;
        }
        if (entity instanceof IGeneticEntity) {
            IGeneticEntity gentity = (IGeneticEntity)entity;
            switch (Species.valueOf(stack.getTag().getString("species"))) {
                case HORSE:
                    if (gentity instanceof HorseGeneticEntity) {
                        break;
                    }
                    else {
                        return false;
                    }
                case DONKEY:
                    if (gentity instanceof DonkeyGeneticEntity) {
                        break;
                    }
                    else {
                        return false;
                    }
                case MULE:
                    if (gentity instanceof MuleGeneticEntity) {
                        break;
                    }
                    else {
                        return false;
                    }
            }
            String geneString = stack.getTag().getString("genes");
            if (!gentity.getGenes().isValidGeneString(geneString)) {
                System.out.println("This gene book is invalid.");
                return false;
            }
            gentity.getGenes().genesFromString(geneString);
            return true;
        }
        return false;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        /*
        playerIn.openBook(itemstack, handIn);
        playerIn.addStat(Stats.ITEM_USED.get(this));
        */
        return ActionResult.resultSuccess(itemstack);
    }

    public static enum Species {
        HORSE,
        DONKEY,
        MULE
    }
}
