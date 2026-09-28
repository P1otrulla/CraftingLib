package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * builder.withInput(stone)
 *     .withResult(slabs)
 *     .build();
 * </pre>
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractStonecuttingRecipeBuilder<T, B extends AbstractStonecuttingRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, StonecuttingRecipe<T>, B> {

    private T input;

    protected AbstractStonecuttingRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    @NotNull
    public B withInput(@NotNull T input) {
        this.input = this.requireItem(input, "Input");
        return this.self();
    }

    @NotNull
    @Override
    public StonecuttingRecipe<T> build() {
        return new StonecuttingRecipe<>(this.buildSettings(), this.requireSet(this.input, "Input"));
    }
}