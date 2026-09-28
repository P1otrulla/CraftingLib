package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @param <T> platform item type
 */
public final class SimpleCraftingRecipeManager<T> extends SimpleRecipeManager<CraftingRecipe<T>>
        implements CraftingRecipeManager<T> {

    public SimpleCraftingRecipeManager(@NotNull PlatformRecipeRegistrar<CraftingRecipe<T>> registrar) {
        super(registrar);
    }

    @NotNull
    @Override
    public Optional<RecipeMatch<T>> findMatch(@NotNull CraftingGrid<T> grid) {
        Objects.requireNonNull(grid, "grid");

        for (CraftingRecipe<T> recipe : this.recipesView()) {
            if (!recipe.fitsGrid(grid.getSize())) {
                continue;
            }

            Optional<RecipeMatch<T>> match = recipe.match(grid);
            if (match.isPresent()) {
                return match;
            }
        }

        return Optional.empty();
    }
}