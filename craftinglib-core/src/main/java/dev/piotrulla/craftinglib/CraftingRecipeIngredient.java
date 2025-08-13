package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CraftingRecipeIngredient<T> {

    @NotNull
    T item();

    int amount();

    @Nullable
    Object metaData();

    boolean matches(@NotNull CraftingRecipeIngredient<T> other, boolean exactMatch);
}