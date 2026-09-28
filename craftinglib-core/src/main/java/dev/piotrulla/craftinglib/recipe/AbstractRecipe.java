package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Common state and item comparison for every recipe type.
 *
 * @param <T> platform item type
 */
public abstract class AbstractRecipe<T> implements CustomRecipe<T> {

    private static final int SINGLE_ITEM = 1;

    protected final ItemAdapter<T> itemAdapter;
    private final RecipeSettings<T> settings;

    protected AbstractRecipe(@NotNull RecipeSettings<T> settings) {
        this.settings = Objects.requireNonNull(settings, "settings");
        this.itemAdapter = settings.getItemAdapter();
    }

    @NotNull
    @Override
    public final String getKey() {
        return this.settings.getKey();
    }

    /**
     * @return copy of the result item
     */
    @NotNull
    @Override
    public final T getResult() {
        return this.settings.getResult();
    }

    @NotNull
    @Override
    public final MatchMode getMatchMode() {
        return this.settings.getMatchMode();
    }

    @Override
    public final boolean shouldReplaceVanilla() {
        return this.settings.shouldReplaceVanilla();
    }

    @NotNull
    @Override
    public final RecipeBookSettings getRecipeBook() {
        return this.settings.getRecipeBook();
    }

    protected boolean matchesIngredient(@NotNull T required, @Nullable T actual) {
        return !this.itemAdapter.isEmpty(actual)
                && this.itemAdapter.isSimilar(required, actual, this.getMatchMode())
                && this.itemAdapter.getAmount(actual) >= this.itemAdapter.getAmount(required);
    }

    @NotNull
    protected T requireItem(T item, @NotNull String description) {
        if (this.itemAdapter.isEmpty(item)) {
            throw new IllegalArgumentException(description + " of recipe '" + this.getKey() + "' cannot be null or empty");
        }

        return this.itemAdapter.copy(item);
    }

    /**
     * For stations that always consume exactly one item (furnace input, brewing ingredient...).
     */
    @NotNull
    protected T requireSingleItem(T item, @NotNull String description) {
        T copy = this.requireItem(item, description);
        if (this.itemAdapter.getAmount(copy) != SINGLE_ITEM) {
            throw new IllegalArgumentException(description + " of recipe '" + this.getKey() + "' must have amount "
                    + SINGLE_ITEM + ", got: " + this.itemAdapter.getAmount(copy));
        }

        return copy;
    }

    /**
     * For recipe types that have no vanilla counterpart to replace.
     */
    protected final void rejectReplaceVanilla() {
        if (this.shouldReplaceVanilla()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " '" + this.getKey()
                    + "' cannot replace vanilla recipes");
        }
    }

    /**
     * For recipe types whose recipe book has only some of the {@link RecipeBookCategory categories}.
     */
    protected final void requireRecipeBookCategory(@NotNull Predicate<RecipeBookCategory> supported, @NotNull String bookName) {
        Optional<RecipeBookCategory> category = this.getRecipeBook().getCategory();
        if (category.isPresent() && !supported.test(category.get())) {
            throw new IllegalArgumentException("Recipe '" + this.getKey() + "': category " + category.get()
                    + " is not available in the " + bookName + " recipe book");
        }
    }

    /**
     * For recipe types that have a recipe book group but no categories.
     */
    protected final void rejectRecipeBookCategory() {
        Optional<RecipeBookCategory> category = this.getRecipeBook().getCategory();
        if (category.isPresent()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " '" + this.getKey()
                    + "' has no recipe book categories, got: " + category.get());
        }
    }

    /**
     * For recipe types that are not shown in any recipe book.
     */
    protected final void rejectRecipeBook() {
        this.rejectRecipeBookCategory();
        if (this.getRecipeBook().hasGroup()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " '" + this.getKey()
                    + "' is not shown in a recipe book, so it cannot have a recipe book group");
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{key=" + this.getKey() + '}';
    }
}