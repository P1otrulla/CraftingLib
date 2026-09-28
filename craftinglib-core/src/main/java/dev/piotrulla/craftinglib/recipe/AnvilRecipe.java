package dev.piotrulla.craftinglib.recipe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Anvil recipe: left + right item turn into the result for a level cost.
 * Amounts of both items are consumed. Anvils have no vanilla recipes, so {@code replaceVanilla} is rejected.
 *
 * @param <T> platform item type
 */
public final class AnvilRecipe<T> extends AbstractRecipe<T> {

    public static final int MIN_LEVEL_COST = 1;

    private final T left;
    private final T right;
    private final int levelCost;

    public AnvilRecipe(@NotNull RecipeSettings<T> settings, @NotNull T left, @NotNull T right, int levelCost) {
        super(settings);
        this.rejectReplaceVanilla();
        this.rejectRecipeBook();
        this.left = this.requireItem(left, "Left item");
        this.right = this.requireItem(right, "Right item");

        if (levelCost < MIN_LEVEL_COST) {
            throw new IllegalArgumentException("Level cost of recipe '" + this.getKey() + "' must be at least "
                    + MIN_LEVEL_COST + ", got: " + levelCost);
        }
        this.levelCost = levelCost;
    }

    /**
     * @return copy of the item in the first slot, its amount is consumed
     */
    @NotNull
    public T getLeft() {
        return this.itemAdapter.copy(this.left);
    }

    /**
     * @return copy of the item in the second slot, its amount is consumed
     */
    @NotNull
    public T getRight() {
        return this.itemAdapter.copy(this.right);
    }

    public int getLevelCost() {
        return this.levelCost;
    }

    public boolean matches(@Nullable T left, @Nullable T right) {
        return this.matchesIngredient(this.left, left) && this.matchesIngredient(this.right, right);
    }
}