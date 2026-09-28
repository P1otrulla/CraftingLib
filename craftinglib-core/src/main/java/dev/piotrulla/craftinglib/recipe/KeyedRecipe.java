package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import org.jetbrains.annotations.NotNull;

/**
 * Anything registered in a {@link RecipeManager}.
 */
public interface KeyedRecipe {

    /**
     * @return unique key, valid as a Minecraft namespaced key ({@code [a-z0-9/._-]})
     */
    @NotNull
    String getKey();
}