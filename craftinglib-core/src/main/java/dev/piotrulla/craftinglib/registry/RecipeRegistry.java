package dev.piotrulla.craftinglib.registry;

import dev.piotrulla.craftinglib.Recipe;
import org.jetbrains.annotations.NotNull;

public interface RecipeRegistry<T> {

    /**
     * Adds a recipe to the platform's recipe system.
     *
     * @param recipe the recipe to add
     * @throws RegistryException if the recipe cannot be added
     */
    void addRecipe(@NotNull Recipe<T> recipe) throws RegistryException;

    /**
     * Removes a recipe from the platform's recipe system.
     *
     * @param recipe the recipe to remove
     * @throws RegistryException if the recipe cannot be removed
     */
    void removeRecipe(@NotNull Recipe<T> recipe) throws RegistryException;

    /**
     * Removes all recipes that produce the given result item.
     * This is typically used to remove vanilla recipes when replacing them.
     *
     * @param result the result item whose recipes should be removed
     * @throws RegistryException if recipes cannot be removed
     */
    void removeRecipesByResult(@NotNull T result) throws RegistryException;

    /**
     * Checks if the registry supports the given recipe type.
     *
     * @param recipe the recipe to check
     * @return true if the recipe is supported, false otherwise
     */
    boolean supportsRecipe(@NotNull Recipe<T> recipe);

    /**
     * Gets the number of recipes currently registered.
     *
     * @return the number of registered recipes
     */
    int getRegisteredRecipeCount();
}