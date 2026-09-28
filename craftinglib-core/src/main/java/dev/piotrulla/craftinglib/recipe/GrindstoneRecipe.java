package dev.piotrulla.craftinglib.recipe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Grindstone recipe: an input (optionally with a second item) turns into the result and drops experience.
 * Items may be put in either grindstone slot. Amounts of both items are consumed.
 * Grindstones have no vanilla recipes to replace, so {@code replaceVanilla} is rejected.
 *
 * @param <T> platform item type
 */
public final class GrindstoneRecipe<T> extends AbstractRecipe<T> {

    public static final int NO_EXPERIENCE = 0;

    private final T input;
    private final T secondInput;
    private final int experience;

    /**
     * @param secondInput item required in the other slot, {@code null} when the other slot must stay empty
     * @param experience  experience points given to the player taking the result
     */
    public GrindstoneRecipe(@NotNull RecipeSettings<T> settings, @NotNull T input, @Nullable T secondInput, int experience) {
        super(settings);
        this.rejectReplaceVanilla();
        this.rejectRecipeBook();
        this.input = this.requireItem(input, "Input");
        this.secondInput = secondInput == null ? null : this.requireItem(secondInput, "Second input");

        if (experience < NO_EXPERIENCE) {
            throw new IllegalArgumentException("Experience of recipe '" + this.getKey() + "' cannot be negative: " + experience);
        }
        this.experience = experience;
    }

    /**
     * @return copy of the input, its amount is consumed
     */
    @NotNull
    public T getInput() {
        return this.itemAdapter.copy(this.input);
    }

    /**
     * @return copy of the second input, its amount is consumed; empty when the other slot must stay empty
     */
    @NotNull
    public Optional<T> getSecondInput() {
        return this.secondInput == null ? Optional.empty() : Optional.of(this.itemAdapter.copy(this.secondInput));
    }

    public int getExperience() {
        return this.experience;
    }

    /**
     * Exact slot order: {@code first} is checked against the input, {@code second} against the second input.
     */
    public boolean matches(@Nullable T first, @Nullable T second) {
        boolean secondMatches = this.secondInput == null
                ? this.itemAdapter.isEmpty(second)
                : this.matchesIngredient(this.secondInput, second);

        return secondMatches && this.matchesIngredient(this.input, first);
    }
}
