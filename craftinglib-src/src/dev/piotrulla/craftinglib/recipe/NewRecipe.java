package dev.piotrulla.craftinglib.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class NewRecipe implements RecipeAccessor {

    private final Plugin plugin;

    public NewRecipe(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack itemStack, String craftingName) {
        NamespacedKey key = new NamespacedKey(this.plugin, "craftinglib-" + craftingName.toLowerCase());

        return new ShapedRecipe(key, itemStack);
    }

    @Override
    public void setIngredient(ShapedRecipe shapedRecipe, char key, ItemStack itemStack) {
        shapedRecipe.setIngredient(key, itemStack.getData());
    }
}
