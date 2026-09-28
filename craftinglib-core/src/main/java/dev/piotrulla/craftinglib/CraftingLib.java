package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.recipe.manager.CraftingRecipeManager;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point of a platform implementation.
 * Registering a recipe type the platform cannot handle fails with {@link UnsupportedOperationException}.
 *
 * @param <T> platform item type (e.g. Bukkit {@code ItemStack})
 */
public interface CraftingLib<T> {

    /**
     * @return manager of crafting (grid) recipes
     */
    @NotNull
    CraftingRecipeManager<T> getRecipeManager();

    @NotNull
    RecipeManager<SmeltingRecipe<T>> getSmeltingManager();

    @NotNull
    RecipeManager<BrewingRecipe<T>> getBrewingManager();

    @NotNull
    RecipeManager<StonecuttingRecipe<T>> getStonecuttingManager();

    @NotNull
    RecipeManager<SmithingRecipe<T>> getSmithingManager();

    @NotNull
    RecipeManager<AnvilRecipe<T>> getAnvilManager();

    @NotNull
    RecipeManager<GrindstoneRecipe<T>> getGrindstoneManager();

    /**
     * Unregisters every recipe and detaches from the platform.
     */
    void shutdown();
}