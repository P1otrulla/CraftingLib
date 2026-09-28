package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Immutable custom crafting (grid) recipe.
 *
 * @param <T> platform item type
 */
public interface CraftingRecipe<T> extends CustomRecipe<T> {

    @NotNull
    RecipeType getType();

    /**
     * @param gridSize side length of the grid (2 = player inventory, 3 = workbench)
     */
    boolean fitsGrid(int gridSize);

    /**
     * @return match with per-slot consumption, or empty when the grid does not satisfy this recipe
     */
    @NotNull
    Optional<RecipeMatch<T>> match(@NotNull CraftingGrid<T> grid);

    /**
     * Ingredient placement used to fill an empty grid (recipe book autofill), starting at the top-left corner.
     *
     * @param gridSize side length of the grid, see {@link #fitsGrid(int)}
     * @return slot index to copy of the ingredient, slots not in the map stay empty
     * @throws IllegalArgumentException when the recipe does not fit the grid
     */
    @NotNull
    Map<Integer, T> getGridLayout(int gridSize);
}