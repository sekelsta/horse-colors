package sekelsta.horse_colors.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
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

import sekelsta.horse_colors.HorseColors;
import sekelsta.horse_colors.client.GeneBookScreen;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.entity.genetics.Genome;
import sekelsta.horse_colors.entity.genetics.EquineGenome;
import sekelsta.horse_colors.entity.genetics.Species;

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

    public static Species getSpecies(CompoundNBT compoundnbt) {
        String s = compoundnbt.getString("species");
        if (!StringUtils.isNullOrEmpty(s)) {
            return Species.valueOf(s);
        }
        return null;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundNBT compoundnbt = stack.getTag();
            Species species = getSpecies(compoundnbt);
            if (species != null) {
                String translation = null;
                switch (species) {
                    case HORSE:
                        translation = ModEntities.HORSE_GENETIC.getDescriptionId();
                        break;
                    case DONKEY:
                        translation = ModEntities.DONKEY_GENETIC.getDescriptionId();
                        break;
                    case MULE:
                        translation = ModEntities.MULE_GENETIC.getDescriptionId();
                        break;
                }
                if (translation != null) {
                    // Compare to the author name on written books
                    tooltip.add(new TranslationTextComponent(translation).withStyle(TextFormatting.GRAY));
                }
            }
        }
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (validBookTagContents(itemstack.getTag())) {
            if (worldIn.isClientSide()) {
                openGeneBook(itemstack.getTag());
            }
            return ActionResult.success(itemstack);
        }
        HorseColors.logger.error("Gene book has invalid NBT");
        return ActionResult.fail(itemstack);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (stack.getTag() == null) {
            // Most likely someone summoned this item by command without data
            HorseColors.logger.error("Gene book has no NBT data");
            return ActionResultType.FAIL;
        }
        // Check that this itemstack has a UUID
        if (!stack.getTag().hasUUID("EntityUUID")) {
            return ActionResultType.PASS;
        }
        // Get this itemstack's entity's UUID
        UUID entityUUID = stack.getTag().getUUID("EntityUUID");
        // Null check that probably shouldn't be needed
        if (entityUUID == null) {
            return ActionResultType.PASS;
        }
        // Check that the entity matches
        if (!(entityUUID.equals(target.getUUID()))) {
            return ActionResultType.PASS;
        }
        // Make changes based on the entity
        if (target.hasCustomName()) {
            stack.setHoverName(target.getCustomName());
        }
        else {
            stack.resetHoverName();
        }
        return ActionResultType.sidedSuccess(player.level.isClientSide);
    } 


    @OnlyIn(Dist.CLIENT)
    public void openGeneBook(CompoundNBT nbt) {
        Minecraft mc = Minecraft.getInstance();
        Genome genome = new EquineGenome(getSpecies(nbt));
        genome.genesFromString(nbt.getString("genes"));
        mc.setScreen(new GeneBookScreen(genome));
    }
}
