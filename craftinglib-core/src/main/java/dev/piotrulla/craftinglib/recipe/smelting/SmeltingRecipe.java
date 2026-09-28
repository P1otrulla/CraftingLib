package dev.piotrulla.craftinglib.recipe.smelting;

import dev.piotrulla.craftinglib.recipe.AbstractRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeSettings;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Furnace / blast furnace / smoker / campfire recipe: one input item cooks into the result.
 *
 * @param <T> platform item type
 */
public final class SmeltingRecipe<T> extends AbstractRecipe<T> {

    private static final String COOKING_BOOK = "cooking";

    private final CookingType cookingType;
    private final T input;
    private final float experience;
    private final int cookingTime;

    /**
     * @param experience  experience dropped when the result is taken out
     * @param cookingTime cooking time in ticks
     */
    public SmeltingRecipe(
            @NotNull RecipeSettings<T> settings,
            @NotNull CookingType cookingType,
            @NotNull T input,
            float experience,
            int cookingTime
    ) {
        super(settings);
        this.requireRecipeBookCategory(RecipeBookCategory::isCookingCategory, COOKING_BOOK);
        this.cookingType = Objects.requireNonNull(cookingType, "cookingType");
        this.input = this.requireSingleItem(input, "Input");

        if (experience < 0) {
            throw new IllegalArgumentException("Experience of recipe '" + this.getKey() + "' cannot be negative: " + experience);
        }
        if (cookingTime < 1) {
            throw new IllegalArgumentException("Cooking time of recipe '" + this.getKey() + "' must be positive: " + cookingTime);
        }

        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @NotNull
    public CookingType getCookingType() {
        return this.cookingType;
    }

    /**
     * @return copy of the input item
     */
    @NotNull
    public T getInput() {
        return this.itemAdapter.copy(this.input);
    }

    public float getExperience() {
        return this.experience;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    public boolean matches(@Nullable T item) {
        return this.matchesIngredient(this.input, item);
    }
}