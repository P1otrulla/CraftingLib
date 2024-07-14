package dev.piotrulla.craftinglib.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public interface RecipeAccessor {

    ShapedRecipe createShapedRecipe(ItemStack itemStack, String craftingName);

    void setIngredient(ShapedRecipe shapedRecipe, char key, ItemStack itemStack);
}
