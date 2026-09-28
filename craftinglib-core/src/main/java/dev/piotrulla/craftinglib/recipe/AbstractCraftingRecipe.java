package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import org.jetbrains.annotations.NotNull;

/**
 * Base for grid recipes (shaped, shapeless).
 *
 * @param <T> platform item type
 */
public abstract class AbstractCraftingRecipe<T> extends AbstractRecipe<T> implements CraftingRecipe<T> {

    private static final String CRAFTING_BOOK = "crafting";

    protected AbstractCraftingRecipe(@NotNull RecipeSettings<T> settings) {
        super(settings);
    }

    /**
     * Called by final subclasses once constructed, so no half-initialized {@code this} escapes.
     */
    protected final void requireCraftingCategory() {
        this.requireRecipeBookCategory(RecipeBookCategory::isCraftingCategory, CRAFTING_BOOK);
    }

    protected void requireFitsGrid(int gridSize) {
        if (!this.fitsGrid(gridSize)) {
            throw new IllegalArgumentException("Recipe '" + this.getKey() + "' does not fit "
                    + gridSize + "x" + gridSize + " grid");
        }
    }
}