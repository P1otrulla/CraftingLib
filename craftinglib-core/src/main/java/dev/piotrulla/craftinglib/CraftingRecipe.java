package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CraftingRecipe<T> {

    @NotNull
    String name();

    @NotNull
    String id();

    @NotNull
    List<CraftingRecipeIngredient<T>> ingredients();

    @NotNull CraftingRecipeIngredient<T> result();

    boolean matches(@NotNull List<CraftingRecipeIngredient<T>> input);

    boolean requiresExactMatch();
}
