package dev.piotrulla.craftinglib.recipe;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class OldRecipe implements RecipeAccessor {

    @Override
    public ShapedRecipe createShapedRecipe(ItemStack itemStack, String group) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(itemStack);

        shapedRecipe.setGroup(group);

        return shapedRecipe;
    }

    @Override
    public void setIngredient(ShapedRecipe shapedRecipe, char key, ItemStack itemStack) {
        shapedRecipe.setIngredient(key, itemStack.getData());
    }
}
