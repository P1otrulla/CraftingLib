package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Used when the platform cannot handle a recipe type: registration fails fast with the given reason.
 *
 * @param <R> recipe type
 */
public final class UnsupportedRecipeRegistrar<R extends KeyedRecipe> implements PlatformRecipeRegistrar<R> {

    private final String reason;

    public UnsupportedRecipeRegistrar(@NotNull String reason) {
        this.reason = Objects.requireNonNull(reason, "reason");
    }

    @Override
    public void register(@NotNull R recipe) {
        throw new UnsupportedOperationException(this.reason + " (recipe '" + recipe.getKey() + "')");
    }

    @Override
    public void unregister(@NotNull R recipe) {
        // nothing was registered
    }
}