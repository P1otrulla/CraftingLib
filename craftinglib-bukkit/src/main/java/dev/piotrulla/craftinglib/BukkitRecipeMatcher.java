package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Recipe matcher - single responsibility: matching recipes
 */
public final class BukkitRecipeMatcher {

    private BukkitRecipeMatcher() {
    }

    /**
     * Finds a matching recipe from collection
     */
    @Nullable
    public static BukkitRecipe findMatchingRecipe(
            @NotNull Collection<BukkitRecipe> recipes,
            @NotNull ItemStack[] matrix,
            @NotNull InventoryType inventoryType) {

        Objects.requireNonNull(recipes, "Recipes cannot be null");
        Objects.requireNonNull(matrix, "Matrix cannot be null");
        Objects.requireNonNull(inventoryType, "Inventory type cannot be null");

        if (isEmptyMatrix(matrix)) {
            return null;
        }

        List<RecipeIngredient<ItemStack>> ingredients = convertMatrixToIngredients(matrix);

        for (BukkitRecipe recipe : recipes) {
            if (matchesRecipe(recipe, ingredients, inventoryType)) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Finds a matching recipe by ingredients
     */
    @Nullable
    public static BukkitRecipe findMatchingRecipe(
            @NotNull Collection<BukkitRecipe> recipes,
            @NotNull List<RecipeIngredient<ItemStack>> ingredients) {

        Objects.requireNonNull(recipes, "Recipes cannot be null");
        Objects.requireNonNull(ingredients, "Ingredients cannot be null");

        for (BukkitRecipe recipe : recipes) {
            if (recipe.matches(ingredients)) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Checks if recipe matches the criteria
     */
    public static boolean matchesRecipe(
            @NotNull BukkitRecipe recipe,
            @NotNull List<RecipeIngredient<ItemStack>> ingredients,
            @NotNull InventoryType inventoryType
    ) {
        return recipe.getInventoryType() == inventoryType && recipe.matches(ingredients);
    }

    /**
     * Checks if recipe matches matrix directly (legacy support)
     */
    public static boolean matchesMatrix(
            @NotNull BukkitRecipe recipe,
            @NotNull ItemStack[] matrix,
            @NotNull InventoryType inventoryType
    ) {
        if (recipe.getInventoryType() != inventoryType) {
            return false;
        }

        if (!recipe.isCraftingRecipe()) {
            return false;
        }

        return recipe.matchesCraftingMatrix(matrix);
    }

    /**
     * Converts matrix to ingredients list
     */
    @NotNull
    public static List<RecipeIngredient<ItemStack>> convertMatrixToIngredients(@NotNull ItemStack[] matrix) {
        List<RecipeIngredient<ItemStack>> ingredients = new ArrayList<>(matrix.length);

        for (ItemStack item : matrix) {
            if (item == null || item.getType() == Material.AIR) {
                ingredients.add(null);
            } else {
                ingredients.add(new BukkitRecipeIngredient(item));
            }
        }

        return ingredients;
    }

    /**
     * Checks if matrix is empty
     */
    public static boolean isEmptyMatrix(@NotNull ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a result key for indexing
     */
    @NotNull
    public static String createResultKey(@NotNull ItemStack result) {
        Objects.requireNonNull(result, "Result cannot be null");
        return result.getType().name();
    }

    /**
     * Determines crafting type from matrix size
     */
    @NotNull
    public static InventoryType determineCraftingType(int matrixSize) {
        if (matrixSize == 4) {
            return InventoryType.PLAYER_INVENTORY_2X2;
        }
        return InventoryType.CRAFTING_TABLE_3X3;
    }
}