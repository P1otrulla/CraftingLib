package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Registry of custom recipes of one type.
 *
 * @param <R> recipe type
 */
public interface RecipeManager<R extends KeyedRecipe> {

    /**
     * Registers the recipe. A recipe with the same key is replaced.
     */
    void registerRecipe(@NotNull R recipe);

    /**
     * @return true when a recipe with the key existed
     */
    boolean unregisterRecipe(@NotNull String key);

    void unregisterAll();

    @NotNull
    Optional<R> findRecipe(@NotNull String key);

    /**
     * @return unmodifiable snapshot, in registration order
     */
    @NotNull
    Collection<R> getRecipes();
}