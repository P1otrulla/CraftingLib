package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * For recipe types handled only by a platform listener, with nothing to register in the platform itself.
 *
 * @param <R> recipe type
 */
public final class NoOpRecipeRegistrar<R extends KeyedRecipe> implements PlatformRecipeRegistrar<R> {

    @Override
    public void register(@NotNull R recipe) {
        // handled by a listener
    }

    @Override
    public void unregister(@NotNull R recipe) {
        // handled by a listener
    }
}