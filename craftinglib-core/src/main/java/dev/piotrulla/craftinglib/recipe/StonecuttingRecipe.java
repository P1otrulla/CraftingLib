package dev.piotrulla.craftinglib.recipe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stonecutter recipe: one input item, one of possibly many results offered for it.
 *
 * @param <T> platform item type
 */
public final class StonecuttingRecipe<T> extends AbstractRecipe<T> {

    private final T input;

    public StonecuttingRecipe(@NotNull RecipeSettings<T> settings, @NotNull T input) {
        super(settings);
        this.rejectRecipeBookCategory();
        this.input = this.requireSingleItem(input, "Input");
    }

    /**
     * @return copy of the input item
     */
    @NotNull
    public T getInput() {
        return this.itemAdapter.copy(this.input);
    }

    public boolean matches(@Nullable T item) {
        return this.matchesIngredient(this.input, item);
    }
}