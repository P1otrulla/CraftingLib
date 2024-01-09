package dev.piotrulla.craftinglib;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

public final class CraftingUtil {

    /**
     * Removes all recipes for given item without reloading server
     * @param itemStack Item to remove recipes
     **/
    public static void removeRecipe(ItemStack itemStack) {
        Iterator<Recipe> recipes = Bukkit.recipeIterator();

        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();

            if (recipe != null && recipe.getResult().isSimilar(itemStack)) {
                recipes.remove();
            }
        }
    }

    public static void removeItem(Inventory inventory, int slot, int amount) {
        if (inventory.getItem(slot) == null) {
            return;
        }

        ItemStack item = inventory.getItem(slot).clone();

        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }

        if (item.getAmount() <= amount) {
            inventory.setItem(slot, new ItemStack(Material.AIR));

            return;
        }

        item.setAmount(item.getAmount() - amount);
        inventory.setItem(slot, item);
    }

    private CraftingUtil() {

    }
}
