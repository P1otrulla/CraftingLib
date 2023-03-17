package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.recipe.RecipeAccessor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Iterator;

public final class CraftingRegistry {

    private final char[] CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

    private final RecipeAccessor accessor;
    private final Server server;

    public CraftingRegistry(RecipeAccessor accessor, Server server) {
        this.accessor = accessor;
        this.server = server;
    }

    public void addCrafting(Crafting crafting) {
        ShapedRecipe recipe = this.accessor.createShapedRecipe(crafting.result(), crafting.group());

        recipe.shape("ABC", "DEF", "GHI");

        for (int i = 0; i <= 8; i++){
            ItemStack itemStack = crafting.items()[i];

            if (itemStack == null) {
                itemStack = new ItemStack(Material.AIR);
            }

            this.accessor.setIngredient(recipe, CHARS[i], itemStack);
        }

        this.server.addRecipe(recipe);
    }

    public void removeCrafting(Crafting crafting) {
        Iterator<Recipe> recipeIterator = this.server.recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            if (recipe != null && recipe.getResult().isSimilar(crafting.result())) {
                recipeIterator.remove();
            }
        }
    }
}
