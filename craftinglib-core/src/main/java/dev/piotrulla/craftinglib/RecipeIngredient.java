package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RecipeIngredient<T> {

    @NotNull
    T item();

    int amount();

    @Nullable
    Object metaData();

    boolean matches(@NotNull RecipeIngredient<T> other, boolean exactMatch);
}