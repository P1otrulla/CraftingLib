package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Recipe<T> {

    @NotNull
    String name();

    @NotNull
    String id();

    @NotNull
    List<RecipeIngredient<T>> ingredients();

    @NotNull RecipeIngredient<T> result();

    boolean matches(@NotNull List<RecipeIngredient<T>> input);

    boolean requiresExactMatch();
}
