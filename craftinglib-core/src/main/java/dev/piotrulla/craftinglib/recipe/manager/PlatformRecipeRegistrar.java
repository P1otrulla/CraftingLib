package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Pushes recipes into the platform's own recipe system (e.g. Bukkit server recipes).
 *
 * @param <R> recipe type
 */
public interface PlatformRecipeRegistrar<R extends KeyedRecipe> {

    void register(@NotNull R recipe);

    void unregister(@NotNull R recipe);
}