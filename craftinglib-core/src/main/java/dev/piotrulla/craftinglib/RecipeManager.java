package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface RecipeManager<T> {

    void addRecipe(@NotNull Recipe<T> recipe);

    boolean removeRecipe(@NotNull String recipeId);

    boolean removeRecipe(@NotNull Recipe<T> recipe);

    @Nullable
    Recipe<T> findRecipe(@NotNull String recipeId);

    @Nullable
    Recipe<T> findMatchingRecipe(@NotNull List<RecipeIngredient<T>> input);

    @NotNull
    Collection<Recipe<T>> getAllRecipes();

    int getRecipeCount();
}