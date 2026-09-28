package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Settings shared by every recipe type.
 *
 * @param <T> platform item type
 */
public final class RecipeSettings<T> {

    private final ItemAdapter<T> itemAdapter;
    private final String key;
    private final T result;
    private final MatchMode matchMode;
    private final boolean replaceVanilla;
    private final RecipeBookSettings recipeBook;

    public RecipeSettings(
            @NotNull ItemAdapter<T> itemAdapter,
            @NotNull String key,
            @NotNull T result,
            @NotNull MatchMode matchMode,
            boolean replaceVanilla
    ) {
        this(itemAdapter, key, result, matchMode, replaceVanilla, RecipeBookSettings.DEFAULT);
    }

    public RecipeSettings(
            @NotNull ItemAdapter<T> itemAdapter,
            @NotNull String key,
            @NotNull T result,
            @NotNull MatchMode matchMode,
            boolean replaceVanilla,
            @NotNull RecipeBookSettings recipeBook
    ) {
        this.itemAdapter = Objects.requireNonNull(itemAdapter, "itemAdapter");
        this.key = RecipeKeys.requireValid(key);
        this.matchMode = Objects.requireNonNull(matchMode, "matchMode");
        this.replaceVanilla = replaceVanilla;
        this.recipeBook = Objects.requireNonNull(recipeBook, "recipeBook");

        if (itemAdapter.isEmpty(result)) {
            throw new IllegalArgumentException("Result of recipe '" + key + "' cannot be null or empty");
        }
        this.result = itemAdapter.copy(result);
    }

    @NotNull
    public ItemAdapter<T> getItemAdapter() {
        return this.itemAdapter;
    }

    @NotNull
    public String getKey() {
        return this.key;
    }

    @NotNull
    public T getResult() {
        return this.itemAdapter.copy(this.result);
    }

    @NotNull
    public MatchMode getMatchMode() {
        return this.matchMode;
    }

    public boolean shouldReplaceVanilla() {
        return this.replaceVanilla;
    }

    @NotNull
    public RecipeBookSettings getRecipeBook() {
        return this.recipeBook;
    }
}
