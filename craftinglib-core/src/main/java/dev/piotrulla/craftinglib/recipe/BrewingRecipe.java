package dev.piotrulla.craftinglib.recipe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Brewing stand recipe: ingredient (top slot) turns the input (bottle slot) into the result.
 * Vanilla brewing cannot be replaced, so {@code replaceVanilla} is rejected.
 *
 * @param <T> platform item type
 */
public final class BrewingRecipe<T> extends AbstractRecipe<T> {

    private final T input;
    private final T ingredient;

    public BrewingRecipe(@NotNull RecipeSettings<T> settings, @NotNull T input, @NotNull T ingredient) {
        super(settings);
        this.rejectReplaceVanilla();
        this.rejectRecipeBook();
        this.input = this.requireSingleItem(input, "Input");
        this.ingredient = this.requireSingleItem(ingredient, "Ingredient");
    }

    /**
     * @return copy of the item in the bottle slot (e.g. awkward potion)
     */
    @NotNull
    public T getInput() {
        return this.itemAdapter.copy(this.input);
    }

    /**
     * @return copy of the item in the ingredient slot
     */
    @NotNull
    public T getIngredient() {
        return this.itemAdapter.copy(this.ingredient);
    }

    public boolean matches(@Nullable T input, @Nullable T ingredient) {
        return this.matchesIngredient(this.input, input) && this.matchesIngredient(this.ingredient, ingredient);
    }
}