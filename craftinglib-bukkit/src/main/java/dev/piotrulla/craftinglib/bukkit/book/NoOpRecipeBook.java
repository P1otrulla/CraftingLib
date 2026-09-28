package dev.piotrulla.craftinglib.bukkit.book;

import org.jetbrains.annotations.NotNull;

/**
 * For servers without recipe discovery (before 1.13) and stations without a recipe book.
 */
public final class NoOpRecipeBook implements RecipeBook {

    public static final NoOpRecipeBook INSTANCE = new NoOpRecipeBook();

    private NoOpRecipeBook() {
    }

    @Override
    public void publish(@NotNull String key) {
        // nothing to unlock
    }

    @Override
    public void retract(@NotNull String key) {
        // nothing to lock
    }
}
