package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public interface CraftingRecipeManager<T> {

    void addRecipe(@NotNull CraftingRecipe<T> recipe);

    boolean removeRecipe(@NotNull String recipeId);

    boolean removeRecipe(@NotNull CraftingRecipe<T> recipe);

    @Nullable
    CraftingRecipe<T> findRecipe(@NotNull String recipeId);

    @Nullable
    CraftingRecipe<T> findMatchingRecipe(@NotNull List<CraftingRecipeIngredient<T>> input);

    @NotNull
    Collection<CraftingRecipe<T>> getAllRecipes();

    int getRecipeCount();
}