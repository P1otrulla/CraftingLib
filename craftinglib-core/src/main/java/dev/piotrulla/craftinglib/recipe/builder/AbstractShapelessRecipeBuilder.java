package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Every call to {@link #withIngredient(Object)} adds one grid slot.
 * Recipes with up to 4 ingredients are also craftable in the player inventory.
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractShapelessRecipeBuilder<T, B extends AbstractShapelessRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, ShapelessCraftingRecipe<T>, B> {

    private final List<T> ingredients = new ArrayList<>();

    protected AbstractShapelessRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    @NotNull
    public B withIngredient(@NotNull T ingredient) {
        if (this.ingredients.size() >= ShapelessCraftingRecipe.MAX_INGREDIENTS) {
            throw new IllegalStateException("Recipe '" + this.key + "' cannot have more than "
                    + ShapelessCraftingRecipe.MAX_INGREDIENTS + " ingredients");
        }

        this.ingredients.add(this.requireItem(ingredient, "Ingredient"));
        return this.self();
    }

    @NotNull
    @Override
    public ShapelessCraftingRecipe<T> build() {
        if (this.ingredients.isEmpty()) {
            throw new IllegalStateException("Recipe '" + this.key + "' has no ingredients");
        }

        return new ShapelessCraftingRecipe<>(this.buildSettings(), this.ingredients);
    }
}
