package sekelsta.horse_colors.item;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class InfertilityPotionBrewingRecipe implements IBrewingRecipe {
    /**
     * Returns true is the passed ItemStack is an input for this recipe. "Input"
     * being the item that goes in one of the three bottom slots of the brewing
     * stand (e.g: water bottle)
     */
    @Override
    public boolean isInput(ItemStack input) {
        return PotionUtils.getPotion(input) == Potions.AWKWARD;
    }

    /**
     * Returns true if the passed ItemStack is an ingredient for this recipe.
     * "Ingredient" being the item that goes in the top slot of the brewing
     * stand (e.g: nether wart)
     */
    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == Items.POISONOUS_POTATO;
    }

    /**
     * Returns the output when the passed input is brewed with the passed
     * ingredient. Empty if invalid input or ingredient.
     */
    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!input.isEmpty() && isInput(input) && !ingredient.isEmpty() && isIngredient(ingredient)) {
            return new ItemStack(ModItems.infertilityPotion.get());
        }

        return ItemStack.EMPTY;
    }
}
