package dev.piotrulla.craftinglib.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Teaches the core how to work with a platform item type.
 *
 * @param <T> platform item type
 */
public interface ItemAdapter<T> {

    /**
     * @return true for {@code null}, "air" and items with amount lower than 1
     */
    boolean isEmpty(@Nullable T item);

    int getAmount(@NotNull T item);

    /**
     * Compares type/data only. Amount is not part of this check.
     */
    boolean isSimilar(@NotNull T required, @NotNull T actual, @NotNull MatchMode matchMode);

    @NotNull
    T copy(@NotNull T item);
}
