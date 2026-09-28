package dev.piotrulla.craftinglib.bukkit.book;

import org.jetbrains.annotations.NotNull;

/**
 * Unlocks registered recipes in the client recipe book of players.
 */
public interface RecipeBook {

    /**
     * Unlocks the recipe for online players and everyone who joins later. Idempotent.
     */
    void publish(@NotNull String key);

    /**
     * Locks the recipe again for online players and stops unlocking it on join. Idempotent.
     */
    void retract(@NotNull String key);
}
