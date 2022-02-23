package net.osnixer.craftinglib.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface RecipeNMS {

    ShapedRecipe getRecipe(ItemStack itemStack);

    ShapedRecipe setIngredient(ShapedRecipe shaped, char character, ItemStack itemStack);
}
