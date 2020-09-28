package sekelsta.horse_colors.item;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import sekelsta.horse_colors.client.GeneBookScreen;
import sekelsta.horse_colors.entity.*;
import sekelsta.horse_colors.genetics.Genome;
import sekelsta.horse_colors.genetics.HorseGenome;
import sekelsta.horse_colors.genetics.IGeneticEntity;
import sekelsta.horse_colors.genetics.Species;

public class GeneBookItem extends Item {
    public GeneBookItem() {
        this.setMaxStackSize(1);
    }

    public static boolean validBookTagContents(NBTTagCompound nbt) {
        if (nbt == null) {
            return false;
        }
        // 8 is string type
        if (!nbt.hasKey("species", 8)) {
            return false;
        }
        if (!nbt.hasKey("genes", 8)) {
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

    public static Species getSpecies(NBTTagCompound compoundnbt) {
        String s = compoundnbt.getString("species");
        if (!StringUtils.isNullOrEmpty(s)) {
            return Species.valueOf(s);
        }
        return null;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compoundnbt = stack.getTagCompound();
            String s = compoundnbt.getString("species");
            Species species = getSpecies(compoundnbt);
            if (species != null) {
                String translation = null;
                switch (species) {
                    case HORSE:
                        translation = ModEntities.HORSE_GENETIC.getName();
                        break;
                    case DONKEY:
                        translation = ModEntities.DONKEY_GENETIC.getName();
                        break;
                    case MULE:
                        translation = ModEntities.MULE_GENETIC.getName();
                        break;
                }
                if (translation != null) {
                    tooltip.add(I18n.translateToLocal("entity." + translation + ".name"));
                }
            }
        }
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (validBookTagContents(itemstack.getTagCompound())) {
            if (worldIn.isRemote) {
                openGeneBook(itemstack.getTagCompound());
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
        }
        System.out.println("Gene book has invalid NBT");
        return new ActionResult<>(EnumActionResult.FAIL, itemstack);
    }

    @SideOnly(Side.CLIENT)
    public void openGeneBook(NBTTagCompound nbt) {
        Minecraft mc = Minecraft.getMinecraft();
        Genome genome = new HorseGenome(getSpecies(nbt));
        genome.genesFromString(nbt.getString("genes"));
        mc.displayGuiScreen(new GeneBookScreen(genome));
    }
}
