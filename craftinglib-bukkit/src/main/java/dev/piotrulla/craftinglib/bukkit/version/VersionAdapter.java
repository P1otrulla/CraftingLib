package dev.piotrulla.craftinglib.bukkit.version;

import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hides recipe API differences between server versions.
 * Recipe book settings are cosmetic and silently skipped where the server does not support them.
 */
public interface VersionAdapter {

    @NotNull
    ShapedRecipe createShapedRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook);

    @NotNull
    ShapelessRecipe createShapelessRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook);

    void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient);

    void addIngredient(@NotNull ShapelessRecipe recipe, @NotNull ItemStack ingredient);

    @NotNull
    Recipe createCookingRecipe(@NotNull SmeltingRecipe<ItemStack> recipe);

    boolean isCookingRecipe(@NotNull Recipe serverRecipe);

    boolean isCookingRecipeFor(@NotNull Recipe serverRecipe, @NotNull SmeltingRecipe<ItemStack> recipe);

    /**
     * Adds the recipe and, where supported, resends recipes to online players so they show up in the recipe book.
     */
    boolean addRecipe(@NotNull Server server, @NotNull Recipe serverRecipe);

    /**
     * Removes an owned recipe by key and, where supported, resends recipes to online players.
     *
     * @return true when removed; false when absent or removal by key is not supported (caller scans all recipes)
     */
    boolean removeRecipe(@NotNull Server server, @NotNull String key);

    boolean isKeyed(@NotNull Recipe serverRecipe);

    /**
     * @return key of a recipe registered by this plugin, {@code null} for foreign or unkeyed recipes
     */
    @Nullable
    String getOwnedKey(@NotNull Recipe serverRecipe);

    /**
     * @return true when {@code namespace} is the namespace of recipes registered by this plugin
     */
    boolean isOwnedNamespace(@NotNull String namespace);

    @NotNull
    String getName();
}
