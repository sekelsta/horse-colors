package sekelsta.horse_colors.item;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
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

    public static boolean validBookTagContents(CompoundTag nbt) {
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

    public static Species getSpecies(CompoundTag compoundnbt) {
        String s = compoundnbt.getString("species");
        if (!StringUtil.isNullOrEmpty(s)) {
            return Species.valueOf(s);
        }
        return null;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag()) {
            CompoundTag compoundnbt = stack.getTag();
            Species species = getSpecies(compoundnbt);
            if (species != null) {
                String translation = null;
                switch (species) {
                    case HORSE:
                        translation = ModEntities.HORSE_GENETIC.get().getDescriptionId();
                        break;
                    case DONKEY:
                        translation = ModEntities.DONKEY_GENETIC.get().getDescriptionId();
                        break;
                    case MULE:
                        translation = ModEntities.MULE_GENETIC.get().getDescriptionId();
                        break;
                }
                if (translation != null) {
                    // Compare to the author name on written books
                    tooltip.add(new TranslatableComponent(translation).withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (validBookTagContents(itemstack.getTag())) {
            if (worldIn.isClientSide()) {
                openGeneBook(itemstack.getTag());
            }
            return InteractionResultHolder.success(itemstack);
        }
        HorseColors.logger.error("Gene book has invalid NBT");
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (stack.getTag() == null) {
            // Most likely someone summoned this item by command without data
            HorseColors.logger.error("Gene book has no NBT data");
            return InteractionResult.FAIL;
        }
        // Check that this itemstack has a UUID
        if (!stack.getTag().hasUUID("EntityUUID")) {
            return InteractionResult.PASS;
        }
        // Get this itemstack's entity's UUID
        UUID entityUUID = stack.getTag().getUUID("EntityUUID");
        // Null check that probably shouldn't be needed
        if (entityUUID == null) {
            return InteractionResult.PASS;
        }
        // Check that the entity matches
        if (!(entityUUID.equals(target.getUUID()))) {
            return InteractionResult.PASS;
        }
        // Make changes based on the entity
        if (target.hasCustomName()) {
            stack.setHoverName(target.getCustomName());
        }
        else {
            stack.resetHoverName();
        }
        return InteractionResult.sidedSuccess(player.level.isClientSide);
    } 


    @OnlyIn(Dist.CLIENT)
    public void openGeneBook(CompoundTag nbt) {
        Minecraft mc = Minecraft.getInstance();
        Genome genome = new EquineGenome(getSpecies(nbt));
        genome.genesFromString(nbt.getString("genes"));
        mc.setScreen(new GeneBookScreen(genome));
    }
}
