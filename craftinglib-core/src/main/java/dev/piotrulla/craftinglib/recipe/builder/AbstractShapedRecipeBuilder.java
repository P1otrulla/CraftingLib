package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.pattern.CraftingPattern;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <pre>
 * builder.withPattern("DDD", " S ", " S ")
 *     .withIngredient('D', diamond)
 *     .withIngredient('S', stick)
 *     .withResult(pickaxe)
 *     .build();
 * </pre>
 * Patterns up to 2x2 are also craftable in the player inventory.
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractShapedRecipeBuilder<T, B extends AbstractShapedRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, ShapedCraftingRecipe<T>, B> {

    private final Map<Character, T> ingredients = new LinkedHashMap<>();
    private CraftingPattern pattern;
    private boolean mirrored = true;

    protected AbstractShapedRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    /**
     * @param rows 1-3 rows of 1-3 characters each, space = empty slot
     */
    @NotNull
    public B withPattern(@NotNull String... rows) {
        this.pattern = CraftingPattern.of(rows);
        return this.self();
    }

    @NotNull
    public B withIngredient(char symbol, @NotNull T ingredient) {
        if (symbol == CraftingPattern.EMPTY_SYMBOL) {
            throw new IllegalArgumentException("Space is reserved for empty slots and cannot be an ingredient symbol");
        }

        this.ingredients.put(symbol, this.requireItem(ingredient, "Ingredient '" + symbol + "'"));
        return this.self();
    }

    /**
     * Whether a horizontally mirrored layout is accepted too. Defaults to true (vanilla behaviour).
     */
    @NotNull
    public B withMirroring(boolean mirrored) {
        this.mirrored = mirrored;
        return this.self();
    }

    @NotNull
    @Override
    public ShapedCraftingRecipe<T> build() {
        if (this.pattern == null) {
            throw new IllegalStateException("Pattern of recipe '" + this.key + "' is not set");
        }

        return new ShapedCraftingRecipe<>(this.buildSettings(), this.pattern, this.ingredients, this.mirrored);
    }
}
