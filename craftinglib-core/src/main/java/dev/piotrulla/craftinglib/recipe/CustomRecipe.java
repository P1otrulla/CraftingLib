package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Settings every recipe type shares, see {@link RecipeSettings}.
 *
 * @param <T> platform item type
 */
public interface CustomRecipe<T> extends KeyedRecipe {

    /**
     * @return copy of the result item
     */
    @NotNull
    T getResult();

    @NotNull
    MatchMode getMatchMode();

    /**
     * @return true when conflicting vanilla recipes should be removed/blocked
     */
    boolean shouldReplaceVanilla();

    @NotNull
    RecipeBookSettings getRecipeBook();
}
