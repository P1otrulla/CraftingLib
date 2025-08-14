package dev.piotrulla.craftinglib.action;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RecipeActionHandler<E extends RecipeAction> {

    /**
     * Handles a crafting action.
     *
     * @param event the action to handle
     */
    void handle(@NotNull E event);
}
