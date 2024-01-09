package dev.piotrulla.craftinglib.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class NewerRecipe implements RecipeAccessor {

    private final Plugin plugin;

    public NewerRecipe(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack itemStack, String craftingName) {
        NamespacedKey key = new NamespacedKey(this.plugin, "craftinglib-" + craftingName.toLowerCase());

        return new ShapedRecipe(key, itemStack);
    }

    @Override
    public void setIngredient(ShapedRecipe shapedRecipe, char key, ItemStack itemStack) {
        shapedRecipe.setIngredient(key, new RecipeChoice.ExactChoice(itemStack));
    }
}
