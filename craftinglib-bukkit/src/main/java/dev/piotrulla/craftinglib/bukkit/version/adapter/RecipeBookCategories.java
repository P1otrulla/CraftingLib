package dev.piotrulla.craftinglib.bukkit.version.adapter;

import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

/**
 * Maps core categories to Bukkit ones (same constant names). Loaded only when the Bukkit category API exists.
 */
final class RecipeBookCategories {

    private RecipeBookCategories() {
    }

    static void applyCrafting(@NotNull Recipe serverRecipe, @NotNull RecipeBookCategory category) {
        ((CraftingRecipe) serverRecipe).setCategory(CraftingBookCategory.valueOf(category.name()));
    }

    static void applyCooking(@NotNull Recipe serverRecipe, @NotNull RecipeBookCategory category) {
        ((CookingRecipe<?>) serverRecipe).setCategory(CookingBookCategory.valueOf(category.name()));
    }
}
