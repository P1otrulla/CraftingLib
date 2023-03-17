package dev.piotrulla.craftinglib.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.UUID;

public class NewRecipe implements RecipeAccessor {

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack itemStack, String group) {
        NamespacedKey key = NamespacedKey.minecraft(UUID.randomUUID().toString());

        ShapedRecipe shapedRecipe = new ShapedRecipe(key, itemStack);

        shapedRecipe.setGroup(group);

        return shapedRecipe;
    }

    @Override
    public void setIngredient(ShapedRecipe shapedRecipe, char key, ItemStack itemStack) {
        shapedRecipe.setIngredient(key, itemStack.getData());
    }
}
