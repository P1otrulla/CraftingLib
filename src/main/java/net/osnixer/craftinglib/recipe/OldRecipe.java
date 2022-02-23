package net.osnixer.craftinglib.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class OldRecipe implements RecipeNMS {

    @Override
    public ShapedRecipe getRecipe(ItemStack itemStack) {
        return new ShapedRecipe(itemStack);
    }

    @Override
    public ShapedRecipe setIngredient(ShapedRecipe shaped, char character, ItemStack itemStack) {
        shaped.setIngredient(character, itemStack.getData());
        return shaped;
    }
}
