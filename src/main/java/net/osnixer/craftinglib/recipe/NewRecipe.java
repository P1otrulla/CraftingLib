package net.osnixer.craftinglib.recipe;

import net.osnixer.craftinglib.CraftingLib;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class NewRecipe implements RecipeNMS {

    @Override
    public ShapedRecipe getRecipe(ItemStack itemStack) {
        NamespacedKey key = new NamespacedKey(CraftingLib.getInstance().getPlugin(), NamespacedKey.randomKey().getKey());

        return new ShapedRecipe(key, itemStack);
    }

    @Override
    public ShapedRecipe setIngredient(ShapedRecipe shaped, char character, ItemStack itemStack) {
        shaped.setIngredient(character, itemStack.getData());
        return shaped;
    }
}
