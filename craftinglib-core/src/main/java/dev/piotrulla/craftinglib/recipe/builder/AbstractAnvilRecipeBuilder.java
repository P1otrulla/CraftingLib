package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * builder.withLeft(sword)
 *     .withRight(magicDust)
 *     .withResult(magicSword)
 *     .withLevelCost(10)
 *     .build();
 * </pre>
 * Level cost defaults to {@link AnvilRecipe#MIN_LEVEL_COST}.
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractAnvilRecipeBuilder<T, B extends AbstractAnvilRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, AnvilRecipe<T>, B> {

    private T left;
    private T right;
    private int levelCost = AnvilRecipe.MIN_LEVEL_COST;

    protected AbstractAnvilRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    /**
     * @param left item in the first slot, its amount is consumed
     */
    @NotNull
    public B withLeft(@NotNull T left) {
        this.left = this.requireItem(left, "Left item");
        return this.self();
    }

    /**
     * @param right item in the second slot, its amount is consumed
     */
    @NotNull
    public B withRight(@NotNull T right) {
        this.right = this.requireItem(right, "Right item");
        return this.self();
    }

    @NotNull
    public B withLevelCost(int levelCost) {
        this.levelCost = levelCost;
        return this.self();
    }

    @NotNull
    @Override
    public AnvilRecipe<T> build() {
        return new AnvilRecipe<>(
                this.buildSettings(),
                this.requireSet(this.left, "Left item"),
                this.requireSet(this.right, "Right item"),
                this.levelCost
        );
    }
}