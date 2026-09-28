package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * builder.withInput(awkwardPotion)
 *     .withIngredient(goldenCarrot)
 *     .withResult(customPotion)
 *     .build();
 * </pre>
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractBrewingRecipeBuilder<T, B extends AbstractBrewingRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, BrewingRecipe<T>, B> {

    private T input;
    private T ingredient;

    protected AbstractBrewingRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    /**
     * @param input item in the bottle slot (e.g. awkward potion)
     */
    @NotNull
    public B withInput(@NotNull T input) {
        this.input = this.requireItem(input, "Input");
        return this.self();
    }

    /**
     * @param ingredient item in the top (ingredient) slot
     */
    @NotNull
    public B withIngredient(@NotNull T ingredient) {
        this.ingredient = this.requireItem(ingredient, "Ingredient");
        return this.self();
    }

    @NotNull
    @Override
    public BrewingRecipe<T> build() {
        return new BrewingRecipe<>(
                this.buildSettings(),
                this.requireSet(this.input, "Input"),
                this.requireSet(this.ingredient, "Ingredient")
        );
    }
}