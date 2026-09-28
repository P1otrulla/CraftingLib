package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeKeys;
import dev.piotrulla.craftinglib.recipe.RecipeSettings;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Settings shared by every recipe builder. Platforms extend the concrete builders and add
 * convenience overloads (e.g. Bukkit {@code Material}).
 *
 * @param <T> platform item type
 * @param <R> built recipe type
 * @param <B> concrete builder type (for fluent chaining)
 */
public abstract class AbstractRecipeBuilder<T, R extends KeyedRecipe, B extends AbstractRecipeBuilder<T, R, B>> {

    protected final ItemAdapter<T> itemAdapter;
    protected final String key;

    private T result;
    private MatchMode matchMode = MatchMode.EXACT;
    private boolean replaceVanilla;
    private boolean discoverable = RecipeBookSettings.DEFAULT.isDiscoverable();
    private String recipeBookGroup = RecipeBookSettings.NO_GROUP;
    private RecipeBookCategory recipeBookCategory;

    protected AbstractRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        this.itemAdapter = Objects.requireNonNull(itemAdapter, "itemAdapter");
        this.key = RecipeKeys.requireValid(key);
    }

    @NotNull
    public B withResult(@NotNull T result) {
        this.result = this.requireItem(result, "Result");
        return this.self();
    }

    /**
     * Defaults to {@link MatchMode#EXACT}.
     */
    @NotNull
    public B withMatchMode(@NotNull MatchMode matchMode) {
        this.matchMode = Objects.requireNonNull(matchMode, "matchMode");
        return this.self();
    }

    /**
     * Crafting: other recipes with the same result type are removed and blocked.
     * Smelting: other recipes of the same station with the same input are removed.
     * Brewing: not supported.
     */
    @NotNull
    public B replaceVanillaRecipes(boolean replaceVanilla) {
        this.replaceVanilla = replaceVanilla;
        return this.self();
    }

    /**
     * Defaults to true: the recipe is unlocked in the recipe book of every player.
     * Only crafting and cooking recipes are shown in a recipe book.
     */
    @NotNull
    public B discoverable(boolean discoverable) {
        this.discoverable = discoverable;
        return this.self();
    }

    /**
     * Recipes with the same group are shown as one recipe book entry (like all wooden planks).
     * Supported by crafting, cooking and stonecutting recipes.
     */
    @NotNull
    public B withRecipeBookGroup(@NotNull String group) {
        this.recipeBookGroup = Objects.requireNonNull(group, "group");
        return this.self();
    }

    /**
     * Recipe book tab, must be a crafting or cooking category matching the recipe type.
     */
    @NotNull
    public B withRecipeBookCategory(@NotNull RecipeBookCategory category) {
        this.recipeBookCategory = Objects.requireNonNull(category, "category");
        return this.self();
    }

    @NotNull
    public abstract R build();

    @NotNull
    protected abstract B self();

    @NotNull
    protected RecipeSettings<T> buildSettings() {
        if (this.result == null) {
            throw new IllegalStateException("Result of recipe '" + this.key + "' is not set");
        }

        RecipeBookSettings recipeBook = new RecipeBookSettings(this.discoverable, this.recipeBookGroup, this.recipeBookCategory);
        return new RecipeSettings<>(this.itemAdapter, this.key, this.result, this.matchMode, this.replaceVanilla, recipeBook);
    }

    @NotNull
    protected T requireItem(T item, @NotNull String description) {
        if (this.itemAdapter.isEmpty(item)) {
            throw new IllegalArgumentException(description + " of recipe '" + this.key + "' cannot be null or empty");
        }

        return this.itemAdapter.copy(item);
    }

    @NotNull
    protected <V> V requireSet(V value, @NotNull String description) {
        if (value == null) {
            throw new IllegalStateException(description + " of recipe '" + this.key + "' is not set");
        }

        return value;
    }
}