package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Registry of crafting (grid) recipes.
 *
 * @param <T> platform item type
 */
public interface CraftingRecipeManager<T> extends RecipeManager<CraftingRecipe<T>> {

    /**
     * @return first registered recipe matching the grid
     */
    @NotNull
    Optional<RecipeMatch<T>> findMatch(@NotNull CraftingGrid<T> grid);
}